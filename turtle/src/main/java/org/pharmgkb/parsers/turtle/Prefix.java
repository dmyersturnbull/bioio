package org.pharmgkb.parsers.turtle;

import com.google.common.base.MoreObjects;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.util.Objects;


@Immutable
public class Prefix implements Serializable {

	private static final long serialVersionUID = -6130391673700012465L;

	private String m_prefix;
	private String m_uri;

	public Prefix(@Nonnull String prefix, @Nonnull String uri) {
		m_prefix = prefix;
		m_uri = uri;
	}

	@Nonnull
	public String getPrefix() {
		return m_prefix;
	}

	@Nonnull
	public String getUri() {
		return m_uri;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Prefix prefix = (Prefix) o;
		return Objects.equals(m_prefix, prefix.m_prefix) &&
				Objects.equals(m_uri, prefix.m_uri);
	}

	@Override
	public int hashCode() {
		return Objects.hash(m_prefix, m_uri);
	}

	@Nonnull
	public String asString() {
		return "@prefix " + m_prefix + ": <" + m_uri + "> .";
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("prefix", m_prefix)
				.add("uri", m_uri)
				.toString();
	}
}
