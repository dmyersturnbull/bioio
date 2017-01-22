package org.pharmgkb.parsers.vcf.model.extra;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.pharmgkb.parsers.model.GeneralizedBigDecimal;
import org.pharmgkb.parsers.vcf.model.VcfPosition;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Parses out the {@code GL} string into a map from {@link VcfGenotype genotypes} to their likelihoods.
 * @author Douglas Myers-Turnbull
 */
public class GenotypeLikelihoods {

	private final VcfPosition m_position;

	public GenotypeLikelihoods(@Nonnull VcfPosition position) {
		m_position = position;
	}

	/**
	 * Returns a map from each {@link VcfGenotype} to its likelihood, or {@link Optional#empty()} if the {@code GL} is not present.
	 * TODO Fix
	 */
	@Nonnull
	public Optional<ImmutableMap<VcfGenotype, GeneralizedBigDecimal>> getLikelihoods(@Nonnegative int index) {

		VcfGenotype genotype = m_position.getGenotype(index).orElse(null);
		int ploidy = genotype==null? 2 : genotype.ploidy(); // VCF spec says assume diploid
		boolean isPhased = genotype != null && genotype.isPhased();

		List<GeneralizedBigDecimal> likelihoods = (List<GeneralizedBigDecimal>) m_position.getSamples()
				.get(index)
				.getConverted(ReservedFormatProperty.GenotypeLikelihoods)
				.orElse(null);
		if (likelihoods == null) return Optional.empty();

		List<VcfGenotype> genotypes =
				ordering(ploidy, m_position.getAlts().size() + 1, ImmutableList.of(), new ArrayList<>(ploidy*(m_position.getAlts().size()+1)))
						.stream()
						.map(l -> new VcfGenotype.Builder(m_position, isPhased)
								.requirePloidy(ploidy)
//								.addAlleles() // TODO
								.build()
						).collect(Collectors.toList());

		ImmutableMap.Builder<VcfGenotype, GeneralizedBigDecimal> map = new ImmutableMap.Builder<>();
		if (genotypes.size() != likelihoods.size()) {
			throw new IllegalArgumentException("Length of GL does not match length expected from GT");
		}
		for (int i = 0; i < genotypes.size(); i++) {
			map.put(genotypes.get(i), likelihoods.get(i));
		}

		return Optional.of(ImmutableMap.copyOf(map.build()));
	}

	private List<ImmutableList<Integer>> ordering(int p, int n, List<Integer> suffix, List<ImmutableList<Integer>> results) {
		/*
		Ordering (P , N , suffix =""):
		for a in 0 . . . N
		if (P == 1) println str (a) + suffix
		if (P > 1) Ordering (P -1, a, str (a) + suffix )
		 */
		for (int i = 0; i < n; i++) {
			ImmutableList<Integer> appended = new ImmutableList.Builder<Integer>()
					.add(i).addAll(suffix)
					.build();
			if (p == 1) results.add(appended);
			if (p > 1) return ordering(p - 1, n, appended, results);
		}
		return results;
	}

}
