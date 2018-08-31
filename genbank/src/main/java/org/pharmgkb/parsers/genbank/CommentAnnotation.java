package org.pharmgkb.parsers.genbank;

import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;
import java.util.Objects;

public class CommentAnnotation implements GenbankAnnotation {

	private String m_text;

	public CommentAnnotation(String text) {
		m_text = text;
	}

	@Nonnull
	public String getText() {
		return m_text;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("text", m_text)
				.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		CommentAnnotation that = (CommentAnnotation) o;
		return Objects.equals(m_text, that.m_text);
	}

	@Override
	public int hashCode() {
		return Objects.hash(m_text);
	}
}
