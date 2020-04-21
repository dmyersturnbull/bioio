package org.pharmgkb.parsers.bed;

import org.pharmgkb.parsers.LineWriter;
import org.pharmgkb.parsers.bed.model.BedFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Parses a UCSC BED file.
 * See <a href="http://genome.ucsc.edu/FAQ/FAQformat.html">http://genome.ucsc.edu/FAQ/FAQformat.html</a>.
 *
 * Example use:
 * <code>
 *     Stream&lt;String&gt; lines = new BedWriter().apply(features);
 * </code>
 *
 * @author Douglas Myers-Turnbull
 * @see BedFeature
 */
@ThreadSafe
public class BedWriter implements LineWriter<BedFeature> {

	private static final long sf_logEvery = 10000;
	private static final Logger sf_logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private AtomicLong m_lineNumber = new AtomicLong(0l);

	@Nonnull
	@Override
	public String apply(@Nonnull BedFeature feature) {

		if (m_lineNumber.incrementAndGet() % sf_logEvery == 0) {
			sf_logger.debug("Writing line #{}", m_lineNumber);
		}

		StringBuilder sb = new StringBuilder(feature.getChromosome())
				.append("\t").append(feature.getStart())
				.append("\t").append(feature.getEnd());

		if (feature.getName().isPresent()) {
			sb.append("\t").append(feature.getName().get());
		}
		if (feature.getScore().isPresent()) {
			sb.append("\t").append(feature.getScore().get());
		}
		if (feature.getStrand().isPresent()) {
			sb.append("\t").append(feature.getStrand().get().getSymbol());
		}
		if (feature.getThickStart().isPresent()) {
			sb.append("\t").append(feature.getThickStart().get());
		}
		if (feature.getThickEnd().isPresent()) {
			sb.append("\t").append(feature.getThickEnd().get());
		}
		if (feature.getColor().isPresent()) {
			sb.append("\t").append(feature.getColor().get().getRed())
					.append(",").append(feature.getColor().get().getGreen())
					.append(",").append(feature.getColor().get().getBlue());
		}
		if (!feature.getBlocks().isEmpty()) {

			// write count
			sb.append("\t").append(feature.getBlocks().size());

			// write lengths
			sb.append("\t");
			for (int i = 0; i < feature.getBlocks().size(); i++) {
				if (i > 0) {
					sb.append(",");
				}
				sb.append(feature.getBlocks().get(i).getLength());
			}

			// write starts
			sb.append("\t");
			for (int i = 0; i < feature.getBlocks().size(); i++) {
				if (i > 0) {
					sb.append(",");
				}
				sb.append(feature.getBlocks().get(i).getStart());
			}

		}
		return sb.toString();
	}

	@Nonnegative
	@Override
	public long nLinesProcessed() {
		return m_lineNumber.get();
	}

	@Override
	public String toString() {
		return "BedWriter{" +
				"lineNumber=" + m_lineNumber.get() +
				'}';
	}
}
