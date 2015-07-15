package org.pharmgkb.parsers.bed;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import org.pharmgkb.parsers.ObjectBuilder;
import org.pharmgkb.parsers.model.Strand;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import java.awt.Color;
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
public class BedFeature {

	private final String m_chromosome;

	private final long m_start;

	private final long m_end;

	private final Optional<String> m_name;

	private final Optional<Integer> m_score;

	private final Optional<Strand> m_strand;

	private final Optional<Long> m_thickStart;

	private final Optional<Long> m_thickEnd;

	private final Optional<Color> m_color;

	private final ImmutableList<BedBlock> m_blocks;

	@Nonnull
	public String getChromosome() {
		return m_chromosome;
	}

	@Nonnegative
	public long getStart() {
		return m_start;
	}

	@Nonnegative
	public long getEnd() {
		return m_end;
	}

	@Nonnull
	public Optional<String> getName() {
		return m_name;
	}

	@Nonnull
	public Optional<Integer> getScore() {
		return m_score;
	}

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

	private BedFeature(@Nonnull Builder builder) {
		m_chromosome = builder.m_chromosome;
		m_start = builder.m_start;
		m_end = builder.m_end;
		m_name = builder.m_name;
		m_score = builder.m_score;
		m_strand = builder.m_strand;
		m_thickStart = builder.m_thickStart;
		m_thickEnd = builder.m_thickEnd;
		m_color = builder.m_color;
		m_blocks = ImmutableList.copyOf(builder.m_blocks);
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

	@NotThreadSafe
	public static class Builder implements ObjectBuilder<BedFeature> {

		private String m_chromosome;

		private long m_start;

		private long m_end;

		private Optional<String> m_name;

		private Optional<Integer> m_score;

		private Optional<Strand> m_strand;

		private Optional<Long> m_thickStart;

		private Optional<Long> m_thickEnd;

		private Optional<Color> m_color;

		private final List<BedBlock> m_blocks;

		public Builder(@Nonnull String chromosome, @Nonnegative long start, @Nonnegative long end) {
			Preconditions.checkArgument(!chromosome.contains("\t"), "Chromosome name " + chromosome + " contains a tab");
			Preconditions.checkArgument(!chromosome.contains("\n") && !chromosome.contains("\r"),  "Chromosome name " + chromosome
					+ " contains a newline (LF or CR)");
			Preconditions.checkArgument(start > -1, "Start " + start + " < 0");
			Preconditions.checkArgument(end > -1, "End " + end + " < 0");
			Preconditions.checkArgument(start <= end, "Start " + start + " comes before end " + end);
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
			setName(feature.getName());
			setScore(feature.getScore());
			setStrand(feature.getStrand());
			setColor(feature.getColor());
			setThickStart(feature.getThickStart());
			setThickEnd(feature.getThickEnd());
			m_blocks = new ArrayList<>(feature.getBlocks()); // we need to copy so that it's mutable!
		}

		@Nonnull
		public Builder setChromosome(@Nonnull String chromosome) {
			Preconditions.checkArgument(!chromosome.contains("\t"),
			                             "Chromosome name contains a tab");
			Preconditions.checkArgument(!chromosome.contains("\n") && !chromosome.contains("\r"),
			                            "Chromosome name contains a newline (LF or CR)");
			m_chromosome = chromosome;
			return this;
		}

		@Nonnull
		public Builder setStart(@Nonnegative long start) {
			Preconditions.checkArgument(start > -1, "Start " + start + " < 0");
			m_start = start;
			return this;
		}

		@Nonnull
		public Builder setEnd(@Nonnegative long end) {
			Preconditions.checkArgument(end > -1, "End " + end + " < 0");
			m_end = end;
			return this;
		}

		@Nonnull
		public Builder setName(@Nullable String name) {
			return setName(Optional.ofNullable(name));
		}
		@Nonnull
		public Builder setName(@Nonnull Optional<String> name) {
			Preconditions.checkArgument(!name.isPresent() || !name.get().contains("\t"),
			                            "Feature name contains a tab");
			Preconditions.checkArgument(!name.isPresent() || !name.get().contains(System.lineSeparator()),
			                            "Feature name contains a newline");
			m_name = name;
			return this;
		}

		@Nonnull
		public Builder setScore(@Nullable Integer score) {
			return setScore(Optional.ofNullable(score));
		}
		@Nonnull
		public Builder setScore(@Nonnull Optional<Integer> score) {
			if (score.isPresent()) {
				Preconditions.checkArgument(score.get() > -1, "Score " + score.get() + " < 0");
				Preconditions.checkArgument(score.get() < 1001, "Score " + score.get() + " > 1000");
			}
			m_score = score;
			return this;
		}

		@Nonnull
		public Builder setStrand(@Nullable Strand strand) {
			return setStrand(Optional.ofNullable(strand));
		}
		@Nonnull
		public Builder setStrand(@Nonnull Optional<Strand> strand) {
			m_strand = strand;
			return this;
		}

		@Nonnull
		public Builder setThickStart(@Nullable @Nonnegative Long thickStart) {
			return setThickStart(Optional.ofNullable(thickStart));
		}
		@Nonnull
		public Builder setThickStart(@Nonnull @Nonnegative Optional<Long> thickStart) {
			Preconditions.checkArgument(!thickStart.isPresent() || thickStart.get() > -1, "Thick start " + thickStart
					+ " < 0");
			m_thickStart = thickStart;
			return this;
		}

		@Nonnull
		public Builder setThickEnd(@Nullable @Nonnegative Long thickEnd) {
			return setThickEnd(Optional.ofNullable(thickEnd));
		}
		@Nonnull
		public Builder setThickEnd(@Nonnull @Nonnegative Optional<Long> thickEnd) {
			Preconditions.checkArgument(!thickEnd.isPresent() || thickEnd.get() > -1, "Thick end " + thickEnd + " < 0");
			m_thickEnd = thickEnd;
			return this;
		}

		@Nonnull
		public Builder setColorFromString(@Nullable String color) {
			return setColorFromString(Optional.ofNullable(color));
		}
		@Nonnull
		public Builder setColorFromString(@Nonnull Optional<String> color) {
			if (color.isPresent()) {
				String[] parts = color.get().split(",");
				Preconditions.checkArgument(parts.length == 3, "Can't parse color " + color);
				try {
					m_color = Optional.of(new Color(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]),
					                                Integer.parseInt(parts[2])));
				} catch (IllegalArgumentException e) { // includes NumberFormatException
					throw new IllegalArgumentException("Can't parse color " + color, e);
				}
			} else {
				m_color = Optional.empty();
			}
			return this;
		}

		@Nonnull
		public Builder setColor(@Nullable Color color) {
			return setColor(Optional.ofNullable(color));
		}
		@Nonnull
		public Builder setColor(@Nonnull Optional<Color> color) {
			if (color.isPresent()) {
				Preconditions.checkArgument(color.get().getAlpha() == 255, "Color has alpha " + color.get().getAlpha()
						+ "; should be 255");
			}
			m_color = color;
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
			                            "The first block starts at " + block.getStart() + " != 0");
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
				Preconditions.checkArgument(blockEnd == m_end - m_start,
				                            "The end of the last block must be the end of the feature; is " + blockEnd
						                            + " instead of " + (m_end - m_start));
			}
			return new BedFeature(this);
		}

	}

}
