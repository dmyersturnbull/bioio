package org.pharmgkb.parsers;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * A feature of a number of base pairs at a specific (known) location in a genome.
 * The following formats, at least, have this form:
 * <ul>
 *     <li>BED</li>
 *     <li>GFF</li>
 *     <li>ENCODE</li>
 *     <li>UCSC personal genome</li>
 * </ul>
 * @author Douglas Myers-Turnbull
 */
public interface GenomeFeature {

	/**
	 * @return The chromosome or scaffold of this feature
	 */
	@Nonnull String getChromosome();

	/**
	 * @return The start of the feature on the chromosome, 0-based
	 */
	@Nonnegative long getStart();

	/**
	 * @return The end of the feature on the chromosome, 0-based
	 */
	@Nonnegative long getEnd();

	@Nonnull Optional<Strand> getStrand();

	/**
	 * @return The name of this feature
	 */
	@Nonnull Optional<String> getName();

}
