package org.pharmgkb.parsers.genbank.model;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Objects;

/**
 * Author Douglas Myers-Turnbull
 */
@Immutable
public class GenbankFeature {

	private String m_kind;
	private GenbankSequenceRange m_range;
	private ImmutableMap<String, String> m_properties;
	private ImmutableList<String> m_extraLines;

	public GenbankFeature(
			@Nonnull String kind,
			@Nonnull GenbankSequenceRange range,
			@Nonnull ImmutableMap<String, String> properties,
			@Nonnull ImmutableList<String> extraLines
	) {
		m_kind = kind;
		m_range = range;
		m_properties = properties;
		m_extraLines = extraLines;
	}

	@Nonnull
	public String getKind() {
		return m_kind;
	}

	@Nonnull
	public GenbankSequenceRange getRange() {
		return m_range;
	}

	/**
	 * All of the properties beginning with '/'.
	 * The slash is removed from the key, and quotes are stripped from the values.
	 * Will be in the same order as the original.
	 */
	@Nonnull
	public ImmutableMap<String, String> getProperties() {
		return m_properties;
	}

	/**
	 * Lines at the end not conforming to GenBank format. Whitespace is still trimmed.
	 */
	@Nonnull
	public ImmutableList<String> getExtraLines() {
		return m_extraLines;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("kind", m_kind)
				.add("range", m_range)
				.add("properties", m_properties)
				.add("extraLines", m_extraLines)
				.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GenbankFeature that = (GenbankFeature) o;
		return Objects.equals(m_kind, that.m_kind) &&
				Objects.equals(m_range, that.m_range) &&
				Objects.equals(m_properties, that.m_properties) &&
				Objects.equals(m_extraLines, that.m_extraLines);
	}

	@Override
	public int hashCode() {
		return Objects.hash(m_kind, m_range, m_properties, m_extraLines);
	}
}
