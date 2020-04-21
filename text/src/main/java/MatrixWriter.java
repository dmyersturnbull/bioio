
import com.google.common.base.MoreObjects;
import org.pharmgkb.parsers.BadDataFormatException;
import org.pharmgkb.parsers.LineWriter;
import org.pharmgkb.parsers.ObjectBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Writes matrices, CSV, etc.
 * @author Douglas Myers-Turnbull
 */
@ThreadSafe
public class MatrixWriter<T> implements LineWriter<List<T>> {

    private static final long sf_logEvery = 10000;
    private static final Logger sf_logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private AtomicLong m_lineNumber = new AtomicLong(0L);
    private final Function<T, String> m_converter;
    private final String m_delimiter;
    private final String m_linePrefix;
    private final String m_lineSuffix;
    private final String m_valuePrefix;
    private final String m_valueSuffix;
    private final boolean m_jaggedDimensions;

    public MatrixWriter(@Nonnull Builder<T> builder) {
        this.m_converter = builder.m_converter;
        this.m_delimiter = builder.m_delimiter;
        this.m_valuePrefix = builder.m_valuePrefix;
        this.m_valueSuffix = builder.m_valueSuffix;
        this.m_linePrefix = builder.m_linePrefix;
        this.m_lineSuffix = builder.m_lineSuffix;
        this.m_jaggedDimensions = builder.m_jaggedDimensions;
    }

    @Nonnull
    @Override
    public String apply(@Nonnull List<T> row) {
        if (m_lineNumber.incrementAndGet() % sf_logEvery == 0) {
            sf_logger.debug("Writing line #{}", m_lineNumber);
        }
        List<String> strings = row.stream().map(this.m_converter).collect(Collectors.toList());
        List<String> bad = (strings.stream().filter(this.m_delimiter::contains).collect(Collectors.toList()));
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
                .add("isJagged", m_jaggedDimensions)
                .toString();
    }

    @NotThreadSafe
    public static class Builder<T> implements ObjectBuilder<MatrixWriter<T>> {

        private Function<T, String> m_converter;
        private String m_delimiter;
        private String m_linePrefix;
        private String m_lineSuffix;
        private String m_valuePrefix;
        private String m_valueSuffix;
        private boolean m_jaggedDimensions;

        public Builder() {
            this.m_converter = v -> v.toString().trim();
            this.m_delimiter = "\t";
            this.m_linePrefix = "";
            this.m_lineSuffix = "";
            this.m_valuePrefix = "";
            this.m_valueSuffix = "";
            this.m_jaggedDimensions = false;
        }

        @Nonnull
        public MatrixWriter.Builder<T> setDelimiter(String delimiter) {
            m_delimiter = delimiter;
            return this;
        }

        @Nonnull
        public MatrixWriter.Builder<T> setConverter(Function<T, String> converter) {
            m_converter = converter;
            return this;
        }

        @Nonnull
        public MatrixWriter.Builder<T> encloseLine(String prefix, String suffix) {
            m_linePrefix = prefix;
            m_lineSuffix = suffix;
            return this;
        }

        @Nonnull
        public MatrixWriter.Builder<T> encloseValue(String prefix, String suffix) {
            m_valuePrefix = prefix;
            m_valueSuffix = suffix;
            return this;
        }

        @Nonnull
        public MatrixWriter.Builder<T> allowJagged(Pattern regexWithGroup1) {
            m_jaggedDimensions = true;
            return this;
        }

        @Nonnull
        @Override
        public MatrixWriter<T> build() {
            return new MatrixWriter<T>(this);
        }
    }
}