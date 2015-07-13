package org.pharmgkb.parsers.chain;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSortedMap;
import org.pharmgkb.parsers.ObjectBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import java.lang.invoke.MethodHandles;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;
import java.util.function.Function;

/**
 * A mapping between reference coordinate sets according tot he UCSC "chain format".
 * See <a href="https://genome.ucsc.edu/goldenPath/help/chain.html">the UCSC specification</a>.
 * @author Douglas Myers-Turnbull
 */
@Immutable
public class GenomeChain implements Function<Locus, Optional<Locus>> {

	private static final Logger sf_logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	// only compares loci of the same chromosome and strand
	private static Comparator<LocusRange> sf_comparator
			= (o1, o2) -> ((Long) o1.getEnd().getPosition()).compareTo(o2.getEnd().getPosition());

	private final ImmutableMap<ChromosomeName, ImmutableSortedMap<LocusRange, LocusRange>> m_map;

	private GenomeChain(@Nonnull Builder builder) {
		Map<ChromosomeName, ImmutableSortedMap<LocusRange, LocusRange>> map = new HashMap<>();
		builder.m_map.forEach((name, values) -> map.put(name, ImmutableSortedMap.copyOfSorted(values)));
		m_map = ImmutableMap.copyOf(map);
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

		@Nonnull
		public Builder addMapEntry(@Nonnull LocusRange source, @Nonnull LocusRange target) {

			final long sourceSize = source.length();
			final long targetSize = target.length();

			Preconditions.checkArgument(sourceSize == targetSize,
			                            source + " has size " + sourceSize
					                            + " but " + target + " has size " + targetSize);

			final ChromosomeName sourceChr = source.getStart().getChromosome();
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

}
