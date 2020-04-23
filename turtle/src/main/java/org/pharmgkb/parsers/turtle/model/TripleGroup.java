package org.pharmgkb.parsers.turtle.model;

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Objects;


/**
 * @author Douglas Myers-Turnbull
 */
@Immutable
public class TripleGroup {

	private final String m_subject;
	private final ImmutableMap<String, Node> m_triples;

	public TripleGroup(@Nonnull String subject, @Nonnull ImmutableMap<String, Node> triples) {
		this.m_subject = subject;
		this.m_triples = triples;
	}

	@Nonnull
	public String getSubject() {
		return m_subject;
	}

	@Nonnull
	public ImmutableMap<String, Node> getTriples() {
		return m_triples;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TripleGroup that = (TripleGroup) o;
		return Objects.equals(m_subject, that.m_subject) &&
				Objects.equals(m_triples, that.m_triples);
	}

	@Override
	public int hashCode() {
		return Objects.hash(m_subject, m_triples);
	}

	@Override
	public String toString() {
		return "TripleGroup{" +
				"subject='" + m_subject + '\'' +
				", triples=" + m_triples +
				'}';
	}
}
