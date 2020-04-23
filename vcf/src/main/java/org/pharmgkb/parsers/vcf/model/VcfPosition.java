package org.pharmgkb.parsers.vcf.model;

import com.google.common.base.*;
import com.google.common.base.Objects;
import com.google.common.collect.*;
import org.pharmgkb.parsers.ObjectBuilder;
import org.pharmgkb.parsers.model.GeneralizedBigDecimal;
import org.pharmgkb.parsers.model.Locus;
import org.pharmgkb.parsers.model.Strand;
import org.pharmgkb.parsers.vcf.model.allele.VcfAllele;
import org.pharmgkb.parsers.vcf.model.allele.VcfBasesAllele;
import org.pharmgkb.parsers.vcf.model.extra.ReservedFormatProperty;
import org.pharmgkb.parsers.vcf.model.extra.VcfGenotype;
import org.pharmgkb.parsers.vcf.utils.VcfAlleleFactory;
import org.pharmgkb.parsers.vcf.utils.VcfPatterns;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import java.lang.invoke.MethodHandles;
import java.util.*;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Stores the entire contents of a single non-metadata (and non-header) VCF line.
 * Note that this includes all sample data. Also be aware that positions are 0-based while VCF itself is 1-based.
 * @author Douglas Myers-Turnbull
 */
@Immutable
public class VcfPosition {

	private static final Logger sf_logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private final String m_chromosome;
	private final long m_position;
	private final ImmutableList<String> m_ids;
	private final VcfBasesAllele m_ref;
	private final ImmutableList<VcfAllele> m_alts;
	private final Optional<GeneralizedBigDecimal> m_quality;
	private final ImmutableList<String> m_filters;
	private final VcfInfo m_info;
	private final ImmutableList<String> m_format;
	private final ImmutableList<VcfSample> m_samples;

	@Nonnull
	public ImmutableList<String> getFormat() {
		return m_format;
	}

	@Nonnull
	public String getChromosome() {
		return m_chromosome;
	}

	/**
	 * Returns the 0-based position.
	 * @return May be -1 for telomers; nonnegative otherwise
	 */
	public long getPosition() {
		return m_position;
	}

	/**
	 * @return The 0-based locus; note that the {@link Strand} is always {@link Strand#PLUS +}.
	 */
	@Nonnull
	public Locus getLocus() {
		return new Locus(m_chromosome, m_position, Strand.PLUS);
	}

	@Nonnull
	public ImmutableList<String> getIds() {
		return m_ids;
	}

	@Nonnull
	public ImmutableList<VcfAllele> getAllAlleles() {
		// note that this isn't fast
		return new ImmutableList.Builder<VcfAllele>().add(m_ref).addAll(m_alts).build();
	}

	@Nonnull
	public VcfAllele getRef() {
		return m_ref;
	}

	@Nonnull
	public ImmutableList<VcfAllele> getAlts() {
		return m_alts;
	}

	@Nonnull
	public Optional<GeneralizedBigDecimal> getQuality() {
		return m_quality;
	}

	@Nonnull
	public ImmutableList<String> getFilters() {
		return m_filters;
	}

	@Nonnull
	public VcfInfo getInfo() {
		return m_info;
	}

	@Nonnull
	public ImmutableList<VcfSample> getSamples() {
		return m_samples;
	}

	/**
	 * @return A list with one element per sample; an element is {@link Optional#empty()} iff the {@link ReservedFormatProperty#Genotype GT} is not specified for that sample
	 */
	@Nonnull
	public ImmutableList<Optional<VcfGenotype>> getGenotypes() {
		ImmutableList.Builder<Optional<VcfGenotype>> builder = new ImmutableList.Builder<>();
		for (int i = 0; i < m_samples.size(); i++) {
			builder.add(getGenotype(i));
		}
		return builder.build();
	}

	/**
	 * @param index Starting at 0
	 */
	@Nonnull
	public Optional<VcfGenotype> getGenotype(@Nonnegative int index) {
		return VcfGenotype.fromGtString(this, m_samples.get(index).get(ReservedFormatProperty.Genotype));
	}

	private VcfPosition(@Nonnull Builder builder) {
		m_chromosome = builder.m_chromosome;
		m_position = builder.m_position;
		m_ids = ImmutableList.copyOf(builder.m_ids);
		m_ref = builder.m_ref;
		m_alts = ImmutableList.copyOf(builder.m_alts);
		m_quality = builder.m_quality;
		m_filters = ImmutableList.copyOf(builder.m_filters);
		m_info = new VcfInfo(ImmutableMultimap.copyOf(builder.m_info));
		m_format = ImmutableList.copyOf(builder.m_format);
		m_samples = ImmutableList.copyOf(builder.m_samples);
	}

	@Override
	public boolean equals(@Nullable Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		VcfPosition that = (VcfPosition) o;
		return com.google.common.base.Objects.equal(m_position, that.m_position) &&
				Objects.equal(m_chromosome, that.m_chromosome) &&
				Objects.equal(m_ids, that.m_ids) &&
				Objects.equal(m_ref, that.m_ref) &&
				Objects.equal(m_alts, that.m_alts) &&
				Objects.equal(m_quality, that.m_quality) &&
				Objects.equal(m_filters, that.m_filters) &&
				Objects.equal(m_info, that.m_info) &&
				Objects.equal(m_format, that.m_format) &&
				Objects.equal(m_samples, that.m_samples);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(m_chromosome, m_position, m_ids, m_ref, m_alts, m_quality, m_filters, m_info, m_format, m_samples);
	}

	@Nonnull
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("chromosome", m_chromosome)
				.add("position", m_position)
				.add("ids", String.join(",", m_ids))
				.add("ref", m_ref)
				.add("alts", m_alts.stream().map(Object::toString).collect(Collectors.joining(",")))
				.add("quality", m_quality)
				.add("filters", String.join(",", m_filters))
				.add("info", m_info.asMap().entrySet().stream()
            .map(e -> e.getKey() + "=" + String.join(",", e.getValue()))
            .collect(Collectors.joining(";")))
				.add("format", String.join(",", m_format))
				.add("samples", m_samples.stream()
            .map(Object::toString)
            .collect(Collectors.joining(",")))
				.toString();
	}

	@NotThreadSafe
	public static class Builder implements ObjectBuilder<VcfPosition> {

		// don't even both checking this; it's HUGE
		private static final Range<Long> sf_forbiddenRange = Range.closed((long)-2E31, (long)-2E31 + 7);

		// these are effectively final
		private String m_chromosome;
		private long m_position;
		private VcfBasesAllele m_ref;

		private List<String> m_ids = new ArrayList<>();
		private List<VcfAllele> m_alts = new ArrayList<>();
		private Optional<GeneralizedBigDecimal> m_quality = Optional.empty();
		private List<String> m_filters = new ArrayList<>();
		private LinkedListMultimap<String, String> m_info = LinkedListMultimap.create();
		private List<String> m_format = new ArrayList<>();
		private List<VcfSample> m_samples = new ArrayList<>();

		public Builder(@Nonnull Builder builder) {
			Preconditions.checkNotNull(builder, "Builder cannot be null");
			m_chromosome = builder.m_chromosome;
			m_position = builder.m_position;
			m_ids.addAll(builder.m_ids);
			m_ref = builder.m_ref;
			m_alts.addAll(builder.m_alts);
			m_quality = builder.m_quality;
			m_filters.addAll(builder.m_filters);
			builder.m_info.entries().forEach(e -> m_info.put(e.getKey(), e.getValue()));
			m_format.addAll(builder.m_format);
			m_format.addAll(builder.m_format);
			m_samples.addAll(builder.m_samples);
		}

		public Builder(@Nonnull VcfPosition position) {
			Preconditions.checkNotNull(position, "VcfPosition cannot be null");
			m_chromosome = position.m_chromosome;
			m_position = position.m_position;
			m_ids.addAll(position.m_ids);
			m_ref = position.m_ref;
			m_alts.addAll(position.m_alts);
			m_quality = position.m_quality;
			m_filters.addAll(position.m_filters);
			position.m_info.entries().forEach(e -> m_info.put(e.getKey(), e.getValue()));
			m_format.addAll(position.m_format);
			m_format = ImmutableList.copyOf(position.m_format);
			m_samples.addAll(position.m_samples);
		}

		public Builder(@Nonnull String chromosome, long position, @Nonnull String ref) {
			VcfAllele translatedRef = VcfAlleleFactory.translate(ref);
			Preconditions.checkArgument(translatedRef instanceof VcfBasesAllele, "REF must match [ATGCNatgcn] but was " + ref);
			init(chromosome, position, (VcfBasesAllele)translatedRef);
		}

		public Builder(@Nonnull String chromosome, long position, @Nonnull VcfBasesAllele ref) {
			init(chromosome, position, ref);
		}

		private void init(@Nonnull String chromosome, long position, @Nonnull VcfBasesAllele ref) {
			Preconditions.checkNotNull(chromosome, "Chromosome cannot be null");
			Preconditions.checkNotNull(ref, "REF cannot be null");
			Preconditions.checkArgument(position > -2, "Position must be -1 or higher (0-based), and -1 only indicates the position is telomeric");
			Preconditions.checkArgument(!sf_forbiddenRange.contains(position), "Int types cannot be between -2E31 and -2E31+7");
			m_chromosome = chromosome;
			m_position = position;
			m_ref = ref;
		}

		@Nonnull
		public Builder addIds(@Nonnull Collection<String> ids) {
			ids.forEach(this::addId);
			return this;
		}

		/**
		 * Appends each ID only if it is not already present
		 */
		@Nonnull
		public Builder addIdsIfNotPresent(@Nonnull Collection<String> ids) {
			ids.forEach(this::addIdIfNotPresent);
			return this;
		}

		@Nonnull
		public Builder addId(@Nonnull String id) {
			Preconditions.checkNotNull(id, "ID cannot be null");
			m_ids.add(id);
			return this;
		}

		/**
		 * Appends the ID only if it is not already present
		 */
		@Nonnull
		public Builder addIdIfNotPresent(@Nonnull String id) {
			Preconditions.checkNotNull(id, "ID cannot be null");
			if (!m_ids.contains(id)) m_ids.add(id);
			return this;
		}

		@Nonnull
		public Builder addAlts(@Nonnull Collection<String> alts) {
			alts.forEach(this::addAlt);
			return this;
		}

		@Nonnull
		public Builder addAlt(@Nonnull String alt) {
			Preconditions.checkNotNull(alt, "ALT cannot be null");
			return addAlt(VcfAlleleFactory.translate(alt));
		}

		@Nonnull
		public Builder addAlt(@Nonnull VcfAllele alt) {
			Preconditions.checkNotNull(alt, "ALT cannot be null");
			m_alts.add(alt);
			return this;
		}

		/**
		 * @throws IllegalArgumentException TODO: If the number is between −231 and −231 + 7, inclusive, which the VCF spec forbids
		 */
		@Nonnull
		public Builder setQuality(@Nonnull Optional<GeneralizedBigDecimal> quality) {
			Preconditions.checkNotNull(quality, "Quality cannot be null");
			m_quality = quality;
			return this;
		}

		@Nonnull
		public Builder addFilters(@Nonnull Collection<String> filters) {
			filters.forEach(this::addFilter);
			return this;
		}

		@Nonnull
		public Builder addFiltersIfNotPresent(@Nonnull Collection<String> filters) {
			filters.forEach(this::addFilterIfNotPresent);
			return this;
		}

		@Nonnull
		public Builder addFilter(@Nonnull String filter) {
			Preconditions.checkNotNull(filter, "FILTER cannot be null");
			Preconditions.checkArgument(!filter.equals("0"), "Filter cannot be 0");
			m_filters.add(filter);
			return this;
		}

		@Nonnull
		public Builder addFilterIfNotPresent(@Nonnull String filter) {
			Preconditions.checkNotNull(filter, "FILTER cannot be null");
			Preconditions.checkArgument(!filter.equals("0"), "Filter cannot be 0");
			if (!m_filters.contains(filter)) m_filters.add(filter);
			return this;
		}

		/**
		 * Removes all filters and sets it to PASS.
		 */
		@Nonnull
		public Builder setFilterToPass() {
			m_filters.clear();
			m_filters.add("PASS");
			return this;
		}

		@Nonnull
		public Builder putInfo(@Nonnull Multimap<String, String> info) {
			info.entries().forEach(e -> putInfo(e.getKey(), e.getValue()));
			return this;
		}

		@Nonnull
		public Builder putInfo(@Nonnull String key, @Nonnull Collection<String> values) {
			values.forEach(v -> putInfo(key, v));
			return this;
		}

		@Nonnull
		public Builder putInfo(@Nonnull String key, @Nonnull String value) {
			Preconditions.checkNotNull(key, "INFO key cannot be null");
			Preconditions.checkNotNull(value, "INFO value cannot be null");
			Preconditions.checkArgument(!value.equals("") || !m_info.containsKey(key),
					"INFO value can only be a singleton list of an empty string or a list of non-empty strings");
			check("INFO key", key, VcfPatterns.SINGLE_INFO_KEY_PATTERN);
			m_info.put(key, value);
			return this;
		}

		@Nonnull
		public Builder addFormats(@Nonnull Collection<String> formats) {
			formats.forEach(this::addFormat);
			return this;
		}

		@Nonnull
		public Builder addFormat(@Nonnull String format) {
			Preconditions.checkNotNull(format, "FORMAT cannot be null");
			check("FORMAT", format, VcfPatterns.SINGLE_FORMAT_PATTERN);
			m_format.add(format);
			return this;
		}

		@Nonnull
		public Builder addSamples(@Nonnull Collection<VcfSample> samples) {
			samples.forEach(this::addSample);
			return this;
		}

		@Nonnull
		public Builder addSample(@Nonnull VcfSample sample) {
			Preconditions.checkNotNull(sample, "Sample cannot be null");
			m_samples.add(sample);
			return this;
		}

		@Nonnull
		public Builder clearIds() {
			m_ids.clear();
			return this;
		}

		@Nonnull
		public Builder clearAlts() {
			m_alts.clear();
			return this;
		}

		@Nonnull
		public Builder clearFilters() {
			m_filters.clear();
			return this;
		}

		@Nonnull
		public Builder clearInfo() {
			m_info.clear();
			return this;
		}

		@Nonnull
		public Builder clearFormat() {
			m_format.clear();
			return this;
		}

		@Nonnull
		public Builder clearSamples() {
			m_samples.clear();
			return this;
		}

		private void check(@Nonnull String type, @Nonnull String string, @Nonnull Pattern pattern) {
			if (!pattern.matcher(string).matches()) {
				throw new IllegalArgumentException(type + " \"" + string + "\" is invalid");
			}
		}

		/**
		 * @throws IllegalStateException For various conditions
		 */
		@Nonnull
		@Override
		public VcfPosition build() {

			Preconditions.checkState(new HashSet<>(m_alts).size() == m_alts.size(), "Position has a duplicate ALT");
			Preconditions.checkState(m_filters.size() == 1 || !m_filters.contains("PASS"), "FILTER contains PASS and a failed filter");
			Preconditions.checkState(new HashSet<>(m_filters).size() == m_filters.size(), "Position has a duplicate FILTER");
			Preconditions.checkState(new HashSet<>(m_ids).size() == m_ids.size(), "Position has a duplicate ID");
			Preconditions.checkState(new HashSet<>(m_info.keySet()).size() == m_info.keySet().size(), "Position has a duplicate INFO key");

			for (int i = 0; i < m_samples.size(); i++) {
				if (!ImmutableList.copyOf(m_samples.get(i).keySet()).equals(m_format)) {
					throw new IllegalStateException("Sample at index " + i + " has properties [" + String.join(",", m_samples.get(i).keySet())
							+ "], but expected [" + String.join(",", m_format) + "]");
				}
			}

			return new VcfPosition(this);
		}

	}

}
