package org.pharmgkb.parsers.chain.model;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import org.pharmgkb.parsers.ObjectBuilder;
import org.pharmgkb.parsers.model.ChromosomeName;
import org.pharmgkb.parsers.model.Locus;
import org.pharmgkb.parsers.model.LocusRange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.function.Function;

/**
 * A mapping between reference coordinate sets according tot he UCSC "chain format".
 * See <a href="https://genome.ucsc.edu/goldenPath/help/chain.html">the UCSC specification</a>.
 * Coordinates are 0-based.
 * @author Douglas Myers-Turnbull
 */
@Immutable
public class GenomeChain implements Function<Locus, Optional<Locus>> {

	private static final Logger sf_logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// only compares loci of the same chromosome and strand
	private static final Comparator<LocusRange> sf_comparator = Comparator.comparingLong(o -> o.getEnd().getPosition());

	private final ImmutableMap<ChromosomeName, ImmutableSortedMap<LocusRange, LocusRange>> m_map;

	private GenomeChain(@Nonnull Builder builder) {
		this(builder.m_map);
	}

	private GenomeChain(@Nonnull Map<? extends ChromosomeName, ? extends NavigableMap<LocusRange, LocusRange>> mutableMap) {
		Map<ChromosomeName, ImmutableSortedMap<LocusRange, LocusRange>> map = new HashMap<>(2048);
		mutableMap.forEach((name, values) -> map.put(name, ImmutableSortedMap.copyOfSorted(values)));
		m_map = ImmutableMap.copyOf(map);
	}

	/**
	 * @return A new GenomeChain with the source and target assemblies swapped
	 */
	@Nonnull
	public GenomeChain invert() {
		Map<ChromosomeName, NavigableMap<LocusRange, LocusRange>> map = new HashMap<>(m_map.size());
		for (Map.Entry<ChromosomeName, ImmutableSortedMap<LocusRange, LocusRange>> e : m_map.entrySet()) {
			ChromosomeName chr = e.getKey();
			map.put(chr, new TreeMap<>());
			for (Map.Entry<LocusRange, LocusRange> entry : e.getValue().entrySet()) {
				map.get(chr).put(entry.getValue(), entry.getKey());
			}
		}
		return new GenomeChain(map);
	}

	@Nonnull
	public Optional<Locus> apply(@Nonnull Locus locus) {

		NavigableMap<LocusRange, LocusRange> list = m_map.get(locus.getChromosome());
		if (list == null) return Optional.empty();

		for (LocusRange range : list.navigableKeySet()) {

			if (range.contains(locus)) {
				final LocusRange targetRange = list.get(range);
				final long delta = locus.getPosition() - range.getStart().getPosition();
				return Optional.of(new Locus(
						targetRange.getChromosome(),
						targetRange.getStart().getPosition() + delta,
						targetRange.getStrand()
				));
			}

			if (range.getEnd().getPosition() > locus.getPosition()) {
				return Optional.empty();
			}

		}

		return Optional.empty();
	}

	@NotThreadSafe
	public static class Builder implements ObjectBuilder<GenomeChain> {

		private Map<ChromosomeName, NavigableMap<LocusRange, LocusRange>> m_map = new HashMap<>();

		public Builder() {

		}

		public Builder(@Nonnull Builder builder) {
			builder.m_map.forEach((chr, map) -> {
				NavigableMap<LocusRange, LocusRange> newMap = new TreeMap<>(map);
				m_map.put(chr, newMap);
			});
		}

		public Builder(@Nonnull GenomeChain chain) {
			chain.m_map.forEach((chr, map) -> {
				NavigableMap<LocusRange, LocusRange> newMap = new TreeMap<>(map);
				m_map.put(chr, newMap);
			});
		}

		/**
		 * Removes the mapping from {@code source} to some (any) other {@link LocusRange}.
		 */
		@Nonnull
		public Builder remove(@Nonnull LocusRange source) {
			ChromosomeName chr = source.getChromosome();
			if (m_map.containsKey(chr)) {
				m_map.get(chr).remove(source);
			}
			return this;
		}

		@Nonnull
		public Builder add(@Nonnull LocusRange source, @Nonnull LocusRange target) {

			final long sourceSize = source.length();
			final long targetSize = target.length();

			Preconditions.checkArgument(
					sourceSize == targetSize,
					source + " has size " + sourceSize + " but " + target + " has size " + targetSize
			);

			final ChromosomeName sourceChr = source.getChromosome();
			if (!m_map.containsKey(sourceChr)) {
				m_map.put(sourceChr, new TreeMap<>(sf_comparator));
			}

			for (LocusRange r : m_map.get(sourceChr).keySet()) {
				// can only compare when the strands are the same
				if (r.getStrand() == target.getStrand() && r.overlapsWith(source)) {
					throw new IllegalArgumentException("Source locus " + source + " overlaps with " + r);
				}
			}

			for (LocusRange r : m_map.get(sourceChr).values()) { // really, this should never happen
				// can only compare when the strands are the same
				if (r.getStrand() == target.getStrand() && r.overlapsWith(target)) {
					throw new IllegalArgumentException("Target locus " + target + " overlaps with " + r);
				}
			}

			m_map.get(sourceChr).put(source, target);

			return this;
		}

		@Nonnull
		public GenomeChain build() {
			return new GenomeChain(this);
		}

	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GenomeChain that = (GenomeChain) o;
		return Objects.equals(m_map, that.m_map);
	}

	@Override
	public int hashCode() {
		return Objects.hash(m_map);
	}

	@Override
	public String toString() {
		return "GenomeChain{" +
				"map=" + m_map +
				'}';
	}
}
