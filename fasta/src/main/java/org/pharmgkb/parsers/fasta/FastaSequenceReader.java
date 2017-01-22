package org.pharmgkb.parsers.fasta;

import org.pharmgkb.parsers.BadDataFormatException;
import org.pharmgkb.parsers.ObjectBuilder;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.NotThreadSafe;
import javax.annotation.concurrent.ThreadSafe;
import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Reads a FASTA file line by line.
 *
 * The key difference between this class and {@link FastaSequenceParser} is that this allows multi-line sequences.
 * This flexibility precludes using the streaming paradigm, and this class is called a reader rather than a parser to
 * bring to mind {@link java.io.Reader Java Readers}.
 *
 * In addition, the reader may be set to ignore comments (lines beginning with ;) and blank (completely empty) lines.
 *
 * Example usage:
 * {@code
 * try (CompleteFastaReader reader = new CompleteFastaReader(reader)) {
 *     Stream&lt;FastaSequence&gt; stream = reader.read();
 * }
 * }
 *
 * Or, suppose you have 2GB of DNA FASTA and a method {@code Aligner.smithWaterman} that returns a BigDecimal.
 * To get the top 10 alignment scores from that file, in parallel:
 * {@code
 * try (CompleteFastaReader reader = new CompleteFastaReader.Builder(file).build()) {
 *     List<BigDecimal> topScores = reader.read()
 *                                     .parallel()
 *                                     .filter(sequence -> sequence.matches("AaTtGgCc"))
 *                                     .peek(sequence -> logger.info("Read {}", sequence.getHeader())
 *                                     .map(sequence -> Aligner.smithWaterman(sequence.getSequence(), reference))
 *                                     .sorted()
 *                                     .limit(10);
 * }
 * }
 *
 * When built with default options, the grammar is taken to be:
 * <pre>
 *     fasta      ::= '&gt;'header newline sequence (newline fasta)?
 *     header     ::= [^\n\r]+
 *     sequence   ::= .+
 * </pre>
 * Where {@code newline} is taken to be the platform-dependent newline sequence.
 * Notice that, even though the newline is platform-dependent, neither the header nor sequence can contain a CL or LF,
 * which is a platform-independent choice.
 *
 * @author Douglas Myers-Turnbull
 * @deprecated Use {@link MultilineFastaSequenceParser} instead
 */
@Deprecated
@ThreadSafe
public class FastaSequenceReader implements Closeable {

	private static final int sf_spliteratorFlags = Spliterator.ORDERED | Spliterator.IMMUTABLE | Spliterator.NONNULL;

	private final BufferedReader m_reader;
	private final boolean m_allowComments;
	private final boolean m_allowBlankLines;

	public FastaSequenceReader(@Nonnull Builder builder) {
		m_reader = builder.m_reader;
		m_allowComments = builder.m_allowComments;
		m_allowBlankLines = builder.m_allowBlankLines;
	}

	/**
	 * Returns a stream containing the sequences in the order in which they were read.
	 * @throws UncheckedIOException If any IO exception occurs
	 * @throws BadDataFormatException If the data is formatted incorrectly
	 */
	@Nonnull
	public Stream<FastaSequence> read() {

		return StreamSupport.stream(Spliterators.spliteratorUnknownSize(new Iterator<FastaSequence>() {

			String currentHeader;
			String currentSequence;
			String line = "";

			@Override
			public boolean hasNext() {
				return line != null;
			}

			@Nonnull
			@Override
			public FastaSequence next() {

				try {

					while ((line = m_reader.readLine()) != null) {

						if (line.startsWith(">")) {

							// currentHeader is null only for the first header
							// after reading each subsequent header, we know we've finished the sequence
							// so, return that
							FastaSequence seq = null;
							if (currentHeader != null) {
								try {
									seq = new FastaSequence(currentHeader, currentSequence);
								} catch (IllegalArgumentException e) {
									throw new BadDataFormatException(e);
								}
							}

							// reset
							currentHeader = line.substring(1);
							currentSequence = "";

							if (seq != null) {
								return seq;
							}

						} else if (!m_allowBlankLines || !line.isEmpty()) { // don't trim
							if (!m_allowComments || !line.startsWith(";")) {
								if (currentHeader == null) {
									throw new BadDataFormatException("Read sequence line \"" + line
											                                 + "\" without header");
								}
								currentSequence += line;
							}
						}
					}

					// return the last one
					// this one is why we need a reader rather than just streams:
					// a stream doesn't know when it ends
					return new FastaSequence(currentHeader, currentSequence);

				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}

			}
		}, sf_spliteratorFlags), false);

	}

	/**
	 * @throws UncheckedIOException If any IO exception occurs
	 */
	@Override
	public void close() {
		try {
			m_reader.close();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@NotThreadSafe
	public static class Builder implements ObjectBuilder<FastaSequenceReader> {

		private final BufferedReader m_reader;
		private boolean m_allowComments;
		private boolean m_allowBlankLines;

		public Builder(@Nonnull Path path) throws FileNotFoundException {
			this(path.toFile());
		}

		public Builder(@Nonnull File file) throws FileNotFoundException {
			m_reader = new BufferedReader(new FileReader(file));
		}

		public Builder(@Nonnull BufferedReader reader) {
			m_reader = reader;
		}

		/**
		 * Skips lines that start with ";". Otherwise, those lines are treated as part of the sequence.
		 */
		@Nonnull
		public Builder allowComments() {
			m_allowComments = true;
			return this;
		}

		/**
		 * Skips lines that are completely empty (just a newline, not even whitespace).
		 */
		@Nonnull
		public Builder allowBlankLines() {
			m_allowBlankLines = true;
			return this;
		}

		@Nonnull
		@Override
		public FastaSequenceReader build() {
			return new FastaSequenceReader(this);
		}
	}

}
