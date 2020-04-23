package org.pharmgkb.parsers.genbank.model;

import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Objects;

/**
 * Author Douglas Myers-Turnbull
 */
@Immutable
public class DefinitionAnnotation implements GenbankAnnotation {

	private String m_definition;

	public DefinitionAnnotation(@Nonnull String m_definition) {
		this.m_definition = m_definition;
	}

	@Nonnull
	public String getDefinition() {
		return m_definition;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		DefinitionAnnotation that = (DefinitionAnnotation) o;
		return Objects.equals(m_definition, that.m_definition);
	}

	@Override
	public int hashCode() {
		return Objects.hash(m_definition);
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("definition", m_definition)
				.toString();
	}
}

