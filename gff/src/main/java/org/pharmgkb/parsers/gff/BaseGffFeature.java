package org.pharmgkb.parsers.gff;

import com.google.common.base.MoreObjects;
import org.pharmgkb.parsers.CharacterEscaper;
import org.pharmgkb.parsers.ObjectBuilder;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.Optional;

/**
 * A line in a GFF2 file.
 * See <a href="http://genome.ucsc.edu/FAQ/FAQformat.html#format3">http://genome.ucsc.edu/FAQ/FAQformat.html#format3</a>.
 * @author Douglas Myers-Turnbull
 */
@Immutable
public abstract class BaseGffFeature {

	private final String m_coordinateSystemId;

	private final String m_type;

	private final long m_start;

	private final long m_end;

	private final Optional<String> m_source;

	private final Optional<BigDecimal> m_score;

	private final GffStrand m_strand;

	private final Optional<CdsPhase> m_phase;

	@SuppressWarnings("unchecked") // because Builder is generic
	protected BaseGffFeature(@Nonnull Builder builder) {
		m_coordinateSystemId = builder.m_coordinateSystemId;
		m_type = builder.m_type;
		m_start = builder.m_start;
		m_end = builder.m_end;
		m_source = builder.m_source;
		m_score = builder.m_score;
		m_strand = builder.m_strand;
		m_phase = builder.m_phase;
	}

	@Nonnull
	public String getCoordinateSystemName() {
		return m_coordinateSystemId;
	}

	/**
	 * <strong>Note that this is the GFF file value minus 1</strong>.
	 */
	@Nonnegative
	public long getStart() {
		return m_start;
	}

	/**
	 * <strong>Note that this is the GFF file value minus 1</strong>.
	 */
	@Nonnegative
	public long getEnd() {
		return m_end;
	}

	@Nonnull
	public String getType() {
		return m_type;
	}

	@Nonnull
	public Optional<String> getSource() {
		return m_source;
	}

	@Nonnull
	public Optional<BigDecimal> getScore() {
		return m_score;
	}

	@Nonnull
	public GffStrand getStrand() {
		return m_strand;
	}

	@Nonnull
	public Optional<CdsPhase> getPhase() {
		return m_phase;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("id", m_coordinateSystemId).add("type", m_type).add("source", m_source)
				.add("start", m_start).add("end", m_end).add("score", m_score)
				.add("strand", m_strand).add("phase", m_phase).toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		BaseGffFeature that = (BaseGffFeature) o;
		return Objects.equals(m_coordinateSystemId, that.m_coordinateSystemId)
				&& Objects.equals(m_source, that.m_source)
				&& Objects.equals(m_type, that.m_type)
				&& Objects.equals(m_start, that.m_start)
				&& Objects.equals(m_end, that.m_end)
				&& Objects.equals(m_score, that.m_score)
				&& Objects.equals(m_strand, that.m_strand)
				&& Objects.equals(m_phase, that.m_phase);
	}

	@Override
	public int hashCode() {
		return Objects.hash(m_coordinateSystemId, m_type, m_start, m_end, m_source, m_strand, m_phase, m_score);
	}

	@NotThreadSafe
	protected abstract static class Builder<T, B extends Builder> implements ObjectBuilder<T> {

		private String m_coordinateSystemId;

		private final String m_type;

		private final long m_start;

		private final long m_end;

		private Optional<String> m_source;

		private Optional<BigDecimal> m_score;

		private GffStrand m_strand;

		private Optional<CdsPhase> m_phase;

		@Nonnull
		protected abstract CharacterEscaper fieldEscaper();

		@Nonnull
		protected abstract CharacterEscaper coordinateSystemIdEscaper();

		public Builder(@Nonnull String coordinateSystemId, @Nonnull String type, @Nonnegative long start, @Nonnegative long end) {
			m_coordinateSystemId = coordinateSystemId;
			m_type = coordinateSystemIdEscaper().escape(type);
			m_start = start;
			m_end = end;
			m_source = Optional.empty();
			m_score = Optional.empty();
			m_phase = Optional.empty();
		}

		@Nonnull
		public Builder setSource(@Nullable String source) {
			return setSource(Optional.ofNullable(source));
		}
		@Nonnull
		public Builder setSource(@Nonnull Optional<String> source) {
			m_source = fieldEscaper().escape(source);
			return this;
		}

		@Nonnull
		public Builder setScore(@Nullable BigDecimal score) {
			return setScore(Optional.ofNullable(score));
		}
		@Nonnull
		public Builder setScore(@Nonnull Optional<BigDecimal> score) {
			m_score = score;
			return this;
		}

		@Nonnull
		public Builder setStrand(@Nonnull GffStrand strand) {
			m_strand = strand;
			return this;
		}

		@Nonnull
		public Builder setPhase(@Nullable CdsPhase phase) {
			return setPhase(Optional.ofNullable(phase));
		}
		@Nonnull
		public Builder setPhase(@Nonnull Optional<CdsPhase> phase) {
			m_phase = phase;
			return this;
		}

	}

}
