package org.pharmgkb.parsers.vcf.model.extra;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableList;
import org.pharmgkb.parsers.ObjectBuilder;
import org.pharmgkb.parsers.model.Locus;
import org.pharmgkb.parsers.vcf.model.VcfPosition;
import org.pharmgkb.parsers.vcf.model.allele.VcfAllele;
import org.pharmgkb.parsers.vcf.utils.VcfAlleleFactory;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A multiploid genotype in VCF.
 * Note that this class has both a builder and static factory methods.
 * Example use:
 * {@code
 * VcfGenotype genotype = VcfGenotype.fromGtString(position, sample.get(ReservedFormatProperty.Genotype));
 * }
 * @author Douglas Myers-Turnbull
 */
@Immutable
public class VcfGenotype {

	private final ImmutableList<Optional<VcfAllele>> m_alleles;

	// indicies in REF and ALT, REF=0, first ALT=1, ...
	// warning: elements can be null!!
	private final ImmutableList<Integer> m_indices;

	private final boolean m_isPhased;

	@Nonnull
	public static Optional<VcfGenotype> fromGtString(@Nonnull VcfPosition position, @Nonnull Optional<String> gtString) {
		Preconditions.checkNotNull(position, "VcfPosition cannot be null");
		Preconditions.checkNotNull(gtString, "Genotype string cannot be null");
		return gtString.map(gt -> new VcfGenotype.Builder(position, gt.contains("|"))
				.addAlleles(gt)
				.build()
		);
	}

	@Nonnull
	public static VcfGenotype fromGtString(@Nonnull VcfPosition position, @Nonnull String gtString) {
		Preconditions.checkNotNull(position, "VcfPosition cannot be null");
		Preconditions.checkNotNull(gtString, "Genotype string cannot be null");
		return new VcfGenotype.Builder(position, gtString.contains("|"))
				.addAlleles(gtString)
				.build();
	}

	private VcfGenotype(@Nonnull Builder builder) {
		m_isPhased = builder.m_isPhased;
		m_alleles = ImmutableList.copyOf(builder.m_alleles);
		m_indices = ImmutableList.copyOf(builder.m_indices);
	}

	@Nonnegative
	public int ploidy() {
		return m_alleles.size();
	}

	@Nonnull
	public ImmutableList<Optional<VcfAllele>> getAlleles() {
		return m_alleles;
	}

	@Nonnull
	public ImmutableList<Integer> getIndices() {
		return m_indices;
	}

	@Nonnull
	public Optional<VcfAllele> getAllele(@Nonnegative int index) {
		return m_alleles.get(index);
	}

	public boolean isPhased() {
		return m_isPhased;
	}

	@Nonnull
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("alleles", m_alleles.stream().map(Object::toString).collect(Collectors.joining(",")))
				.add("indices", m_indices.stream().map(Object::toString).collect(Collectors.joining(",")))
				.add("isPhased", m_isPhased)
				.toString();
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		VcfGenotype that = (VcfGenotype) o;
		return m_isPhased == that.m_isPhased &&
				Objects.equals(m_alleles, that.m_alleles) &&
				Objects.equals(m_indices, that.m_indices);
	}

	@Override
	public int hashCode() {
		return Objects.hash(m_alleles, m_indices, m_isPhased);
	}

	/**
	 * @return A string like {@code A|<xx>|*};
	 * in other words, like {@link #toVcfString()} except with {@link VcfAllele#toVcfString()} instead of the numeric index.
	 */
	@Nonnull
	public String toSimpleString() {
		String delimiter = m_isPhased? "|" : "/";
		return m_alleles.stream()
				.map(o -> o.map(VcfAllele::toVcfString).orElse("."))
				.collect(Collectors.joining(delimiter));
	}

	/**
	 * @return A string like {@code 0|1} or {@code 0/0}.
	 */
	@Nonnull
	public String toVcfString() {
		String delimiter = m_isPhased? "|" : "/";
		return m_indices.stream()
				.map(Object::toString)
				.collect(Collectors.joining(delimiter));
	}

	@NotThreadSafe
	public static class Builder implements ObjectBuilder<VcfGenotype> {

		private static final Splitter sf_slashOrBar = Splitter.on(Pattern.compile("[|/]"));

		private final List<VcfAllele> m_knownAlleles;
		private final Locus m_locus;
		private final boolean m_isPhased;

		private Optional<Integer> m_ploidy;
		private List<Optional<VcfAllele>> m_alleles;
		private List<Integer> m_indices; // indicies in REF and ALT, REF=0, first ALT=1, ...

		public Builder(@Nonnull VcfPosition position, boolean isPhased) {
			Preconditions.checkNotNull(position, "VcfPosition cannot be null");
			m_isPhased = isPhased;
			m_ploidy = Optional.empty();
			m_knownAlleles = position.getAllAlleles();
			m_locus = position.getLocus();
			m_alleles = new ArrayList<>(2);
			m_indices = new ArrayList<>(2);
		}

		public Builder(@Nonnull VcfGenotype genotype, @Nonnull VcfPosition position) {
			Preconditions.checkNotNull(genotype, "Genotype cannot be null");
			Preconditions.checkNotNull(position, "VcfPosition cannot be null");
			m_ploidy = Optional.of(genotype.ploidy());
			m_knownAlleles = position.getAllAlleles();
			m_locus = position.getLocus();
			m_alleles = new ArrayList<>(genotype.m_alleles);
			m_indices = new ArrayList<>(genotype.m_indices);
			m_isPhased = genotype.m_isPhased;
		}

		public Builder(@Nonnull Builder builder) {
			Preconditions.checkNotNull(builder, "Builder cannot be null");
			m_ploidy = builder.m_ploidy;
			m_knownAlleles = builder.m_knownAlleles;
			m_locus = builder.m_locus;
			m_alleles = new ArrayList<>(builder.m_alleles);
			m_indices = new ArrayList<>(builder.m_indices);
			m_isPhased = builder.m_isPhased;
		}

		/**
		 * If this is called, then {@link #build()} will throw an {@link IllegalStateException} if there are not exactly
		 * this many alleles.
		 */
		@Nonnull
		public Builder requirePloidy(@Nonnegative int ploidy) {
			Preconditions.checkArgument(ploidy > -1, "Ploidy must be nonnegative");
			m_ploidy = Optional.of(ploidy);
			return this;
		}

		/**
		 * Adds an empty allele.
		 */
		@Nonnull
		public Builder addNullAllele() {
			m_alleles.add(Optional.empty());
			m_indices.add(null);
			return this;
		}

		@Nonnull
		public Builder addAllele(@Nonnull String allele) {
			Preconditions.checkNotNull(allele, "Allele cannot be null");
			return addAlleles(VcfAlleleFactory.translate(allele));
		}

		/**
		 * @throws IndexOutOfBoundsException If an allele is not recognized
		 */
		@Nonnull
		public Builder addAlleles(@Nonnull VcfAllele... alleles) {
			for (VcfAllele allele : alleles) {
				Preconditions.checkNotNull(allele, "Allele cannot be null");
				int index = m_knownAlleles.indexOf(allele);
				Preconditions.checkArgument(index > 0,
						"Allele " + allele + " not contained in position " + m_locus);
				addAlleles(index);
			}
			return this;
		}

		/**
		 * @throws IndexOutOfBoundsException If an allele index is out of bounds
		 */
		@Nonnull
		public Builder addAlleles(@Nonnull int... indices) {
			List<Integer> list = new ArrayList<>(indices.length);
			for (int index : indices) {
				if (index >= m_knownAlleles.size()) {
					throw new IndexOutOfBoundsException("Index " + index + " is out of bounds (expected < " + m_knownAlleles.size() + ")");
				}
				list.add(index);
			}
			return addAlleles(list);
		}

		/**
		 * @throws IndexOutOfBoundsException If an allele index is out of bounds
		 */
		@Nonnull
		public Builder addAlleles(@Nonnull Collection<Integer> indices) {
			Preconditions.checkNotNull(indices, "Indicies collection cannot be null");
			List<VcfAllele> alleles = m_knownAlleles;
			for (int index : indices) {
				Preconditions.checkArgument(index > -1 && index < alleles.size(),
						"Index " + index + " is out of bounds (0 to " + alleles.size() + ")");
				m_alleles.add(Optional.of(alleles.get(index)));
				m_indices.add(index);
			}
			return this;
		}

		@Nonnull
		public Builder removeAlleles(@Nonnull int... indices) {
			for (int index : indices) m_alleles.remove(index);
			for (int index : indices) m_indices.remove(index);
			return this;
		}

		/**
		 * @throws IndexOutOfBoundsException If an allele index is out of bounds
		 */
		@Nonnull
		public Builder removeAlleles(@Nonnull Collection<Integer> indices) {
			Preconditions.checkNotNull(indices, "Indicies collection cannot be null");
			for (int index : indices) m_alleles.remove(index);
			m_indices.removeAll(indices);
			return this;
		}

		/**
		 * @throws IllegalArgumentException If the ploidy is not what was required
		 */
		@Nonnull
		@Override
		public VcfGenotype build() {
			Preconditions.checkState(
					m_ploidy.isEmpty() || m_alleles.size() != m_ploidy.get(),
					"Required ploidy " + m_ploidy + " but got " + m_alleles.size()
			);
			return new VcfGenotype(this);
		}

		@Nonnull
		public Builder addAlleles(@Nonnull String gtString) {

			Preconditions.checkNotNull(gtString, "Genotype string cannot be null");

			boolean hasBar = gtString.contains("|");
			boolean hasSlash = gtString.contains("/");
			Preconditions.checkArgument(
					(hasBar ^ hasSlash) && (m_isPhased && hasBar || !m_isPhased && hasSlash),
					"Genotype VCF string " + gtString
							+ " must be either fully phased (|) or fully unphased (/); expected "
							+ (m_isPhased? "phased" : "unphased")
			);

			for (String s : sf_slashOrBar.splitToList(gtString)) {
				if (s.equals(".")) {
					addNullAllele();
				} else {
					try {
						addAlleles(Integer.parseInt(s));
					} catch (NumberFormatException e) {
						throw new IllegalArgumentException("Invalid VCF genotype string " + gtString, e);
					}
				}
			}
			return this;
		}
	}

}
