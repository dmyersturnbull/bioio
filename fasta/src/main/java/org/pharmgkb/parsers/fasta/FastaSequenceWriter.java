package org.pharmgkb.parsers.fasta;

import org.pharmgkb.parsers.LineStructureWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

/**
 * Writes {@link FastaSequence FastaSequences}.
 * @author Douglas Myers-Turnbull
 */
public class FastaSequenceWriter implements LineStructureWriter<FastaSequence> {

	private static final long sf_logEvery = 10000;
	private static final Logger sf_logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private AtomicLong m_lineNumber = new AtomicLong(0l);

	@Nonnull
	@Override
	public Stream<String> apply(@Nonnull FastaSequence sequence) {

		if (m_lineNumber.addAndGet(2) % sf_logEvery == 0) {
			sf_logger.debug("Writing line #{}", m_lineNumber);
		}

		return Stream.of(">" + sequence.getHeader(), sequence.getSequence());
	}

	@Nonnegative
	@Override
	public long nLinesProcessed() {
		return m_lineNumber.get();
	}
}
