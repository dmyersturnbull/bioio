package org.pharmgkb.parsers.genbank.model;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Objects;

/**
 * Author Douglas Myers-Turnbull
 */
@Immutable
public class FeaturesAnnotation implements GenbankAnnotation {

	private final String m_header;
	private final ImmutableList<GenbankFeature> m_features;

	public FeaturesAnnotation(@Nonnull String header, @Nonnull ImmutableList<GenbankFeature> features) {
		m_header = header;
		m_features = features;
	}

	@Nonnull
	public String getHeader() {
		return m_header;
	}

	@Nonnull
	public ImmutableList<GenbankFeature> getFeatures() {
		return m_features;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("header", m_header)
				.add("features", m_features)
				.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		FeaturesAnnotation that = (FeaturesAnnotation) o;
		return Objects.equals(m_header, that.m_header) &&
				Objects.equals(m_features, that.m_features);
	}

	@Override
	public int hashCode() {
		return Objects.hash(m_header, m_features);
	}
}
