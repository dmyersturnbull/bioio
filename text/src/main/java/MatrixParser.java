
import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import org.pharmgkb.parsers.BadDataFormatException;
import org.pharmgkb.parsers.MultilineParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Parses a matrix or table from text.
 * @author Douglas Myers-Turnbull
 */
@ThreadSafe
public class MatrixParser implements MultilineParser<String> {

    private static final Pattern sf_bracketed = Pattern.compile(" *^[\\[{(]?([^]})]*)[]})]? *$");
    private static final Pattern sf_quoted = Pattern.compile("^ *[\"']?([^\"']*)[\"']? *$");
	private static final long sf_logEvery = 10000;
	private static final Logger sf_logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	private AtomicLong m_lineNumber = new AtomicLong(0L);

	private final Splitter m_splitter;
	private final String m_delimiter;
	private final Pattern m_lineExtractor;
	private final Pattern m_valueExtractor;

	public MatrixParser(
	        @Nonnull String delimiter,
            @Nonnull Pattern lineExtractor,
            @Nonnull Pattern valueExtractor
    ) {
		this.m_delimiter = delimiter;
        this.m_lineExtractor = lineExtractor;
        this.m_valueExtractor = valueExtractor;
		this.m_splitter = Splitter.on(delimiter);
        if (lineExtractor.matcher("").groupCount() != 1) {
            throw new IllegalArgumentException("Line extractor " + lineExtractor + " should have 1 capture group");
        }
        if (valueExtractor.matcher("").groupCount() != 1) {
            throw new IllegalArgumentException("Value extractor " + valueExtractor + " should have 1 capture group");
        }
	}

    @Nonnull
    @Override
    public Stream<String> parseAll(@Nonnull Stream<String> stream) throws IOException, BadDataFormatException {
        return stream.flatMap(this);
    }

    @Nonnull
	@Override
	public Stream<String> apply(@Nonnull String line) {
		if (m_lineNumber.incrementAndGet() % sf_logEvery == 0) {
			sf_logger.debug("Reading line #{}", m_lineNumber);
		}
		Matcher match = m_lineExtractor.matcher(line);
		if (!match.matches()) {
		    throw new BadDataFormatException("Line " + line + " does not match");
        }
		String fixed = match.group(1);
		return this.m_splitter.splitToList(fixed).stream().map(this::read);
	}

	@Nonnull
	private String read(@Nonnull String item) {
		// trim OUTSIDE the quotes
	    Matcher match = m_valueExtractor.matcher(item.trim());
	    if (!match.matches() || match.group(1)==null) {
	        throw new BadDataFormatException("Could not read " + item);
        }
	    return match.group(1);
	}

	@Nonnegative
	@Override
	public long nLinesProcessed() {
		return m_lineNumber.get();
	}

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("lineNumber", m_lineNumber)
                .add("delimiter", m_delimiter)
                .add("lineExtractor", m_lineExtractor)
                .add("extractor", m_valueExtractor)
                .toString();
    }
}

