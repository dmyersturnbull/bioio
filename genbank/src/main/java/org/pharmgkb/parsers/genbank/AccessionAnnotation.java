package org.pharmgkb.parsers.genbank;

import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;
import java.util.Objects;

public class AccessionAnnotation implements GenbankAnnotation {

	private String m_accession;

	public AccessionAnnotation(String accession) {
		m_accession = accession;
	}

	@Nonnull
	public String getAccession() {
		return m_accession;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("accession", m_accession)
				.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		AccessionAnnotation that = (AccessionAnnotation) o;
		return Objects.equals(m_accession, that.m_accession);
	}

	@Override
	public int hashCode() {
		return Objects.hash(m_accession);
	}
}
