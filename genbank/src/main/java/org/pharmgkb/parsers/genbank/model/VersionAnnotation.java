package org.pharmgkb.parsers.genbank.model;

import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Objects;

/**
 * Author Douglas Myers-Turnbull
 */
@Immutable
public class VersionAnnotation implements GenbankAnnotation {

	private String m_accession;
	private String m_versionNumber;

	public VersionAnnotation(String m_accession, String m_versionNumber) {
		this.m_accession = m_accession;
		this.m_versionNumber = m_versionNumber;
	}

	@Nonnull
	public String getAccession() {
		return m_accession;
	}

	@Nonnull
	public String getVersionNumber() {
		return m_versionNumber;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("accession", m_accession)
				.add("versionNumber", m_versionNumber)
				.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		VersionAnnotation that = (VersionAnnotation) o;
		return Objects.equals(m_accession, that.m_accession) &&
				Objects.equals(m_versionNumber, that.m_versionNumber);
	}

	@Override
	public int hashCode() {
		return Objects.hash(m_accession, m_versionNumber);
	}
}
