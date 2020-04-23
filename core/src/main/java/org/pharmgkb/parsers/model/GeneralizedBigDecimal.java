package org.pharmgkb.parsers.model;

import com.google.common.collect.Range;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

/**
 * A {@link BigDecimal} that can be infinite or NaN.
 * @author Douglas Myers-Turnbull
 */
public class GeneralizedBigDecimal implements Comparable<GeneralizedBigDecimal>, Serializable {

	public static final GeneralizedBigDecimal NAN = new GeneralizedBigDecimal("NaN");
	public static final GeneralizedBigDecimal POSITIVE_INFINITY = new GeneralizedBigDecimal("Inf");
	public static final GeneralizedBigDecimal NEGATIVE_INFINITY = new GeneralizedBigDecimal("-Inf");
	private static final long serialVersionUID = 8608688553173129002L;

	private final Optional<BigDecimal> m_digits;
	private final String m_string;

	public GeneralizedBigDecimal(int i) {
		this(String.valueOf(i));
	}

	public GeneralizedBigDecimal(long i) {
		this(String.valueOf(i));
	}

	public GeneralizedBigDecimal(@Nonnull String string) {
		m_string = string;
		if (Arrays.asList("Inf", "+Inf", "-Inf", "NaN").contains(string)) {
			m_digits = Optional.empty();
		} else {
			m_digits = Optional.of(new BigDecimal(string));
		}
	}

	public GeneralizedBigDecimal(@Nonnull BigDecimal bd) {
		m_string = bd.toString();
		m_digits = Optional.of(bd);
	}

	@Nonnull
	public Optional<BigDecimal> getValue() {
		return m_digits;
	}

	public boolean isInfinite() {
		return m_string.endsWith("Inf");
	}

	public boolean isPositiveInfinity() {
		return m_string.equals("Inf") || m_string.equals("+Inf");
	}

	public boolean isNegativeInfinity() {
		return m_string.equals("-Inf");
	}

	public boolean isNan() {
		return m_string.equals("NaN");
	}

	/**
	 * Returns one of the following:
	 * <ul>
	 *     <li>{@code NaN}</li>
	 *     <li>{@code Inf}</li>
	 *     <li>{@code +Inf}</li>
	 *     <li>{@code -Inf}</li>
	 *     <li>the </li>
	 * </ul>
	 */
	@Nonnull
	@Override
	public String toString() {
		return m_string;
	}

	/**
	 * {@code Inf} and {@code +Inf} are considered to be equal.
	 */
	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GeneralizedBigDecimal that = (GeneralizedBigDecimal) o;
		return m_string.equals(that.m_string) || isPositiveInfinity() && that.isPositiveInfinity();
	}

	@Override
	public int hashCode() {
		return m_string.hashCode();
	}

	/**
	 * @throws UnsupportedOperationException If this or {@code o} is {@code NaN}
	 */
	@Override
	public int compareTo(@Nonnull GeneralizedBigDecimal o) {
		if (isNan() || o.isNan()) {
			throw new UnsupportedOperationException("Can't compare NaN to anything (including another NaN)");
		}
		if (m_digits.isPresent() && o.m_digits.isPresent()) {
			return m_digits.get().compareTo(o.m_digits.get());
		}
		if (isPositiveInfinity() && o.isPositiveInfinity() || isNegativeInfinity() && o.isNegativeInfinity()) return 0;
		return isPositiveInfinity()? 1 : -1;
	}

	/**
	 * @throws UnsupportedOperationException If this or {@code o} is {@code NaN}
	 */
	public boolean lessThan(@Nonnull String o) {
		return compareTo(new GeneralizedBigDecimal(o)) < 0;
	}

	/**
	 * @throws UnsupportedOperationException If this or {@code o} is {@code NaN}
	 */
	public boolean containedIn(@Nonnull Range<GeneralizedBigDecimal> range) {
		return range.contains(this);
	}

	/**
	 * @throws UnsupportedOperationException If this or {@code o} is {@code NaN}
	 */
	public boolean lessThanOrEqual(@Nonnull String o) {
		return compareTo(new GeneralizedBigDecimal(o)) < 1;
	}

	/**
	 * @throws UnsupportedOperationException If this or {@code o} is {@code NaN}
	 */
	public boolean greaterThan(@Nonnull String o) {
		return compareTo(new GeneralizedBigDecimal(o)) > 0;
	}

	/**
	 * @throws UnsupportedOperationException If this or {@code o} is {@code NaN}
	 */
	public boolean greaterThanOrEqual(@Nonnull String o) {
		return compareTo(new GeneralizedBigDecimal(o)) > -1;
	}

	/**
	 * @throws UnsupportedOperationException If this or {@code o} is {@code NaN}
	 */
	public boolean lessThan(@Nonnull GeneralizedBigDecimal o) {
		return compareTo(o) < 0;
	}

	/**
	 * @throws UnsupportedOperationException If this or {@code o} is {@code NaN}
	 */
	public boolean lessThanOrEqual(@Nonnull GeneralizedBigDecimal o) {
		return compareTo(o) < 1;
	}

	/**
	 * @throws UnsupportedOperationException If this or {@code o} is {@code NaN}
	 */
	public boolean greaterThan(@Nonnull GeneralizedBigDecimal o) {
		return compareTo(o) > 0;
	}

	/**
	 * @throws UnsupportedOperationException If this or {@code o} is {@code NaN}
	 */
	public boolean greaterThanOrEqual(@Nonnull GeneralizedBigDecimal o) {
		return compareTo(o) > -1;
	}
}
