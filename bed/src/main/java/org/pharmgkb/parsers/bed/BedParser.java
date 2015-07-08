package org.pharmgkb.parsers.bed;

import org.pharmgkb.parsers.BadDataFormatException;
import org.pharmgkb.parsers.LineParser;
import org.pharmgkb.parsers.Strand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Parses a UCSC BED file.
 * Follows the specification described in:
 * <a href="http://genome.ucsc.edu/FAQ/FAQformat.html">http://genome.ucsc.edu/FAQ/FAQformat.html</a>.
 *
 * Example usages:
 * <code>
 *     // store in a list
 *     List<BedFeature> features = Files.lines(file).map(new BedParser()).collect(Collectors.toList());
 * </code>
 * <code>
 *     // get a stream of strings of the ranges, sorted by name (will throw an exception if a name is missing)
 *     Files.lines(file).map(new BedParser())
 *          .sorted((f1, f2) -> f1.getName().get().compareTo(f2.getName().get()))
 *          .map(f -> f.getChromosome() + ":" + f.getStart() + "-" + f.getEnd());
 * </code>
 * <code>
 *     // get a stream of distinct chromosome names that start with "chr", in parallel
 *     Files.lines(file).map(new BedParser())
 *          .parallel()
 *          .map(BedFeature::getChromosome).distinct()
 *          .filter(chr -> chr.startsWith("chr"))
 * </code>
 *
 * @author Douglas Myers-Turnbull
 * @see org.pharmgkb.parsers.bed.BedFeature
 * @see org.pharmgkb.parsers.bed.BedWriter
 */
@ThreadSafe
public class BedParser implements LineParser<BedFeature> {

	private static final long sf_logEvery = 10000;
	private static final Pattern sf_comma = Pattern.compile(",");
	private static final Pattern sf_tab = Pattern.compile("\t");

	private static final Logger sf_logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

	private AtomicLong m_lineNumber = new AtomicLong(0l);

	@Override
	public Stream<BedFeature> parseAll(@Nonnull Stream<String> stream) throws IOException, BadDataFormatException {
		return stream.map(this);
	}

	@Override
	public BedFeature apply(@Nonnull String line) throws BadDataFormatException {

		if (m_lineNumber.incrementAndGet() % sf_logEvery == 0) {
			sf_logger.debug("Reading line #{}", m_lineNumber);
		}

		String[] parts = sf_tab.split(line);
		try {
			BedFeature.Builder builder = new BedFeature.Builder(parts[0], Long.parseLong(parts[1]),
			                                                    Long.parseLong(parts[2]));

			if (parts.length > 3) {
				builder.setName(parts[3]);
			}
			if (parts.length > 4) {
				builder.setScore(Integer.parseInt(parts[4]));
			}
			if (parts.length > 5) {
				builder.setStrand(Strand.lookupBySymbol(parts[5]));
			}
			if (parts.length > 6) {
				builder.setThickStart(Long.parseLong(parts[6]));
			}
			if (parts.length > 7) {
				builder.setThickEnd(Long.parseLong(parts[7]));
			}
			if (parts.length > 8) {
				builder.setColorFromString(parts[8]);
			}
			if (parts.length > 9) {
				int blockCount = Integer.parseInt(parts[9]);
				String[] lengths = sf_comma.split(parts[10]);
				String[] starts = sf_comma.split(parts[11]);
				if (blockCount != starts.length) {
					throw new BadDataFormatException("There should be " + blockCount + " blocks, but " + starts.length
							                       + " block starts were specified on line #" + m_lineNumber);
				}
				if (blockCount != lengths.length) {
					throw new BadDataFormatException("There should be " + blockCount + " blocks, but " + lengths.length
							                       + " block lengths were specified on line #" + m_lineNumber);
				}
				for (int i = 0; i < blockCount; i++) {
					long blockStart = Long.parseLong(starts[i]);
					long blockLength = Long.parseLong(lengths[i]);
					builder.addBlock(blockStart, blockStart + blockLength);
				}
			}

			return builder.build();

		} catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
			throw new BadDataFormatException("Bad data format on line #" + m_lineNumber.get()
					                                 + "; line is [[[" + line + "]]]", e);
		}
	}
}
