package org.pharmgkb.parsers.fasta.model;

import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.util.Objects;

/**
 * A fasta sequence and header.
 *
 * @author Douglas Myers-Turnbull
 */
@Immutable
public class FastqSequence implements Comparable<FastqSequence>, HasSequence, HasScores {

	private final String m_header;
	private final String m_sequence;
	private final String m_scores;

	/**
	 * @throws IllegalArgumentException If the header or sequence contains either newline character LF (\n) or CR (\n)
	 */
	public FastqSequence(@Nonnull String header, @Nonnull String sequence, @Nonnull String scores) {
		Preconditions.checkArgument(
				!header.contains("\n") && !header.contains("\r"),
				"Header \"" + header + "\" contains a newline"
		);
		Preconditions.checkArgument(
				!sequence.contains("\n") && !sequence.contains("\r"),
				"Sequence \"" + sequence + "\" contains a newline"
		);
		m_header = header;
		m_sequence = sequence;
		m_scores = scores;
	}

	@Nonnull
	@Override
	public String getHeader() { return m_header; }

	@Nonnull
	@Override
	public String getSequence() { return m_sequence; }

	@Nonnull
	@Override
	public String getScores() { return m_scores; }

	@Nonnull
	@Override
	public String toString() {
		return ">" + m_header + System.lineSeparator() + m_sequence + System.lineSeparator() + "+" + System.lineSeparator() + m_scores;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		FastqSequence that = (FastqSequence) o;
		return Objects.equals(m_header, that.m_header) &&
				Objects.equals(m_sequence, that.m_sequence) &&
				Objects.equals(m_scores, that.m_scores);
	}

	@Override
	public int hashCode() {
		return Objects.hash(m_header, m_sequence, m_scores);
	}

	@Override
	public int compareTo(@Nonnull FastqSequence o) {
		return ComparisonChain.start()
				.compare(m_header, o.m_header)
				.compare(m_sequence, o.m_sequence)
				.compare(m_scores, o.m_scores)
				.result();
	}

}
