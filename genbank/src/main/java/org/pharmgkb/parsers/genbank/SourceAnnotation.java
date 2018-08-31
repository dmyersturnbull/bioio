package org.pharmgkb.parsers.genbank;

import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

public class SourceAnnotation implements GenbankAnnotation {

	private String m_name;
	private String m_formalName;
	private List<String> m_lineage;

	public SourceAnnotation(String name, String formalName, List<String> lineage) {
		this.m_name = name;
		this.m_formalName = formalName;
		this.m_lineage = lineage;
	}

	@Nonnull
	public String getName() {
		return m_name;
	}

	@Nonnull
	public String getFormalName() {
		return m_formalName;
	}

	@Nonnull
	public List<String> getLineage() {
		return m_lineage;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("name", m_name)
				.add("formalName", m_formalName)
				.add("lineage", m_lineage)
				.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		SourceAnnotation that = (SourceAnnotation) o;
		return Objects.equals(m_name, that.m_name) &&
				Objects.equals(m_formalName, that.m_formalName) &&
				Objects.equals(m_lineage, that.m_lineage);
	}

	@Override
	public int hashCode() {
		return Objects.hash(m_name, m_formalName, m_lineage);
	}
}
