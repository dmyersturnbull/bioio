package org.pharmgkb.parsers.vcf.model.metadata;

import javax.annotation.Nonnull;

/**
 * A reserved type of metadata lines, with correct capitalization.
 * Note, specifically, that {@link #Contig} is lowercased.
 * @author Douglas Myers-Turnbull
 */
public enum VcfMetadataType {

	Alt("ALT"),
	Contig("contig"),
	Filter("FILTER"),
	Format("FORMAT"),
	Info("INFO"),
	Pedigree("PEDIGREE"),
	Sample("SAMPLE")
	;

	private final String m_id;

	public static final String ALT_ID = "ALT";
	public static final String CONTIG_ID = "contig";
	public static final String FILTER_ID = "FILTER";
	public static final String FORMAT_ID = "FORMAT";
	public static final String INFO_ID = "INFO";
	public static final String PEDIGREE_ID = "PEDIGREE";
	public static final String SAMPLE_ID = "SAMPLE";

	VcfMetadataType(@Nonnull String id) {
		m_id = id;
	}

	@Nonnull
	public String getId() {
		return m_id;
	}
}
