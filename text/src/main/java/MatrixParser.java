import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import org.pharmgkb.parsers.BadDataFormatException;
import org.pharmgkb.parsers.LineParser;
import org.pharmgkb.parsers.ObjectBuilder;
import org.pharmgkb.parsers.utils.ReflectingConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;
import java.io.UncheckedIOException;
import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Parses a matrix or table from text.
 * @author Douglas Myers-Turnbull
 */
@ThreadSafe
public class MatrixParser<T> implements LineParser<List<T>> {

	private static final long sf_logEvery = 10000;
	private static final Logger sf_logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final Function<String, T> m_converter;
	private final String m_delimiter;
	private final Pattern m_lineExtractor;
	private final Pattern m_valueExtractor;
	private final boolean m_jaggedDimensions;
	private final Splitter m_splitter;

	private final Set<Integer> m_lengths;
	private AtomicLong m_lineNumber = new AtomicLong(0L);

	private MatrixParser(@Nonnull Builder<T> builder) {
		m_converter = builder.m_converter;
		m_delimiter = builder.m_delimiter;
		m_lineExtractor = builder.m_lineExtractor;
		m_valueExtractor = builder.m_lineExtractor;
		m_splitter = Splitter.on(builder.m_delimiter);
		m_jaggedDimensions = builder.m_jaggedDimensions;
		m_lengths = new LinkedHashSet<>(16);
	}

	@Nonnull
	@Override
	public Stream<List<T>> parseAll(@Nonnull Stream<String> stream) throws UncheckedIOException, BadDataFormatException {
		return stream.map(this);
	}

	@Nonnull
	@Override
	public List<T> apply(@Nonnull String line) {
		if (m_lineNumber.incrementAndGet() % sf_logEvery == 0) {
			sf_logger.debug("Reading line #{}", m_lineNumber);
		}
		Matcher match = m_lineExtractor.matcher(line);
		if (!match.matches()) {
			throw new BadDataFormatException("Line " + line + " does not match");
		}
		String fixed = match.group(1);
		List<T> list = m_splitter
				.splitToList(fixed).stream()
				.map(this::readItem)
				.collect(Collectors.toList());
		m_lengths.add(list.size());
		if (m_lengths.size() > 1 && !m_jaggedDimensions) {
			throw new BadDataFormatException(
					"Mismatched row lengths: {}"
					+ m_lengths.stream().map(Object::toString).collect(Collectors.joining(","))
			);
		}
		return list;
	}

	@Nonnull
	private T readItem(@Nonnull String item) {
		// trim OUTSIDE the quotes
		Matcher match = m_valueExtractor.matcher(item.trim());
		if (!match.matches() || match.group(1)==null) {
			throw new BadDataFormatException("Could not read " + item);
		}
		return m_converter.apply(match.group(1));
	}

	@Nonnegative
	@Override
	public long nLinesProcessed() {
		return m_lineNumber.get();
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("converter", m_converter)
				.add("delimiter", m_delimiter)
				.add("lineExtractor", m_lineExtractor)
				.add("valueExtractor", m_valueExtractor)
				.add("isJagged", m_jaggedDimensions)
				.add("lengths", m_lengths)
				.add("lineNumber", m_lineNumber)
				.toString();
	}

	@NotThreadSafe
	public static class Builder<T> implements ObjectBuilder<MatrixParser<T>> {

		private static final Pattern sf_bracketed = Pattern.compile(" *^[\\[{(]?([^]})]*)[]})]? *$");
		private static final Pattern sf_quoted = Pattern.compile("^ *[\"']?([^\"']*)[\"']? *$");

		private final Function<String, T> m_converter;
		private String m_delimiter;
		private Pattern m_lineExtractor;
		private Pattern m_valueExtractor;
		private boolean m_jaggedDimensions;

		@Nonnull
		public static Builder<BigDecimal> decimals() {
			return new Builder<>(BigDecimal::new);
		}

		@Nonnull
		public static Builder<BigInteger> integers() {
			return new Builder<>(BigInteger::new);
		}

		@Nonnull
		public static <A> Builder<A> reflecting(Class<? extends A> clazz) {
			return new Builder<>(s -> new ReflectingConstructor<>(clazz, String.class).instance(s));
		}

		public Builder(@Nonnull Function<String, T> converter) {
			m_converter = converter;
			m_lineExtractor = sf_bracketed;
			m_valueExtractor = sf_quoted;
		}

		@Nonnull
		public Builder<T> setDelimiter(@Nonnull String delimiter) {
			m_delimiter = delimiter;
			return this;
		}

		@Nonnull
		public Builder<T> setLineExtractor(@Nonnull Pattern regexWithGroup1) {
			if (regexWithGroup1.matcher("").groupCount() != 1) {
				throw new IllegalArgumentException("Line extractor " + regexWithGroup1 + " should have exactly 1 capture group");
			}
			m_lineExtractor = regexWithGroup1;
			return this;
		}

		@Nonnull
		public Builder<T> setValueExtractor(@Nonnull Pattern regexWithGroup1) {
			if (regexWithGroup1.matcher("").groupCount() != 1) {
				throw new IllegalArgumentException("Value extractor " + regexWithGroup1 + " should have exactly 1 capture group");
			}
			m_valueExtractor = regexWithGroup1;
			return this;
		}

		@Nonnull
		public Builder<T> allowJagged(@Nonnull Pattern regexWithGroup1) {
			m_jaggedDimensions = true;
			return this;
		}

		@Nonnull
		@Override
		public MatrixParser<T> build() {
			return new MatrixParser<>(this);
		}

	}
}

