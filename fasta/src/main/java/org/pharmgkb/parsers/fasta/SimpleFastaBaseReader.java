package org.pharmgkb.parsers.fasta;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;
import java.io.Closeable;
import java.io.EOFException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.nio.file.Path;
import java.util.Optional;

/**
 * A character stream for FASTA, which reads base-by-base.
 *
 * The FASTA grammar is taken to be:
 * <pre>
 *     fasta      ::= '&gt;'header newline sequence (newline fasta)?
 *     header     ::= [^\n\r]+
 *     sequence   ::= [^\n\r]+
 * </pre>
 * Where {@code newline} is taken to be the platform-dependent newline sequence.
 * Notice that, even though the newline is platform-dependent, neither the header nor sequence can contain a CL or LF,
 * which is a platform-independent choice. Also notice that comments and empty lines are not part of the grammar.
 *
 *
 * Example usage:
 * <pre>
 * >gene_1
 * AT
 * >gene_2
 * GC
 * </pre>
 * {@code
 *     FastaStream stream = new FastaStream(file);
 *     stream.currentHeader() // returns Optional.empty()
 *     stream.readNextBase(); // returns 'A'
 *     stream.currentHeader() // returns "gene_1"
 *     stream.readNextBase(); // returns 'T'
 *     stream.currentHeader() // returns "gene_2"
 *     stream.readNextBase(); // returns 'G'
 *     stream.currentHeader() // returns "gene_2"
 *     stream.readNextBase(); // returns 'C'
 *     stream.readNextBase(); // returns Optional.empty()
 * }
 *
 * @author Douglas Myers-Turnbull
 */
@ThreadSafe
public class SimpleFastaBaseReader implements Closeable {

	private final Reader m_reader;

	private CharBuffer m_buffer;
	private String m_header;
	private long m_nBasesSinceHeader;
	private long m_nBasesReadTotal;
	private long m_nHeadersRead;
	private long m_nBytesReadTotal;

	private SimpleFastaBaseReader(@Nonnull Reader reader, @Nonnegative int nCharsInBuffer) throws IOException {
		m_reader = reader;
		m_buffer = CharBuffer.allocate(nCharsInBuffer);
		initBuffer();
	}

	@Override
	public synchronized void close() throws IOException {
		m_reader.close();
	}

	/**
	 * @return The next <em>base (nucleotide or amino acid)</em> in the stream
	 * @throws IOException For IO errors
	 */
	@Nonnull
	public synchronized Optional<Character> readNextBase() throws IOException {
		try {
			Character base;
			do {
				base = doRead();
				if (base == null) {
					return Optional.empty();
				}
			} while (base == '\n' || base == '\r');
			if (base == '>') {
				readHeader();
				base = doRead();
			}
			// no matter what, we always read just one base
			m_nBasesSinceHeader++;
			m_nBasesReadTotal++;
			return Optional.ofNullable(base);
		} catch (RuntimeException e) { // record more info
			throw new IOException(
					"Error reading; " + m_nBytesReadTotal + " bytes read; on header " + m_header
								+ "; buffer has " + m_buffer.remaining() + " remaining",
					e
			);
		}
	}

	/**
	 * @return The last header read, or null if nothing was read yet
	 */
	@Nonnull
	public synchronized Optional<String> currentHeader() {
		return Optional.ofNullable(m_header);
	}

	@Nonnegative
	public synchronized long nHeadersRead() {
		return m_nHeadersRead;
	}

	@Nonnegative
	public synchronized long nBasesReadTotal() {
		return m_nBasesReadTotal;
	}

	@Nonnegative
	public synchronized long nBasesSinceHeader() {
		return m_nBasesSinceHeader;
	}

	@Nonnegative
	public synchronized long nBytesReadTotal() {
		return m_nBytesReadTotal;
	}

	@Nullable
	private Character doRead() throws IOException {
		if (!m_buffer.hasRemaining()) { // fill buffer if it's empty
			if (!initBuffer()) { // if the stream itself is empty
				return null;
			}
		}
		m_nBytesReadTotal++;
		return m_buffer.get();
	}

	private boolean initBuffer() throws IOException {
		m_buffer.clear();
		if (m_reader.read(m_buffer) == -1) { // actually read
			return false;
		}
		m_buffer.flip();
		return true;
	}

	private void readHeader() throws IOException {
		StringBuilder header = new StringBuilder();
		Character c;
		do {
			c = doRead();
			if (c == null) {
				throw new EOFException("Stream ended unexpectedly in header");
			}
			if (c != '\n' && c != '\r') {
				header.append(c);
			}
		} while (c != '\n' && c != '\r');
		m_header = header.toString();
		m_nBasesSinceHeader = 0;
		m_nHeadersRead++;
	}

	@NotThreadSafe
	public static class Builder {

		private Reader m_reader;
		private int m_nCharsInBuffer;

		public Builder(@Nonnull Path file) throws FileNotFoundException {
			this(new FileReader(file.toFile()));
		}

		@Nonnull
		public Builder(@Nonnull File file) throws FileNotFoundException {
			this(new FileReader(file));
		}

		@Nonnull
		public Builder(@Nonnull Reader reader) {
			m_reader = reader;
			m_nCharsInBuffer = 2048;
		}

		@Nonnull
		public Builder setnCharsInBuffer(@Nonnegative int nCharsInBuffer) {
			m_nCharsInBuffer = nCharsInBuffer;
			return this;
		}

		@Nonnull
		public SimpleFastaBaseReader build() throws IOException {
			return new SimpleFastaBaseReader(m_reader, m_nCharsInBuffer);
		}
	}

	@Override
	public String toString() {
		return "SimpleFastaBaseReader{" +
				"reader=" + m_reader +
				", buffer=" + m_buffer +
				", header='" + m_header + '\'' +
				", nBasesSinceHeader=" + m_nBasesSinceHeader +
				", nBasesReadTotal=" + m_nBasesReadTotal +
				", nHeadersRead=" + m_nHeadersRead +
				", nBytesReadTotal=" + m_nBytesReadTotal +
				'}';
	}
}
