
import com.google.common.base.MoreObjects;
import org.pharmgkb.parsers.BadDataFormatException;
import org.pharmgkb.parsers.LineWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Writes matrices, CSV, etc.
 * @author Douglas Myers-Turnbull
 */
@ThreadSafe
public class MatrixWriter implements LineWriter<List<String>> {

    private static final long sf_logEvery = 10000;
    private static final Logger sf_logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private AtomicLong m_lineNumber = new AtomicLong(0L);
    private final String m_delimiter;
    private final String m_linePrefix;
    private final String m_lineSuffix;
    private final String m_valuePrefix;
    private final String m_valueSuffix;
    private final boolean m_isFixedSize;

    public static MatrixWriter tabs() {
        return new MatrixWriter("\t", "", "", "", "", true);
    }

    public static MatrixWriter commas() {
        return new MatrixWriter(",", "", "", "", "", true);
    }

    public static MatrixWriter spaces() {
        return new MatrixWriter(" ", "", "", "", "", true);
    }

    public MatrixWriter(
            @Nonnull String delimiter,
            @Nonnull String linePrefix, @Nonnull String lineSuffix,
            @Nonnull String valuePrefix, @Nonnull String valueSuffix,
            boolean isFixedSize
    ) {
        this.m_delimiter = delimiter;
        this.m_valuePrefix = valuePrefix;
        this.m_valueSuffix = valueSuffix;
        this.m_linePrefix = linePrefix;
        this.m_lineSuffix = lineSuffix;
        this.m_isFixedSize = isFixedSize;
    }

    @Nonnull
    @Override
    public String apply(@Nonnull List<String> row) {
        if (m_lineNumber.incrementAndGet() % sf_logEvery == 0) {
            sf_logger.debug("Writing line #{}", m_lineNumber);
        }
        List<String> bad = (row.stream().filter(this.m_delimiter::contains).collect(Collectors.toList()));
        if (bad.size() > 0 && m_valueSuffix.isEmpty() && m_valuePrefix.isEmpty()) {
            throw new BadDataFormatException("Values contain the delimiters: " + String.join(",", bad));
        } else if (bad.size() > 0) {
           sf_logger.debug("Values contain the delimiters: " + String.join(",", bad));
        }
        // don't trim; let them do it
        String values = row.stream()
                .map(s -> this.m_valuePrefix + s + this.m_valueSuffix)
                .collect(Collectors.joining(this.m_delimiter));
        return m_linePrefix + values + m_lineSuffix;
    }

    @Override
    public long nLinesProcessed() {
        return m_lineNumber.get();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("lineNumber", m_lineNumber)
                .add("delimiter", m_delimiter)
                .add("linePrefix", m_linePrefix)
                .add("lineSuffix", m_lineSuffix)
                .add("valuePrefix", m_valuePrefix)
                .add("valueSuffix", m_valueSuffix)
                .add("isFixedSize", m_isFixedSize)
                .toString();
    }
}