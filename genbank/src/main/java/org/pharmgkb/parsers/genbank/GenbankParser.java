package org.pharmgkb.parsers.genbank;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.pharmgkb.parsers.BadDataFormatException;
import org.pharmgkb.parsers.MultilineParser;
import org.pharmgkb.parsers.genbank.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import java.io.UncheckedIOException;
import java.lang.invoke.MethodHandles;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * <strong>WARNING: This is experimental.</strong>
 * @author Douglas Myers-Turnbull
 */
@NotThreadSafe
public class GenbankParser implements MultilineParser<GenbankAnnotation> {

	private static Pattern pattern = Pattern.compile("/([^=]+)=(\"?[^\"]+\"?)");
	private static final long sf_logEvery = 10000;
	private static final Logger sf_logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private AtomicLong m_lineNumber = new AtomicLong(0L);
	private String m_currentLine = "";

	@Nonnull
	@Override
	public Stream<GenbankAnnotation> parseAll(@Nonnull Stream<String> stream) throws UncheckedIOException, BadDataFormatException {
		return stream.flatMap(this);
	}

	@Nonnull
	@Override
	public Stream<GenbankAnnotation> apply(@Nonnull String line) {
		try {
			m_lineNumber.addAndGet(1);
			if (line.isEmpty()) return Stream.empty();
			if (line.startsWith(" ") || m_currentLine.isEmpty()) {
				m_currentLine += line + "\n";
				return Stream.empty();
			}  // or ends with //
			GenbankAnnotation annotation = parse(m_currentLine);
			m_currentLine = line + "\n";
			return Stream.of(annotation);
		} catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
			throw new BadDataFormatException("Couldn't parse line #" + m_lineNumber, e);
		} catch (RuntimeException e) {
			// this is a little weird, but it's helpful
			// not that we're not throwing a BadDataFormatException because we don't expect AIOOB, e.g.
			e.addSuppressed(new RuntimeException("Unexpectedly failed to parse line " + m_lineNumber));
			throw e;
		}
	}

	private enum EntryType {
		TOP_LEVEL(0), MID_LEVEL(2), LOW_LEVEL(12), FEATURE_LEVEL(5), FEATURE_PROPERTY_LEVEL(21);
		public int indentation;
		EntryType(int indentation) {
			this.indentation = indentation;
		}
	}

	private GenbankAnnotation parse(String line) {
		Entry e = Entry.extract(line);
		switch (e.directive) {
			case "LOCUS" -> {
				List<String> parts = e.splitAndTrim();
				String[] dateChars = parts.get(5).split("-");
				dateChars[1] = Character.toUpperCase(dateChars[1].charAt(0)) + dateChars[1].substring(1).toLowerCase();
				LocalDate date = LocalDate.parse(String.join("-", dateChars), DateTimeFormatter.ofPattern("dd-MMM-yyyy"));
				return new LocusAnnotation(parts.get(0), parts.get(1), parts.get(3), parts.get(4), date);
			} case "DEFINITION" -> {
				return new DefinitionAnnotation(e.trim());
			} case "ACCESSION" -> {
				return new AccessionAnnotation(e.trim());
			} case "VERSION" -> {
				List<String> parts = e.splitAndTrim();
				return new VersionAnnotation(parts.get(0), parts.get(1));
			} case "KEYWORDS" -> {
				List<String> parts = e.trim().equals(".") ? Collections.emptyList() : e.splitAndTrim();
				return new KeywordsAnnotation(ImmutableList.copyOf(parts));
			} case "COMMENT" -> {
				return new CommentAnnotation(e.trim());
			} case "SOURCE" -> {
				return parseSource(e);
			} case "REFERENCE" -> {
				return parseReference(e);
			} case "FEATURES" -> {
				return parseFeatures(e);
			} case "ORIGIN" -> {
				return parseOrigin(e);
			}
			default -> throw new IllegalArgumentException("Invalid directive " + e.directive);
		}
	}

	private OriginAnnotation parseOrigin(Entry e) {
		StringBuilder builder = new StringBuilder();
		for (String s : e.textAsTrimmedLines()) {
			int ignore = s.split(" +")[0].length();
			builder.append(s.substring(ignore).replace(" ", ""));
		}
		return new OriginAnnotation(e.header, builder.toString());
	}

	private SourceAnnotation parseSource(Entry e) {
		LinkedHashMap<String, Entry> asMap = parseSubEntriesAsMap(e, 2);
		List<String> organismT = asMap.get("ORGANISM").textAfterDirectiveAsLines();
		String organism = organismT.get(0);
		List<String> lineage = organismT.stream()
				.skip(1)
				.flatMap(s -> Arrays.stream(s.split(";")))
				.map(String::trim)
				.collect(Collectors.toList());
		return new SourceAnnotation(
				asMap.get("SOURCE").trim(),
				organism,
				ImmutableList.copyOf(lineage)
		);
	}

	private ReferenceAnnotation parseReference(Entry e) {
		Map<String, Entry> asMap = parseSubEntriesAsMap(e, EntryType.MID_LEVEL.indentation);
		return new ReferenceAnnotation(
				e.header,
				Optional.ofNullable(asMap.get("AUTHORS")).map(Entry::trim),
				Optional.ofNullable(asMap.get("CONSORTIUM")).map(Entry::trim),
				Optional.ofNullable(asMap.get("TITLE")).map(Entry::trim),
				Optional.ofNullable(asMap.get("JOURNAL")).map(Entry::trim),
				Optional.ofNullable(asMap.get("PUBMED")).map(e2 -> Integer.parseInt(e2.trim())),
				Optional.ofNullable(asMap.get("REMARK")).map(Entry::trim)
		);
	}

	private FeaturesAnnotation parseFeatures(Entry e) {
		List<Entry> entries = parseSubEntries(e, EntryType.FEATURE_LEVEL.indentation);
		List<GenbankFeature> features = entries.stream()
				.filter(e2 -> !e2.directive.equals("FEATURES"))
				.map(this::parseFeature)
				.collect(Collectors.toList());
		return new FeaturesAnnotation(e.header, ImmutableList.copyOf(features));
	}

	private GenbankFeature parseFeature(Entry e) {
		String kind = e.directive;
		GenbankSequenceRange range = new GenbankSequenceRange(e.header);
		LinkedHashMap<String, String> properties = new LinkedHashMap<>();
		List<String> extraLines = new ArrayList<>();
		for (String line : e.textAsTrimmedLines()) {
			if (line.startsWith("/")) {
				Matcher match = pattern.matcher(line);
				if (!match.matches()) throw new AssertionError("No match for " + line);
				properties.put(match.group(1), match.group(2));
			} else {
				extraLines.add(line);
			}
		}
		return new GenbankFeature(kind, range, ImmutableMap.copyOf(properties), ImmutableList.copyOf(extraLines));
	}

	private LinkedHashMap<String, Entry> parseSubEntriesAsMap(Entry e, int directiveIndents) {
		List<Entry> entries = parseSubEntries(e, EntryType.MID_LEVEL.indentation);
		LinkedHashMap<String, Entry> asMap = new LinkedHashMap<>();
		for (Entry e2 : entries) asMap.put(e2.directive, e2);
		return asMap;
	}

	private List<Entry> parseSubEntries(Entry e, int directiveIndents) {
		List<Entry> entries = new ArrayList<>();
		StringBuilder current = new StringBuilder();
		for (String line : e.fullTextAsLines()) {
			int indent = nSpaces(line);
			if (indent > directiveIndents) {
				current.append(line).append("\n");
			} else {
				if (current.length() > 0) {
					entries.add(Entry.extract(current.toString()));
				}
				current = new StringBuilder(line + "\n");
			}
		}
		if (current.length() > 0) {
			entries.add(Entry.extract(current.toString()));
		}
		return entries;
	}

	@Nonnegative
	@Override
	public long nLinesProcessed() {
		return m_lineNumber.get();
	}

	private static int nSpaces(String line) {
		int i = 0;
		for (char c : line.toCharArray()) {
			if (c != ' ') return i;
			i++;
		}
		throw new IllegalArgumentException("The line is blank");
	}

	private static class Entry {

		String directive;
		String header;
		String text;

		public Entry(String directive, String header, String text) {
			this.directive = directive;
			this.header = header;
			this.text = text;
		}

		public List<String> textAsTrimmedLines() {
			return Arrays.stream(text.trim().split("\n")).skip(1).map(String::trim).collect(Collectors.toList());
		}
		public List<String> fullTextAsLines() {
			return Arrays.stream(text.split("\n")).collect(Collectors.toList());
		}
		public List<String> textAfterDirectiveAsLines() {
			return Arrays.stream(afterDirective().split("\n")).collect(Collectors.toList());
		}
		public String afterDirective() {
			return text.substring(directive.length()).trim();
		}
		public String trim() {
			return Arrays.stream(afterDirective().split("\n")).map(String::trim).collect(Collectors.joining("\n"));
		}
		public List<String> splitAndTrim() {
			return Arrays.stream(afterDirective().split("\n?\\s+\n?")).map(String::trim).collect(Collectors.toList());
		}

		public static Entry extract(String line) {
			line = line.trim();
			String directive = getDirective(line);
			String header = line.split("\n")[0].substring(directive.length()).trim();
			return new Entry(directive, header, line);
		}

		private static String getDirective(String line) {
			StringBuilder builder = new StringBuilder();
			for (char c : line.toCharArray()) {
				if (c == ' ' || c == '\n') return builder.toString();
				builder.append(c);
			}
			throw new IllegalArgumentException("The line is blank");
		}

		@Override
		public String toString() {
			return MoreObjects.toStringHelper(this)
					.add("directive", directive)
					.add("header", header)
					.add("text", text)
					.toString();
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) return true;
			if (o == null || getClass() != o.getClass()) return false;
			Entry entry = (Entry) o;
			return Objects.equals(directive, entry.directive) &&
					Objects.equals(header, entry.header) &&
					Objects.equals(text, entry.text);
		}

		@Override
		public int hashCode() {
			return Objects.hash(directive, header, text);
		}
	}

	@Override
	public String toString() {
		return "GenbankParser{" +
				"lineNumber=" + m_lineNumber.get() +
				", currentLine='" + m_currentLine + '\'' +
				'}';
	}
}
