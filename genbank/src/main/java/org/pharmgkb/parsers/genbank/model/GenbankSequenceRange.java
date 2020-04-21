package org.pharmgkb.parsers.genbank.model;

import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Author Douglas Myers-Turnbull
 */
@Immutable
public class GenbankSequenceRange {

	private static Pattern pattern = Pattern.compile("(complement\\()?(<)?(-?\\d+)\\.{2}(-?\\d+)(>)?\\)?");

	private String m_text;

	public GenbankSequenceRange(String text) {
		m_text = text;
	}

	@Nonnull
	public String getText() {
		return m_text;
	}

	public long start() {
		String x = m_text.replace("complement(", "").replace("<", "");
		return Long.parseLong(x.split("\\.{2}")[0]);
	}

	public long end() {
		String x = m_text.replace(")", "").replace(">", "");
		return Long.parseLong(x.split("\\.{2}")[1]);
	}

	public boolean isComplement() {
		return m_text.contains("complement");
	}

	public boolean isStartPartial() {
		String x = m_text.replace("complement(", "");
		return x.charAt(0) == '<';
	}

	public boolean isEndPartial() {
		String x = m_text.replace(")", "");
		return x.charAt(x.length() - 1) == '>';
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("m_text", m_text)
				.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		GenbankSequenceRange that = (GenbankSequenceRange) o;
		return Objects.equals(m_text, that.m_text);
	}

	@Override
	public int hashCode() {
		return Objects.hash(m_text);
	}
}
