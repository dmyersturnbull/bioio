package org.pharmgkb.parsers.vcf.model.metadata;

import javax.annotation.Nonnull;
import java.io.Serializable;

/**
 * The contents of any VCF line that begins with a {@link #}, including the header and {@code vcfVersion} lines.
 * @author Douglas Myers-Turnbull
 */
public interface VcfMetadata extends Serializable {

	/**
	* @return The full VCF-formatted text of the line, including {@code #} or {@code ##}, and <strong>already escaped</strong>
	*/
	@Nonnull
	String toVcfLine();

}
