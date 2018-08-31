package org.pharmgkb.parsers.genbank;

import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;
import java.util.Objects;

public class OriginAnnotation implements GenbankAnnotation {

	private String m_header;
	private String m_sequence;

	public OriginAnnotation(String data, String sequence) {
		m_header = data;
		m_sequence = sequence;
	}

	public String getHeader() {
		return m_header;
	}

	public String getSequence() {
		return m_sequence;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("header", m_header)
				.add("sequence", m_sequence)
				.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		OriginAnnotation that = (OriginAnnotation) o;
		return Objects.equals(m_header, that.m_header) &&
				Objects.equals(m_sequence, that.m_sequence);
	}

	@Override
	public int hashCode() {
		return Objects.hash(m_header, m_sequence);
	}
}
