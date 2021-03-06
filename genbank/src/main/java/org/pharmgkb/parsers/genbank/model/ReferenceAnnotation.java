package org.pharmgkb.parsers.genbank.model;

import com.google.common.base.MoreObjects;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Objects;
import java.util.Optional;

/**
 * Author Douglas Myers-Turnbull
 */
@Immutable
public class ReferenceAnnotation implements GenbankAnnotation {

	private final String m_header;
	private final Optional<String> m_authors;
	private final Optional<String> m_consortium;
	private final Optional<String> m_title;
	private final Optional<String> m_journal;
	private final Optional<Integer> m_pubmedId;
	private final Optional<String> m_remark;

	public ReferenceAnnotation(
			@Nonnull String header,
			@Nonnull Optional<String> authors,
			@Nonnull Optional<String> consortium,
			@Nonnull Optional<String> title,
			@Nonnull Optional<String> journal,
			@Nonnull Optional<Integer> pubmedId,
			@Nonnull Optional<String> remark
	) {
		m_header = header;
		m_authors = authors;
		m_consortium = consortium;
		m_title = title;
		m_journal = journal;
		m_pubmedId = pubmedId;
		m_remark = remark;
	}

	@Nonnull
	public String getHeader() {
		return m_header;
	}

	@Nonnull
	public Optional<String> getAuthors() {
		return m_authors;
	}

	@Nonnull
	public Optional<String> getTitle() {
		return m_title;
	}

	@Nonnull
	public Optional<String> getJournal() {
		return m_journal;
	}

	@Nonnegative
	public Optional<Integer> getPubmedId() {
		return m_pubmedId;
	}

	@Nonnull
	public Optional<String> getConsortium() {
		return m_consortium;
	}

	@Nonnull
	public Optional<String> getRemark() {
		return m_remark;
	}

	public boolean isDirectSubmission() {
		return m_title.orElse("-").equals("Direct Submission");
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("header", m_header)
				.add("authors", m_authors)
				.add("consortium", m_consortium)
				.add("title", m_title)
				.add("journal", m_journal)
				.add("pubmedId", m_pubmedId)
				.add("remark", m_remark)
				.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ReferenceAnnotation reference = (ReferenceAnnotation) o;
		return m_pubmedId == reference.m_pubmedId &&
				Objects.equals(m_header, reference.m_header) &&
				Objects.equals(m_authors, reference.m_authors) &&
				Objects.equals(m_consortium, reference.m_consortium) &&
				Objects.equals(m_title, reference.m_title) &&
				Objects.equals(m_journal, reference.m_journal) &&
				Objects.equals(m_remark, reference.m_remark);
	}

	@Override
	public int hashCode() {
		return Objects.hash(m_header, m_authors, m_consortium, m_title, m_journal, m_pubmedId, m_remark);
	}
}
