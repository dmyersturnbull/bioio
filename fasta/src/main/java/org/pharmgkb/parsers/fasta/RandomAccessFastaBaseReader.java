package org.pharmgkb.parsers.fasta;

import com.google.common.collect.ImmutableList;
import org.pharmgkb.parsers.BadDataFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.ebi.pride.tools.braf.BufferedRandomAccessFile;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;
import java.io.*;
import java.lang.invoke.MethodHandles;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Optional;

/**
 * A buffered arbitrary-position interface to read FASTA bases.
 *
 * Reads the file once to
 * 1) write a file containing no within-sequence line breaks, and
 * 2) map the position (in bytes) of each header in the new file.
 *
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
 * ATGC
 * </pre>
 * {@code
 * RandomAccessFastaStream stream = new RandomAccessFastaStream.Builder(file).setnCharsInBuffer(1024).build();
 * stream.read("gene_1", 1); // returns 'T'
 * }
 *
 * @author Douglas Myers-Turnbull
 */
@ThreadSafe
public class RandomAccessFastaBaseReader implements Closeable {

	private static final Logger sf_logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private static final String sf_tempExtension = ".no_breaks";

	private File m_originalFile;

	private File m_tempFile;

	private LinkedHashMap<String, Long> m_headerToPosition = new LinkedHashMap<>();
	private BufferedRandomAccessFile m_stream;
	private String m_currentHeader;

	private RandomAccessFastaBaseReader(
			@Nonnull File file, @Nonnegative int nBytesInBuffer, @Nonnull File tempFile,
			boolean keepTempFileOnExit
	) throws IOException, BadDataFormatException {
		init(file, nBytesInBuffer, tempFile, keepTempFileOnExit);
	}

	private void init(
			@Nonnull File file, @Nonnegative int nBytesInBuffer, @Nonnull File temp,
			boolean keepTempFileOnExit
	) throws IOException {

		m_originalFile = file;
		if (temp.exists() && keepTempFileOnExit) { // for safety
			throw new IllegalArgumentException("Temporary file " + temp.getPath() + " already exists; delete first");
		} else {
			m_tempFile = temp;
		}
		if (!keepTempFileOnExit) {
			m_tempFile.deleteOnExit();
		}

		// we're going to read a FASTA stream base-by-base while:
		// 1) writing each base and each new header
		// 2) keeping track of how many bytes into the new file we are
		try (SimpleFastaBaseReader simple = new SimpleFastaBaseReader.Builder(file).build()) {
			try (PrintWriter pw = new PrintWriter(m_tempFile)) {

				long nBytesInNewFile = 0; // we'll use this for header positions
				String currentHeader = null; // we'll need this to know whether a header is new
				Optional<Character> c;

				while (((c = simple.readNextBase()).isPresent())) {

					//noinspection OptionalGetWithoutIsPresent
					final String headerRead = simple.currentHeader().get(); // not null because we read a base

					// only write the header if we just read it
					if (!headerRead.equals(currentHeader)) {

						if (currentHeader == null) {
							// 1 for the >, and 1 for the \n = 2
							nBytesInNewFile += headerRead.length() + 2;
						} else {
							// 1 for the \n, 1 for the >, and 1 for the \n = 3
							nBytesInNewFile += headerRead.length() + 3;
							pw.println();
						}

						//noinspection OptionalGetWithoutIsPresent
						m_headerToPosition.put(simple.currentHeader().get(), nBytesInNewFile);
						sf_logger.debug("{} -----> {} in {}", simple.currentHeader(), nBytesInNewFile, m_tempFile);
						//noinspection OptionalGetWithoutIsPresent
						currentHeader = simple.currentHeader().get();
						pw.println('>' + currentHeader);

					}

					nBytesInNewFile++;
					pw.print(c.get());
					if (nBytesInNewFile % 1000 == 0) {
						pw.flush();
					}
				}

				pw.flush();

			}
		}
		m_stream = new BufferedRandomAccessFile(m_tempFile.getPath(), "r", nBytesInBuffer);
	}

	/**
	 * @return The list of headers (everything after the &gt; sign) in the FASTA file, in order
	 */
	@Nonnull
	public ImmutableList<String> getHeaders() {
		return ImmutableList.copyOf(m_headerToPosition.keySet());
	}

	/**
	 *
	 * @param header The exact FASTA header, without an initial &gt; sign
	 * @param position The number of bases, starting at 0, from the first
	 * @return The nucleotide or amino acid at that position
	 * @throws IOException IO errors
	 * @throws java.lang.IllegalArgumentException If a header with that name is not in the FASTA file
	 */
	public synchronized char read(
			@Nonnull String header,
			@Nonnegative long position
	) throws IOException {
		return read(header, position, 1).charAt(0);
	}
	/**
	 *
	 * @param header The exact FASTA header, without an initial &gt; sign
	 * @param position The number of bases, starting at 0, from the first
	 * @return The nucleotide or amino acid at that position
	 * @throws IOException IO errors
	 * @throws java.lang.IllegalArgumentException If a header with that name is not in the FASTA file
	 */
	public synchronized String read(
			@Nonnull String header,
			@Nonnegative long position,
			@Nonnegative long length
	) throws IOException {
		if (!m_headerToPosition.containsKey(header)) {
			throw new IllegalArgumentException("Header " + header + " not found in FASTA file " + m_originalFile);
		}
		m_currentHeader = header;
		long start = m_headerToPosition.get(header);
		if (start + position < 0) { // throw IOException here to include seek offset
			throw new IOException("Negative seek offset of " + (start + position) + " reading FASTA file " + m_tempFile);
		}
		m_stream.seek(start + position);
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < length; i++) {
			builder.append((char)m_stream.read());
		}
		return builder.toString();
	}

	/**
	 * @return The header that was last read when calling {@link #read}, or null if nothing has been read yet
	 */
	@Nonnull
	public synchronized Optional<String> currentHeader() {
		return Optional.of(m_currentHeader);
	}

	/**
	 * @return The position that was last read when calling {@link #read}, or 0 if nothing has been read yet
	 */
	@Nonnegative
	public synchronized long currentPosition() throws IOException {
		return m_stream.getFilePointer();
	}

	@Override
	public synchronized void close() throws IOException {
		m_stream.close();
	}

	@NotThreadSafe
	public static class Builder {

		private File m_file;
		private File m_tempFile;
		private int m_nCharsInBuffer;
		private boolean m_keepTempFileOnExit;

		public Builder(@Nonnull Path file) {
			this(file.toFile());
		}

		public Builder(@Nonnull File file) {
			m_file = file;
			m_nCharsInBuffer = 2048;
			m_tempFile = new File(file.getPath() + sf_tempExtension);
		}

		@Nonnull
		public Builder setnCharsInBuffer(@Nonnegative int nCharsInBuffer) {
			m_nCharsInBuffer = nCharsInBuffer;
			return this;
		}

		@Nonnull
		public Builder setTempFile(@Nonnull File tempFile) {
			m_tempFile = tempFile;
			return this;
		}

		@Nonnull
		public Builder keepTempFileOnExit() {
			m_keepTempFileOnExit = true;
			return this;
		}

		@Nonnull
		public RandomAccessFastaBaseReader build() throws IOException {
			return new RandomAccessFastaBaseReader(m_file, m_nCharsInBuffer, m_tempFile, m_keepTempFileOnExit);
		}
	}

	@Override
	public String toString() {
		return "RandomAccessFastaBaseReader{" +
				"originalFile=" + m_originalFile +
				", tempFile=" + m_tempFile +
				", headerToPosition=" + m_headerToPosition +
				", stream=" + m_stream +
				", currentHeader='" + m_currentHeader + '\'' +
				'}';
	}
}
