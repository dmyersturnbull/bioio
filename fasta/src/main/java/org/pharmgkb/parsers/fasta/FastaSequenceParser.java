package org.pharmgkb.parsers.fasta;

import com.google.common.base.Preconditions;
import org.pharmgkb.parsers.BadDataFormatException;
import org.pharmgkb.parsers.MultilineParser;
import org.pharmgkb.parsers.fasta.model.FastaSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

/**
 * Reads a FASTA file sequence-by-sequence.
 *
 * <strong>
 *     This parser requires FASTA sequences to be on a single line.
 *     See {@link MultilineFastaSequenceParser} if you need to process multi-line sequences.
 * </strong>
 *
 * The grammar is taken to be:
 * <pre>
 *     fasta      ::= '&gt;'header newline sequence (newline fasta)?
 *     header     ::= [^\n\r]+
 *     sequence   ::= [^\n\r]+
 * </pre>
 * Where {@code newline} is taken to be the platform-dependent newline sequence.
 * Notice that, even though the newline is platform-dependent, neither the header nor sequence can contain a CL or LF,
 * which is a platform-independent choice.
 *
 * Also notice that comments and empty lines are not part of the grammar. To ignore comments and empty lines:
 * {@code .filter(s -> !s.startsWith(";") && !s.trim().isEmpty())}.
 *
 * Example usage:
 * {@code
 *     Stream<FastaSequence> stream = new FastaSequenceParser().parseAll(Files.lines(file));
 * }
 *
 * @author Douglas Myers-Turnbull
 */
public class FastaSequenceParser implements MultilineParser<FastaSequence> {

	private static final long sf_logEvery = 10000;

	private static final Logger sf_logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private AtomicLong m_lineNumber = new AtomicLong(0l);

	private AtomicReference<String> m_currentHeader = new AtomicReference<>(null);

	@Nonnull
	@Override
	public Stream<FastaSequence> parseAll(@Nonnull Stream<String> stream) throws UncheckedIOException, BadDataFormatException {
		Preconditions.checkArgument(!stream.isParallel(), "Cannot read FASTA from a parallel stream");
		return stream.flatMap(this);
	}

	@Nonnull
	@Override
	public Stream<FastaSequence> apply(@Nonnull String line) {
		if (m_lineNumber.incrementAndGet() % sf_logEvery == 0) {
			sf_logger.debug("Reading line #{}", m_lineNumber);
		}
		if (line.startsWith(">")) {
			final String header = m_currentHeader.getAndSet(line.substring(1));
			if (header != null) {
				throw new BadDataFormatException("No sequence for header " + header + " on line " + m_lineNumber);
			}
			return Stream.empty();
		}
		final String header = m_currentHeader.getAndSet(null);
		if (header == null) {
			throw new BadDataFormatException("No header on line " + m_lineNumber);
		}
		return Stream.of(new FastaSequence(header, line));
	}

	/**
	 * @throws IllegalStateException If the last line processed was a header
	 */
	public void sanityCheckFinished() {
		if (m_currentHeader != null) {
			throw new IllegalStateException("The last line processed was a header on line #" + m_lineNumber);
		}
	}

	@Nonnegative
	@Override
	public long nLinesProcessed() {
		return m_lineNumber.get();
	}

	@Override
	public String toString() {
		return "FastaSequenceParser{" +
				"lineNumber=" + m_lineNumber.get() +
				", currentHeader=" + m_currentHeader.get() +
				'}';
	}
}
