package org.pharmgkb.parsers.turtle;

import org.pharmgkb.parsers.BadDataFormatException;
import org.pharmgkb.parsers.MultilineParser;
import org.pharmgkb.parsers.turtle.model.Node;
import org.pharmgkb.parsers.turtle.model.Prefix;
import org.pharmgkb.parsers.turtle.model.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;


/**
 * @author Douglas Myers-Turnbull
 */
@NotThreadSafe
public class TurtleParser implements MultilineParser<Triple> {

	private static final long sf_logEvery = 10000;
	private static final Pattern sf_prefixPattern =
			Pattern.compile("@prefix[ \t]+([A-Za-z0-9\\-_]+):[ \t]+<([^>]+)>[ \t]*\\.");
	// TODO This does not handle escapes or > or " inside quotes
	// TODO Also requires ^^ before @
	private static final Pattern sf_nodePattern =
			Pattern.compile("[<\"]?([^<\"]+)[>\"]?(?:@([A-Za-z0-9\\-_:]+))?(?:\\^{2}([A-Za-z0-9\\-_:]+))?");
	private static final Pattern sf_xPattern =
			Pattern.compile("([<\"]?(?:[^<\"]+)[>\"]?(?:@(?:[A-Za-z0-9\\-_:]+))?(?:\\^{2}(?:[A-Za-z0-9\\-_:]+))?)");
	private static final Pattern sf_triplePattern =
			Pattern.compile("^[ ]*" + sf_xPattern.pattern() + "[ ]+" + sf_xPattern + "[ ]+" + sf_xPattern + "[ ]*[;.]$");
	private static final Pattern sf_doublePattern =
			Pattern.compile("^[ ]*" + sf_xPattern + "[ ]+" + sf_xPattern + "[ ]*[;.]$");

	private static final Logger sf_logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final boolean m_usePrefixes;
	private final AtomicLong m_lineNumber;
	private final Map<String, Prefix> m_prefixes;
	private final AtomicReference<Node> m_subject;

	public TurtleParser() {
		this(true);
	}
	public TurtleParser(boolean usePrefixes) {
		this.m_usePrefixes = usePrefixes;
		this.m_prefixes = new HashMap<>();
		this.m_subject = new AtomicReference<>();
		m_lineNumber = new AtomicLong(0L);
	}

	@Nonnull
	@Override
	public Stream<Triple> parseAll(@Nonnull Stream<String> stream) throws BadDataFormatException {
		return stream.flatMap(this);
	}

	@Nonnull
	@Override
	public Stream<Triple> apply(@Nonnull String line) {
		try {
			m_lineNumber.addAndGet(1);
			line = line.trim(); // NOTE!
			if (line.isEmpty() || line.startsWith("#")) {
				return Stream.empty();
			}
			if (line.startsWith("@prefix")) {
				if (m_usePrefixes) {
					Prefix prefix = parsePrefix(line);
					m_prefixes.put(prefix.getPrefix(), prefix);
				}
				return Stream.empty();
			}
			@Nullable Node subject = m_subject.get();
			@Nonnull Triple triple = (subject == null) ? parseTriple(line) : parsePartTriple(line, subject);
			if (line.endsWith(";")) {
				m_subject.set(triple.getSubject());
			} else if (line.endsWith(".")) {
				m_subject.set(null);
			} else {
				sf_logger.warn("Line ending " + line.charAt(line.length() - 1) + " not recognized");
				m_subject.set(null);
			}
			return Stream.of(triple);
		} catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
			throw new BadDataFormatException("Couldn't parse line #" + m_lineNumber, e);
		} catch (RuntimeException e) {
			// this is a little weird, but it's helpful
			// not that we're not throwing a BadDataFormatException because we don't expect AIOOB, e.g.
			e.addSuppressed(new RuntimeException("Unexpectedly failed to parse line " + m_lineNumber));
			throw e;
		}
	}

	@Override
	public long nLinesProcessed() {
		return m_lineNumber.get();
	}

	@Nonnull
	public Map<String, Prefix> getPrefixes() {
		return m_prefixes;
	}

	protected Prefix parsePrefix(String line) {
		Matcher matcher = sf_prefixPattern.matcher(line);
		if (!matcher.matches()) {
			throw new IllegalArgumentException("Prefix line '" + line + "' not understood");
		}
		return new Prefix(matcher.group(1).trim(), matcher.group(2).trim());
	}

	protected Triple parseTriple(String line) {
		Matcher matcher = sf_triplePattern.matcher(line);
		if (!matcher.matches()) {
			throw new IllegalArgumentException("Triple '" + line + "' not understood");
		}
		Node subject = parseNode(matcher.group(1).trim(), "subject");
		Node predicate = parseNode(matcher.group(2).trim(), "predicate");
		Node object = parseNode(matcher.group(3).trim(), "object");
		return new Triple(subject, predicate, object);
	}

	protected Triple parsePartTriple(String line, Node subject) {
		Matcher matcher = sf_doublePattern.matcher(line);
		if (!matcher.matches()) {
			throw new IllegalArgumentException("Triple (with preceding subject) '" + line + "' not understood");
		}
		Node predicate = parseNode(matcher.group(1).trim(), "predicate");
		Node object = parseNode(matcher.group(2).trim(), "object");
		return new Triple(subject, predicate, object);
	}

	protected Node parseNode(String string, String label) {
		Matcher matcher = sf_nodePattern.matcher(string);
		if (!matcher.matches()) {
			throw new IllegalArgumentException(
					"Failed to parse " + label + " '" + string + "' with regex " + sf_nodePattern.pattern()
			);
		}
		return new Node(
				matcher.group(1).trim(),
				Optional.ofNullable(matcher.group(2)).map(String::trim),
				Optional.ofNullable(matcher.group(3)).map(String::trim)
		);
	}

	@Override
	public String toString() {
		return "TurtleParser{" +
				"usePrefixes=" + m_usePrefixes +
				", lineNumber=" + m_lineNumber.get() +
				", prefixes=" + m_prefixes +
				", subject=" + m_subject +
				'}';
	}
}
