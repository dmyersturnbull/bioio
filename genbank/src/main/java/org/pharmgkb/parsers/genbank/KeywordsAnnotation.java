package org.pharmgkb.parsers.genbank;

import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;

public class KeywordsAnnotation implements GenbankAnnotation {

	private List<String> keywords;

	public KeywordsAnnotation(@Nonnull List<String> keywords) {
		this.keywords = keywords;
	}

	@Nonnull
	public List<String> getKeywords() {
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
