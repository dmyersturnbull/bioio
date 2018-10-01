package org.pharmgkb.parsers.gff.gff3;

import org.pharmgkb.parsers.BadDataFormatException;
import org.pharmgkb.parsers.LineParser;
import org.pharmgkb.parsers.gff.CdsPhase;
import org.pharmgkb.parsers.gff.GffStrand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Reads GFF3 data lines, ignoring comments and metadata.
 *
 * Calling {@link #parseAll(Stream)} or {@link #collectAll(File)} will filter comment and metadata lines.
 *
 * <h4>Example usages:</h4>
 * <code>
 *     // store in a list
 *     List<Gff3Feature> features = new Gff3Parser().collectAll(file);
 * </code>
 * <code>
 *     // get a stream of the ranges
 *     new BedParser().parseAll(file)
 *          .map(f -> f.getStart() + "-" + f.getEnd());
 * </code>
 *
 * <h4>Important notes:</h4>
 * <ul>
 * <li>This class does not support the ##FASTA directive, which allows FASTA lines to follow GFF3. Such lines
 * will cause the parser to throw a {@link BadDataFormatException}.</li>
 * <li>For consistency across the whole project as well as mathematical convenience, <em>GFF3 coordinates are converted
 * to 0-based</em>.</li>
 * <li>The writer escapes strings as the specification requires, and the parser unescapes them. See {@link Gff3Escapers}
 * for more details.</li>
 * </ul>
 *
 * <h4>Assumptions made</h4>
 * This class follows the <a href="http://www.sequenceontology.org/gff3.shtml">Sequence Ontology specification</a>.
 * Because the specification is not completely clear, this package makes a few assumptions:
 * <ul>
 *     <li>The format is <em>case-insensitive, except for attributes IDs</em>.</li>
 *     <li>Coordinate system ID (sequence ID), type, start, and end are all required.
 *     <a href="http://gmod.org/wiki/GFF3">This page on GFF3</a> seems to interpret the specification this way.</li>
 * </ul>
 *
 * @author Douglas Myers-Turnbull
 */
@ThreadSafe
public class Gff3Parser implements LineParser<Gff3Feature> {

	private static final long sf_logEvery = 10000;
	private static final Pattern sf_comma = Pattern.compile(",");
	private static final Pattern sf_semicolon = Pattern.compile(";");
	private static final Pattern sf_tab = Pattern.compile("\t");
	private static final Pattern sf_equals = Pattern.compile("=");

	private static final Logger sf_logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private AtomicLong m_lineNumber = new AtomicLong(0L);

	@Nonnull
	@Override
	public Stream<Gff3Feature> parseAll(@Nonnull Stream<String> stream) throws IOException, BadDataFormatException {
		return stream.filter(s -> !s.startsWith("#")).map(this);
	}

	@Override
	@Nonnull
	public Gff3Feature apply(@Nonnull String line) throws BadDataFormatException {

		if (m_lineNumber.incrementAndGet() % sf_logEvery == 0) {
			sf_logger.debug("Reading line #{}", m_lineNumber);
		}

		final String[] parts = sf_tab.split(line);
		try {

			final String coordinateSystemId = parts[0];

			final Optional<String> source = parts[1].equals(".")?
					Optional.empty()
					: Optional.of(parts[1]);

			final String type = parts[2];
			final long start = Long.parseLong(parts[3]) - 1;
			final long end = Long.parseLong(parts[4]) - 1;

			final Optional<BigDecimal> score = parts[5].equals(".")?
					Optional.empty()
					: Optional.of(new BigDecimal(parts[5]));

			if (!GffStrand.lookupBySymbol(parts[6]).isPresent()) {
				throw new IllegalArgumentException("Strand " + parts[6] + " is unrecognized");
			}
			final GffStrand strand = GffStrand.lookupBySymbol(parts[6]).get();

			final Optional<CdsPhase> phase;
			switch (parts[7]) {
				case ".":
					phase = Optional.empty();
					break;
				case "0":
					phase = Optional.of(CdsPhase.ZERO);
					break;
				case "1":
					phase = Optional.of(CdsPhase.ONE);
					break;
				case "2":
					phase = Optional.of(CdsPhase.TWO);
					break;
				default:
					throw new IllegalArgumentException("Phase " + parts[7] + " is unrecognized");
			}

			Map<String, List<String>> attributes = stringToMap(parts[8]);

			String escaped = Gff3Escapers.COORDINATE_SYSTEM_IDS.unescape(coordinateSystemId);
			Gff3Feature.Builder builder = new Gff3Feature.Builder(
					escaped,
					Gff3Escapers.FIELDS.unescape(type),
					start, end
			);
			builder.setSource(Gff3Escapers.FIELDS.unescape(source));
			builder.setScore(score);
			builder.setStrand(strand);
			builder.setPhase(phase);
			builder.putAttributes(attributes);

			return builder.build();

		} catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
			throw new BadDataFormatException("Bad data format on line #" + m_lineNumber.get()
					                                 + "; line is [[[" + line + "]]]", e);
		} catch (RuntimeException e) {
			// this is a little weird, but it's helpful
			// not that we're not throwing a BadDataFormatException because we don't expect AIOOB, e.g.
			e.addSuppressed(new RuntimeException("Unexpectedly failed to parse line " + m_lineNumber));
			throw e;
		}
	}

	@Nonnull
	private static Map<String, List<String>> stringToMap(@Nonnull String string) {
		if (".".equals(string)) {
			return Collections.emptyMap();
		}
		Map<String, List<String>> map = new HashMap<>();
		String[] parts = sf_semicolon.split(string);
		for (String part : parts) {
			String[] v = sf_equals.split(part);
			if (v.length != 2) {
				throw new IllegalArgumentException("Bad attribute " + part + " for map " + string);
			}
			String key = Gff3Escapers.FIELDS.unescape(v[0]);
			List<String> values = Arrays.stream(sf_comma.split(v[1]))
					.map(Gff3Escapers.FIELDS::unescape)
					.collect(Collectors.toList());
			map.put(key, values);
		}
		return map;
	}

	@Nonnegative
	@Override
	public long nLinesProcessed() {
		return m_lineNumber.get();
	}

	@Override
	public String toString() {
		return "Gff3Parser{" +
				"lineNumber=" + m_lineNumber.get() +
				'}';
	}
}
