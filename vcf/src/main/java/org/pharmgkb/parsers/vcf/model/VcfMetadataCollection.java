package org.pharmgkb.parsers.vcf.model;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.pharmgkb.parsers.ObjectBuilder;
import org.pharmgkb.parsers.vcf.model.metadata.*;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import javax.annotation.concurrent.NotThreadSafe;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Stores an ordered list of VCF metadata lines, including the {@code ##vcfVersion} and header ({@code #CHROM...}) lines.
 * Also provides fast access to reserved metatadata types. For example, you can get all INFO metadata by {@link #getInfo()}.
 * @author Douglas Myers-Turnbull
 */
@Immutable
public class VcfMetadataCollection {

	private final VcfVersionMetadata m_vcfVersion;
	private final VcfHeaderMetadata m_header;

	private final ImmutableList<VcfMetadata> m_lines;

	private final ImmutableMap<String, VcfAltMetadata> m_alt;
	private final ImmutableMap<String, VcfFilterMetadata> m_filter;
	private final ImmutableMap<String, VcfFormatMetadata> m_format;
	private final ImmutableMap<String, VcfInfoMetadata> m_info;
	private final ImmutableMap<String, VcfSampleMetadata> m_sample;
	private final ImmutableMap<String, VcfContigMetadata> m_contig;

	private final ImmutableList<VcfPedigreeMetadata> m_pedigree;
	private final ImmutableList<VcfRawMetadata> m_assembly;
	private final ImmutableList<VcfRawMetadata> m_pedigreeDb;

	/**
	 * @return The VCF header line, minus the {@code ##vcfVersion=}
	 */
	@Nonnull
	public String getVcfVersion() {
		return m_vcfVersion.getVersionNumber();
	}

	@Nonnull
	public ImmutableList<VcfMetadata> getLines() {
		return m_lines;
	}

	@Nonnull
	public ImmutableMap<String, VcfAltMetadata> getAlt() {
		return m_alt;
	}

	@Nonnull
	public ImmutableMap<String, VcfFilterMetadata> getFilter() {
		return m_filter;
	}

	@Nonnull
	public ImmutableMap<String, VcfFormatMetadata> getFormat() {
		return m_format;
	}

	@Nonnull
	public ImmutableMap<String, VcfInfoMetadata> getInfo() {
		return m_info;
	}

	@Nonnull
	public ImmutableMap<String, VcfSampleMetadata> getSample() {
		return m_sample;
	}

	@Nonnull
	public ImmutableMap<String, VcfContigMetadata> getContig() {
		return m_contig;
	}

	@Nonnull
	public ImmutableList<VcfPedigreeMetadata> getPedigree() {
		return m_pedigree;
	}

	@Nonnull
	public ImmutableList<VcfRawMetadata> getAssembly() {
		return m_assembly;
	}

	@Nonnull
	public ImmutableList<VcfRawMetadata> getPedigreeDb() {
		return m_pedigreeDb;
	}

	@Nonnull
	public VcfHeaderMetadata getHeader() {
		return m_header;
	}

	/**
	 * Convenience method for {@link VcfHeaderMetadata#getSampleNames()}.
	 * @return The names of the VCF samples, in order
	 */
	@Nonnull
	public ImmutableList<String> getSampleNames() {
		return m_header.getSampleNames();
	}

	private VcfMetadataCollection(@Nonnull Builder builder) {

		m_vcfVersion = builder.m_vcfVersion;

		m_lines = ImmutableList.copyOf(builder.m_lines);
		m_alt = ImmutableMap.copyOf(builder.m_alt);
		m_filter = ImmutableMap.copyOf(builder.m_filter);
		m_format = ImmutableMap.copyOf(builder.m_format);
		m_info = ImmutableMap.copyOf(builder.m_info);
		m_sample = ImmutableMap.copyOf(builder.m_sample);
		m_contig = ImmutableMap.copyOf(builder.m_contig);

		m_pedigree = ImmutableList.copyOf(builder.m_pedigree);
		m_assembly = ImmutableList.copyOf(builder.m_assembly);
		m_pedigreeDb = ImmutableList.copyOf(builder.m_pedigreeDb);

		m_header = builder.m_header;
	}

	@NotThreadSafe
	public static class Builder implements ObjectBuilder<VcfMetadataCollection> {

		private VcfVersionMetadata m_vcfVersion = null;
		private VcfHeaderMetadata m_header = null;

		private List<VcfMetadata> m_lines = new ArrayList<>();

		private Map<String, VcfAltMetadata> m_alt = new LinkedHashMap<>();
		private Map<String, VcfFilterMetadata> m_filter = new LinkedHashMap<>();
		private Map<String, VcfFormatMetadata> m_format = new LinkedHashMap<>();
		private Map<String, VcfInfoMetadata> m_info = new LinkedHashMap<>();
		private Map<String, VcfSampleMetadata> m_sample = new LinkedHashMap<>();
		private Map<String, VcfContigMetadata> m_contig = new LinkedHashMap<>();

		private List<VcfPedigreeMetadata> m_pedigree = new ArrayList<>();
		private List<VcfRawMetadata> m_assembly = new ArrayList<>();
		private List<VcfRawMetadata> m_pedigreeDb = new ArrayList<>();

		public Builder() {

		}

		/**
		 * Returns a new Builder by adding the metadata lines of the elements of {@code collections}, in order.
		 */
		public Builder(@Nonnull VcfMetadataCollection... collections) {
			for (VcfMetadataCollection collection : collections) {
				Preconditions.checkNotNull(collection, "VcfMetadataCollection cannot be null");
				collection.m_lines.forEach(this::addLine);
			}
		}

		/**
		 * Returns a new Builder by adding the metadata lines of the elements of {@code builders}, in order.
		 */
		public Builder(@Nonnull Builder... builders) {
			for (Builder builder : builders) {
				Preconditions.checkNotNull(builder, "Builder cannot be null");
				builder.m_lines.forEach(this::addLine);
			}
		}

		@SuppressWarnings("OverlyStrongTypeCast")
		@Nonnull
		public Builder addLine(@Nonnull VcfMetadata line) {
			Preconditions.checkNotNull(line, "VcfMetadata cannot be null");
			m_lines.add(line);
			if (line instanceof VcfVersionMetadata) {
				if (m_vcfVersion != null) throw new IllegalArgumentException("Duplicate VCF version line");
				m_vcfVersion = ((VcfVersionMetadata)line);
			}
			if (line instanceof VcfAltMetadata) {
				String id = ((VcfAltMetadata)line).getId();
				if (m_alt.containsKey(id)) throw new IllegalArgumentException("Duplicate ALT metadata with ID " + id);
				m_alt.put(id, (VcfAltMetadata)line);
			}
			if (line instanceof VcfFilterMetadata) {
				String id = ((VcfFilterMetadata)line).getId();
				if (m_filter.containsKey(id)) throw new IllegalArgumentException("Duplicate FILTER metadata with ID " + id);
				m_filter.put(id, (VcfFilterMetadata)line);
			}
			if (line instanceof VcfFormatMetadata) {
				String id = ((VcfFormatMetadata)line).getId();
				if (m_format.containsKey(id)) throw new IllegalArgumentException("Duplicate FORMAT metadata with ID " + id);
				m_format.put(id, (VcfFormatMetadata)line);
			}
			if (line instanceof VcfInfoMetadata) {
				String id = ((VcfInfoMetadata)line).getId();
				if (m_info.containsKey(id)) throw new IllegalArgumentException("Duplicate INFO metadata with ID " + id);
				m_info.put(id, (VcfInfoMetadata) line);
			}
			if (line instanceof VcfSampleMetadata) {
				String id = ((VcfSampleMetadata)line).getId();
				if (m_sample.containsKey(id)) throw new IllegalArgumentException("Duplicate SAMPLE metadata with ID " + id);
				m_sample.put(id, (VcfSampleMetadata)line);
			}
			if (line instanceof VcfContigMetadata) {
				String id = ((VcfContigMetadata)line).getId();
				if (m_contig.containsKey(id)) throw new IllegalArgumentException("Duplicate CONTIG metadata with ID " + id);
				m_contig.put(id, (VcfContigMetadata)line);
			}
			if (line instanceof VcfPedigreeMetadata) {
				// TODO should we check for duplicate full lines?
				m_pedigree.add((VcfPedigreeMetadata) line);
			}
			if (line instanceof VcfRawMetadata && line.toString().startsWith("##assembly")) {
				m_assembly.add((VcfRawMetadata)line);
			}
			if (line instanceof VcfRawMetadata && line.toString().startsWith("##pedigreeDB")) {
				m_pedigreeDb.add((VcfRawMetadata)line);
			}
			if (line instanceof VcfHeaderMetadata) {
				if (m_header != null) throw new IllegalArgumentException("Duplicate VCF header");
				m_header = ((VcfHeaderMetadata)line);
			}
			return this;
		}

		/**
		 * @throws IllegalStateException If no {@link VcfHeaderMetadata}, or if no {@link VcfVersionMetadata} was added
		 */
		@Nonnull
		@Override
		public VcfMetadataCollection build() {
			Preconditions.checkState(m_vcfVersion != null, "VCF version was not set");
			Preconditions.checkState(m_header != null, "VCF header line (starting with a single #) was not set");
			return new VcfMetadataCollection(this);
		}
	}

	@Override
	public String toString() {
		return "VcfMetadataCollection{" + m_lines.size() + " lines}";
	}
}
