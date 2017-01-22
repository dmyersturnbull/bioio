package org.pharmgkb.parsers.vcf.model.allele;

import javax.annotation.Nonnull;

/**
 * An ALT or REF in VCF.
 * @author Douglas Myers-Turnbull
 */
public interface VcfAllele {

	/**
	 * @return The VCF-ready string, <strong>already escaped</strong>
	 */
	@Nonnull String toVcfString();

	/**
	 * @return Whether this Allele contains one or more of the characters (case-sensitive) in {@code bases},
	 * restricted to bases (i.e. symbolic names like &lt;IDxx&gt; are excluded)
	 */
	default boolean containsBase(@Nonnull char... bases) {
//		if (!(this instanceof VcfBasesAllele || (this instanceof VcfBreakpointAllele))) {
//			return false;
//		}
		boolean isInside = false;
		for (char c : toVcfString().toCharArray()) {
			if (c == '<') {
				isInside = true;
			} else if (c == '>') {
				isInside = false;
			} else if (!isInside) {
				for (char base : bases) {
					if (c == base) {
						return true;
					}
				}
			}
		}
		return false;
	}

}
