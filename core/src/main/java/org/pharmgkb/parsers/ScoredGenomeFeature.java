package org.pharmgkb.parsers;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * A {@link GenomeFeature} with a numerical score. The score may or may not be bounded.
 * The following formats, at least, use this:
 * <ul>
 *     <li>BED</li>
 *     <li>GFF</li>
 *     <li>ENCODE</li>
 * </ul>
 */
public interface ScoredGenomeFeature extends GenomeFeature {

	@Nonnull Optional<BigDecimal> getScore();

}
