package org.pharmgkb.parsers.gff;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
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

	/**
	 * @return Also known as the sequence ID
	 */
	@Nonnull
	public String getCoordinateSystemName() {
		return m_coordinateSystemId;
	}

	/**
	 * <strong>0-based: Note that this is the GFF file value minus 1</strong>.
	 */
	@Nonnegative
	public long getStart() {
		return m_start;
	}

	/**
	 * <strong>0-based: Note that this is the GFF file value minus 1</strong>.
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

		protected String m_coordinateSystemId;

		protected final String m_type;

		protected final long m_start;

		protected final long m_end;

		protected Optional<String> m_source;

		protected Optional<BigDecimal> m_score;

		protected GffStrand m_strand;

		protected Optional<CdsPhase> m_phase;

		/**
		 * Note that the strand defaults to {@link GffStrand#UNSTRANDED}.
		 * @param coordinateSystemId Also known as the sequence ID
		 * @param type For example, "CDS"
		 * @param start <strong>0-based</strong>
		 * @param end <strong>0-based</strong>
		 */
		public Builder(@Nonnull String coordinateSystemId, @Nonnull String type, @Nonnegative long start, @Nonnegative long end) {
			Preconditions.checkArgument(start > -1, "Start " + start + " < 0");
			Preconditions.checkArgument(end > -1, "End " + end + " < 0");
			Preconditions.checkArgument(start <= end, "Start " + start + " comes before end " + end);
			m_coordinateSystemId = coordinateSystemId;
			m_type = type;
			m_start = start;
			m_end = end;
			m_source = Optional.empty();
			m_score = Optional.empty();
			m_strand = GffStrand.UNSTRANDED;
			m_phase = Optional.empty();
		}

		@Nonnull
		public B setSource(@Nullable String source) {
			return setSource(Optional.ofNullable(source));
		}
		@SuppressWarnings("unchecked")
		@Nonnull
		public B setSource(@Nonnull Optional<String> source) {
			m_source = source;
			return (B) this;
		}

		@Nonnull
		public B setScore(@Nullable BigDecimal score) {
			return setScore(Optional.ofNullable(score));
		}
		@SuppressWarnings("unchecked")
		@Nonnull
		public B setScore(@Nonnull Optional<BigDecimal> score) {
			m_score = score;
			return (B) this;
		}

		@SuppressWarnings("unchecked")
		@Nonnull
		public B setStrand(@Nonnull GffStrand strand) {
			m_strand = strand;
			return (B) this;
		}

		@Nonnull
		public B setPhase(@Nullable CdsPhase phase) {
			return setPhase(Optional.ofNullable(phase));
		}
		@SuppressWarnings("unchecked")
		@Nonnull
		public B setPhase(@Nonnull Optional<CdsPhase> phase) {
			m_phase = phase;
			return (B) this;
		}

	}

}
