package org.pharmgkb.parsers.genbank;

import javax.annotation.Nonnull;

public enum GenbankDivision {

	PRI("primate sequences"),
	ROD("rodent sequences"),
	MAM("other mammalian sequences"),
	VRT("other vertebrate sequences"),
	PLN("plant, fungal, and algal sequences"),
	BCT("bacterial sequences"),
	VRL("viral sequences"),
	PHG("bacteriophage sequences"),
	SYN("synthetic sequences"),
	UNA("unannotated sequences"),
	EST("EST sequences (expressed sequence tags"),
	PAT("patent sequences"),
	STS("STS sequences (sequence tagged sites)"),
	GSS("GSS sequences (genome survey sequences"),
	HTG("HTG sequences (high-throughput genomic sequences"),
	HTC("unfinished high-throughput genomic sequences"),
	ENV("environmental sampling sequences"),
	NONSTANDARD("Divisions not speicified in the GenBank specification");

	private String m_description;

	GenbankDivision(String description) {
		this.m_description = description;
	}

	@Nonnull
	public String getDescription() {
		return m_description;
	}
}
