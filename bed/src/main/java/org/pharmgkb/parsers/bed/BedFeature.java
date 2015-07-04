package org.pharmgkb.parsers.bed;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.pharmgkb.parsers.ScoredGenomeFeature;
import org.pharmgkb.parsers.Strand;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import java.awt.Color;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * A UCSC BED line.
 * See <a href="http://genome.ucsc.edu/FAQ/FAQformat.html">http://genome.ucsc.edu/FAQ/FAQformat.html</a>.
 * @author Douglas Myers-Turnbull
 */
@Immutable
public class BedFeature implements ScoredGenomeFeature {

	private final String m_chromosome;

	private final long m_start;

	private final long m_end;

	private final Optional<String> m_name;

	private final Optional<BigDecimal> m_score;

	private final Optional<Strand> m_strand;

	private final Optional<Long> m_thickStart;

	private final Optional<Long> m_thickEnd;

	private final Optional<Color> m_color;

	private final ImmutableList<BedBlock> m_blocks;

	@Override
	@Nonnull
	public String getChromosome() {
		return m_chromosome;
	}

	@Override
	@Nonnegative
	public long getStart() {
		return m_start;
	}

	@Override
	@Nonnegative
	public long getEnd() {
		return m_end;
	}

	@Override
	@Nonnull
	public Optional<String> getName() {
		return m_name;
	}

	@Override
	@Nonnull
	public Optional<BigDecimal> getScore() {
		return m_score;
	}

	@Override
	@Nonnull
	public Optional<Strand> getStrand() {
		return m_strand;
	}

	@Nonnull
	@Nonnegative
	public Optional<Long> getThickStart() {
		return m_thickStart;
	}

	@Nonnull
	@Nonnegative
	public Optional<Long> getThickEnd() {
		return m_thickEnd;
	}

	@Nonnull
	public Optional<Color> getColor() {
		return m_color;
	}

	@Nonnull
	public List<BedBlock> getBlocks() {
		return m_blocks;
	}

	private BedFeature(@Nonnull String chromosome, long start, long end, @Nonnull Optional<String> name,
	                   @Nonnull Optional<BigDecimal> score, Optional<Strand> strand, Optional<Long> thickStart,
	                   Optional<Long> thickEnd, Optional<Color> color, @Nonnull List<BedBlock> blocks) {
		m_chromosome = chromosome;
		m_start = start;
		m_end = end;
		m_name = name;
		m_score = score;
		m_strand = strand;
		m_thickStart = thickStart;
		m_thickEnd = thickEnd;
		m_color = color;
		m_blocks = ImmutableList.copyOf(blocks);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this).add("chromosome", m_chromosome).add("start", m_start).add("end", m_end)
				.add("name", m_name).add("score", m_score).add("strand", m_strand).add("thickStart", m_thickStart)
				.add("thickEnd", m_thickEnd).add("color", m_color).add("blocks", m_blocks).toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		BedFeature that = (BedFeature) o;
		return Objects.equals(m_chromosome, that.m_chromosome)
				&& Objects.equals(m_start, that.m_start)
				&& Objects.equals(m_end, that.m_end)
				&& Objects.equals(m_name, that.m_name)
				&& Objects.equals(m_score, that.m_score)
				&& Objects.equals(m_strand, that.m_strand)
				&& Objects.equals(m_thickStart, that.m_thickStart)
				&& Objects.equals(m_thickEnd, that.m_thickEnd)
				&& Objects.equals(m_color, that.m_color)
				&& Objects.equals(m_blocks, that.m_blocks);
	}

	@Override
	public int hashCode() {
		return Objects.hash(m_chromosome, m_start, m_end, m_name, m_score, m_strand, m_thickStart, m_thickEnd, m_color,
		                    m_blocks);
	}

	public static class Builder {

		private final String m_chromosome;

		private final long m_start;

		private final long m_end;

		private Optional<String> m_name;

		private Optional<BigDecimal> m_score;

		private Optional<Strand> m_strand;

		private Optional<Long> m_thickStart;

		private Optional<Long> m_thickEnd;

		private Optional<Color> m_color;

		private final List<BedBlock> m_blocks;

		private boolean m_built;

		public Builder(@Nonnull String chromosome, @Nonnegative long start, @Nonnegative long end) {
			Preconditions.checkArgument(start > -1, "Start " + start + " is negative");
			Preconditions.checkArgument(end > -1, "End " + end + " is negative");
			m_chromosome = chromosome;
			m_start = start;
			m_end = end;
			m_name = Optional.empty();
			m_score = Optional.empty();
			m_strand = Optional.empty();
			m_thickStart = Optional.empty();
			m_thickEnd = Optional.empty();
			m_color = Optional.empty();
			m_blocks = new ArrayList<>();
		}

		public Builder(@Nonnull BedFeature feature) {
			m_chromosome = feature.getChromosome();
			m_start = feature.getStart();
			m_end = feature.getEnd();
			setName(feature.getName().orElse(null));
			setScore(feature.getScore().orElse(null));
			setStrand(feature.getStrand().orElse(null));
			setColor(feature.getColor().orElse(null));
			setThickStart(feature.getThickStart().orElse(null));
			setThickEnd(feature.getThickEnd().orElse(null));
			m_blocks = new ArrayList<>(feature.getBlocks()); // we need to copy so that it's mutable!
		}

		@Nonnull
		public Builder setName(@Nullable String name) {
			m_name = Optional.ofNullable(name);
			return this;
		}

		@Nonnull
		public Builder setScore(@Nullable BigDecimal score) {
			Preconditions.checkArgument(score == null || BigDecimal.ZERO.compareTo(score) != 1,
			                            "Score is " + score + " < 0");
			Preconditions.checkArgument(score == null || new BigDecimal("1000").compareTo(score) != -1,
			                            "Score is " + score + " > 1000");
			m_score = Optional.ofNullable(score);
			return this;
		}

		@Nonnull
		public Builder setStrand(@Nullable Strand strand) {
			m_strand = Optional.ofNullable(strand);
			return this;
		}

		@Nonnull
		public Builder setThickStart(@Nullable @Nonnegative Long thickStart) {
			Preconditions.checkArgument(thickStart == null || thickStart >= 0, "Thick start " + thickStart
					+ " is negative");
			m_thickStart = Optional.ofNullable(thickStart);
			return this;
		}

		@Nonnull
		public Builder setThickEnd(@Nullable @Nonnegative Long thickEnd) {
			Preconditions.checkArgument(thickEnd == null || thickEnd >= 0, "Thick end " + thickEnd + " is negative");
			m_thickEnd = Optional.ofNullable(thickEnd);
			return this;
		}

		@Nonnull
		public Builder setColor(@Nullable String color) {
			if (color == null) {
				m_color = Optional.empty();
			} else {
				String[] parts = color.split(",");
				Preconditions.checkArgument(parts.length == 3, "Can't parse color " + color);
				try {
					m_color = Optional.of(new Color(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]),
					                                Integer.parseInt(parts[2])));
				} catch (IllegalArgumentException e) { // includes NumberFormatException
					throw new IllegalArgumentException("Can't parse color " + color, e);
				}
			}
			return this;
		}

		@Nonnull
		public Builder setColor(@Nullable Color color) {
			if (color != null) {
				Preconditions.checkArgument(color.getAlpha() == 255, "Item RGB has alpha " + color.getAlpha()
						+ "; should be 255");
			}
			m_color = Optional.ofNullable(color);
			return this;
		}

		@Nonnull
		public Builder clearBlocks() {
			m_blocks.clear();
			return this;
		}

		@Nonnull
		public Builder addBlock(@Nonnull BedBlock block) {
			Preconditions.checkArgument(!m_blocks.isEmpty() || block.getStart() == 0,
			                            "The first block must start at 0, but started at " + block.getStart());
			for (BedBlock other : m_blocks) {
				Preconditions.checkArgument(block.getStart() >= other.getEnd() || block.getEnd() <= other.getStart(),
				                            "block " + other + " overlaps with block " + block);
			}
			m_blocks.add(block);
			return this;
		}

		@Nonnull
		public Builder addBlock(@Nonnegative long start, @Nonnegative long end) {
			return addBlock(new BedBlock(start, end));
		}

		@Nonnull
		public BedFeature build() {
			if (!m_blocks.isEmpty()) {
				long blockEnd = m_blocks.get(m_blocks.size() - 1).getEnd();
				Preconditions.checkArgument(blockEnd == m_end,
				                            "The end of the last block must be the end of the feature; was " + blockEnd
						                  + " instead of " + m_end);
			}
			if (m_built) {
				throw new IllegalStateException("BedFeature already built");
			}
			m_built = true;
			return new BedFeature(m_chromosome, m_start, m_end, m_name, m_score, m_strand, m_thickStart, m_thickEnd,
			                      m_color, m_blocks);
		}

	}

}
