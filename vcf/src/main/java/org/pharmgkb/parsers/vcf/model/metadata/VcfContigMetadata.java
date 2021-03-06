package org.pharmgkb.parsers.vcf.model.metadata;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;
import org.pharmgkb.parsers.ObjectBuilder;
import org.pharmgkb.parsers.vcf.model.extra.ReservedStructuralVariantCode;
import org.pharmgkb.parsers.vcf.utils.PropertyMapBuilder;
import org.pharmgkb.parsers.vcf.utils.VcfPatterns;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.Map;
import java.util.Optional;

/**
 * VCF metadata for ##CONTIG lines.
 * @author Douglas Myers-Turnbull
 */
@Immutable
public class VcfContigMetadata extends VcfIdMetadata {

	public static final String ID = "ID";
	public static final String LENGTH = "length";
	public static final String ASSEMBLY = "assembly";
	public static final String MD5 = "md5";
	public static final String SPECIES = "species";
	public static final String TAXONOMY = "taxonomy";
	public static final String URL = "URL";

	private static final ImmutableSet<String> m_forbiddenIds;
	static {
		ImmutableSet.Builder<String> builder = new ImmutableSet.Builder<>();
		for (ReservedStructuralVariantCode code : ReservedStructuralVariantCode.values()) {
			builder.add(code.name());
		}
		m_forbiddenIds = builder.build();
	}

	private final long m_length;

	public VcfContigMetadata(@Nonnull Map<String, String> props) {
		super(VcfMetadataType.Contig, props);
		super.require(ID, LENGTH);
		super.ensureNoExtras(ID, LENGTH, ASSEMBLY, MD5, SPECIES, TAXONOMY, URL);
		Preconditions.checkArgument(
				VcfPatterns.CONTIG_ID_PATTERN.matcher(getId()).matches(),
				"CONTIG ID must match " + VcfPatterns.CONTIG_ID_PATTERN.pattern()
		);
		Preconditions.checkArgument(
				!m_forbiddenIds.contains(getId()),
				"CONTIG ID cannot be a reserved structural variant code"
		);
		m_length = Long.parseLong(props.get(LENGTH));
	}

  public VcfContigMetadata(
  		@Nonnull String id, long length, @Nonnull String assembly, @Nullable String md5,
		@Nullable String species, @Nullable String taxonomy, @Nullable String url
  ) {
		this(new PropertyMapBuilder()
				.put(ID, id)
				.put(LENGTH, String.valueOf(length))
				.put(MD5, md5)
				.put(SPECIES, species)
				.put(TAXONOMY, taxonomy)
				.put(URL, url)
				.put(LENGTH, String.valueOf(length))
				.build());
		// TODO: This isn't needed, right?
		//m_length = length;
	}


	public long getLength() {
	return m_length;
  }

	@Nonnull
	public String getAssembly() {
		return getPropertyRaw(ASSEMBLY).orElseThrow(() -> new IllegalStateException("Contig is missing required property " + ASSEMBLY));
	}

	@Nonnull
	public Optional<String> getTaxonomy() {
	return getPropertyRaw(TAXONOMY);
  }

	@Nonnull
	public Optional<String> getSpecies() {
	return getPropertyUnquoted(SPECIES);
  }

	@Nonnull
	public Optional<String> getMd5() {
		return getPropertyRaw(MD5);
	}

	@Nonnull
	public Optional<String> getUrl() {
	return getPropertyRaw(URL);
  }

	@NotThreadSafe
	public static class Builder implements ObjectBuilder<VcfContigMetadata> {

		private final String m_id;
		private final int m_length;

		private String m_assembly = null;
		private String m_md5 = null;
		private String m_species = null;
		private String m_taxonomy = null;
		private String m_url = null;

		public Builder(@Nonnull String id, @Nonnegative int length) {
			m_id = id;
			m_length = length;
		}

		@Nonnull
		public Builder setAssembly(@Nullable String assembly) {
			m_assembly = assembly;
			return this;
		}

		@Nonnull
		public Builder setMd5(@Nullable String md5) {
			m_md5 = md5;
			return this;
		}

		@Nonnull
		public Builder setSpecies(@Nullable String species) {
			m_species = species;
			return this;
		}

		@Nonnull
		public Builder setTaxonomy(@Nullable String taxonomy) {
			m_taxonomy = taxonomy;
			return this;
		}

		@Nonnull
		public Builder setUrl(@Nullable String url) {
			m_url = url;
			return this;
		}

		@Nonnull
		@Override
		public VcfContigMetadata build() {
			return new VcfContigMetadata(m_id, m_length, m_assembly, m_md5, m_species, m_taxonomy, m_url);
		}
	}

}
