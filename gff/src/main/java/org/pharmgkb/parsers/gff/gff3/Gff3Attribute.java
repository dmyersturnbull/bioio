package org.pharmgkb.parsers.gff.gff3;

import javax.annotation.Nonnull;

/**
 * Optional GFF3 attributes, which are in the last column.
 * @author Douglas Myers-Turnbull
 */
public enum Gff3Attribute {

	Id("ID"), Name("Name"), Alias("Alias"), Parent("Parent"), Target("Target"), Gap("Gap"), DerivesFrom("Derives_from"), Note("Note"), Dbxref("Dbxref"), Ontology_term("Ontology_term");

	private final String m_id;

	Gff3Attribute(String id) {
		m_id = id;
	}

	@Nonnull
	public String getId() {
		return m_id;
	}
}
