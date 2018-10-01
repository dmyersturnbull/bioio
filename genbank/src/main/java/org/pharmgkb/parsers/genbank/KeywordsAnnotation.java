package org.pharmgkb.parsers.genbank;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.List;
import java.util.Objects;

/**
 * Author Douglas Myers-Turnbull
 */
@Immutable
public class KeywordsAnnotation implements GenbankAnnotation {

	private ImmutableList<String> keywords;

	public KeywordsAnnotation(@Nonnull ImmutableList<String> keywords) {
		this.keywords = keywords;
	}

	@Nonnull
	public ImmutableList<String> getKeywords() {
		return keywords;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("keywords", keywords)
				.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		KeywordsAnnotation that = (KeywordsAnnotation) o;
		return Objects.equals(keywords, that.keywords);
	}

	@Override
	public int hashCode() {
		return Objects.hash(keywords);
	}
}
