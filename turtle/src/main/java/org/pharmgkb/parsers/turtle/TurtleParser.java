package org.pharmgkb.parsers.turtle;

import org.pharmgkb.parsers.BadDataFormatException;
import org.pharmgkb.parsers.LineParser;
import org.pharmgkb.parsers.MultilineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;


@NotThreadSafe
public class TurtleParser implements MultilineParser<Triple> {

	private static final long sf_logEvery = 10000;
	private static final Pattern sf_prefixPattern = Pattern.compile("@prefix[ \t]+([A-Za-z0-9\\-_]+):[ \t]+<([^>]+)>[ \t]*\\.");
	// TODO This does not handle escapes or > or " inside quotes
	// TODO Also requires ^^ before @
	private static final Pattern sf_nodePattern = Pattern.compile("[<\"]?([^<\"]+)[>\"]?(?:@([A-Za-z0-9\\-_:]+))?(?:\\^{2}([A-Za-z0-9\\-_:]+))?");
	private static final Pattern sf_xPattern = Pattern.compile("([<\"]?(?:[^<\"]+)[>\"]?(?:@(?:[A-Za-z0-9\\-_:]+))?(?:\\^{2}(?:[A-Za-z0-9\\-_:]+))?)");
	private static final Pattern sf_triplePattern = Pattern.compile("^[ ]*" + sf_xPattern.pattern() + "[ ]+" + sf_xPattern + "[ ]+" + sf_xPattern + "[ ]*[;\\.]$");
	private static final Pattern sf_doublePattern = Pattern.compile("^[ ]*" + sf_xPattern + "[ ]+" + sf_xPattern + "[ ]*[;\\.]$");

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
		return stream.flatMap(this::apply);
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
		} catch (BadDataFormatException | NullPointerException e) {
			sf_logger.error("Failed on line " + m_lineNumber.get());
			sf_logger.error(e.getMessage());
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
			throw new BadDataFormatException("Prefix line '" + line + "' not understood");
		}
		return new Prefix(matcher.group(1), matcher.group(2));
	}

	protected Triple parseTriple(String line) {
		Matcher matcher = sf_triplePattern.matcher(line);
		if (!matcher.matches()) {
			throw new BadDataFormatException("Triple '" + line + "' not understood");
		}
		Node subject = parseNode(matcher.group(1));
		Node predicate = parseNode(matcher.group(2));
		Node object = parseNode(matcher.group(3));
		return new Triple(subject, predicate, object);
	}

	protected Triple parsePartTriple(String line, Node subject) {
		Matcher matcher = sf_doublePattern.matcher(line);
		if (!matcher.matches()) {
			throw new BadDataFormatException("Triple (with preceding subject) '" + line + "' not understood");
		}
		Node predicate = parseNode(matcher.group(1));
		Node object = parseNode(matcher.group(2));
		return new Triple(subject, predicate, object);
	}

	protected Node parseNode(String string) {
		Matcher matcher = sf_nodePattern.matcher(string);
		matcher.matches();
		return new Node(matcher.group(1), Optional.ofNullable(matcher.group(2)),  Optional.ofNullable(matcher.group(3)));
	}

}
