package org.pharmgkb.parsers.genbank.model;

import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Objects;

/**
 * Author Douglas Myers-Turnbull
 */
@Immutable
public class AccessionAnnotation implements GenbankAnnotation {

	private final String m_accession;

	public AccessionAnnotation(@Nonnull String accession) {
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
