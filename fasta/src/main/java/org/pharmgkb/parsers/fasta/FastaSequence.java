package org.pharmgkb.parsers.fasta;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.Serializable;

/**
 * A fasta sequence and header.
 *
 * @author Douglas Myers-Turnbull
 */
@Immutable
public class FastaSequence implements Comparable<FastaSequence>, Serializable {

	private static final long serialVersionUID = -9016565377345851470L;

	private final String m_header;

	private final String m_sequence;

	/**
	 * @throws IllegalArgumentException If the header or sequence contains either newline character LF (\n) or CR (\n)
	 */
	public FastaSequence(@Nonnull String header, @Nonnull String sequence) {
		Preconditions.checkArgument(!header.contains("\n") && !header.contains("\r"), "Header \"" + header + "\" contains a newline");
		Preconditions.checkArgument(!sequence.contains("\n") && !sequence.contains("\r"), "Sequence \"" + sequence + "\" contains a newline");
		m_header = header;
		m_sequence = sequence;
	}

	@Nonnull
	public String getHeader() {
		return m_header;
	}

	@Nonnull
	public String getSequence() {
		return m_sequence;
	}

	@Nonnull
	@Override
	public String toString() {
		return ">" + m_header + System.lineSeparator() + m_sequence;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		FastaSequence that = (FastaSequence) o;
		return Objects.equal(m_header, that.m_header) &&
				Objects.equal(m_sequence, that.m_sequence);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(m_header, m_sequence);
	}

	@Override
	public int compareTo(@Nonnull FastaSequence o) {
		return ComparisonChain.start()
				.compare(m_header, o.m_header)
				.compare(m_sequence, o.m_sequence)
				.result();
	}
}
