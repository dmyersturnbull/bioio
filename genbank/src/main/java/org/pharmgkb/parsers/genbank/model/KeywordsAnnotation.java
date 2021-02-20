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
public class KeywordsAnnotation implements GenbankAnnotation {

	private final ImmutableList<String> m_keywords;

	public KeywordsAnnotation(@Nonnull ImmutableList<String> keywords) {
		m_keywords = keywords;
	}

	@Nonnull
	public ImmutableList<String> getKeywords() {
		return m_keywords;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("keywords", m_keywords)
				.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		KeywordsAnnotation that = (KeywordsAnnotation) o;
		return Objects.equals(m_keywords, that.m_keywords);
	}

	@Override
	public int hashCode() {
		return Objects.hash(m_keywords);
	}
}
