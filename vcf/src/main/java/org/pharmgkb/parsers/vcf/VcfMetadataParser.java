package org.pharmgkb.parsers.vcf;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import org.pharmgkb.parsers.BadDataFormatException;
import org.pharmgkb.parsers.LineStructureParser;
import org.pharmgkb.parsers.vcf.model.VcfMetadataCollection;
import org.pharmgkb.parsers.vcf.utils.VcfMetadataFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

/**
 * Reads VCF meatadata lines; that is, every line that begins with a {@code #}, including the {@code vcfVersion line} and the header line.
 * @author Douglas Myers-Turnbull
 */
@ThreadSafe
public class VcfMetadataParser implements LineStructureParser<VcfMetadataCollection> {

	private static final long sf_logEvery = 10000;
	private static final Splitter sf_tab = Splitter.on("\t");

	private static final Logger sf_logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private AtomicLong m_lineNumber = new AtomicLong(0L);

	@Nonnull
	@Override
	public VcfMetadataCollection apply(@Nonnull Stream<String> stream) throws BadDataFormatException {
		Preconditions.checkNotNull(stream, "Stream cannot be null");
		final VcfMetadataCollection.Builder builder = new VcfMetadataCollection.Builder();
		stream.takeWhile(s -> s.startsWith("#"))
				.forEachOrdered(line -> {

					try {

						long lineNumber = m_lineNumber.incrementAndGet();
						if (lineNumber % sf_logEvery == 0) {
							sf_logger.debug("Reading line #{}", m_lineNumber);
						}

						if (m_lineNumber.get() == 1L && !line.startsWith("##fileformat=VCFv")) {
							throw new BadDataFormatException("First line is " + line + "; doesn't appear to be VCF");
						}
						builder.addLine(VcfMetadataFactory.translate(line));

					} catch (IllegalArgumentException | IllegalStateException e) {
						throw new BadDataFormatException("Couldn't parse line #" + m_lineNumber, e);
					} catch (RuntimeException e) {
						// this is a little weird, but it's helpful
						// not that we're not throwing a BadDataFormatException because we don't expect AIOOB, e.g.
						e.addSuppressed(new RuntimeException("Failed on line " + m_lineNumber));
						throw e;
					}
				});
		return builder.build();
	}

	@Nonnegative
	@Override
	public long nLinesProcessed() {
		return m_lineNumber.get();
	}

	@Override
	public String toString() {
		return "VcfMetadataParser{" +
				"lineNumber=" + m_lineNumber.get() +
				'}';
	}
}
