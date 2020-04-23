package org.pharmgkb.parsers.genbank.model;

import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Objects;

/**
 * Author Douglas Myers-Turnbull
 */
@Immutable
public class CommentAnnotation implements GenbankAnnotation {

	private String m_text;

	public CommentAnnotation(@Nonnull String text) {
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
