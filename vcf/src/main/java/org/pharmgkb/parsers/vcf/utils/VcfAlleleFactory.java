package org.pharmgkb.parsers.vcf.utils;

import com.google.common.base.Preconditions;
import org.pharmgkb.parsers.vcf.model.allele.*;

import javax.annotation.Nonnull;

/**
 * Converts a VCF ALT allele string to the appropriate subclass of {@link VcfAllele}.
 * @author Douglas Myers-Turnbull
 */
public class VcfAlleleFactory {

	private VcfAlleleFactory() {}

	@Nonnull
	public static VcfAllele translate(@Nonnull String string) {
		Preconditions.checkNotNull(string, "Allele string cannot be null");
		if (string.startsWith("[") || string.startsWith("]")) {
			return VcfBreakpointAllele.fromVcfAlt(string);
		}
		if (string.startsWith("<")) {
			return VcfSymbolicAllele.fromVcfAlt(string);
		}
		if (string.equals("*")) {
			return VcfDeletedAllele.DELETED;
		}
		return VcfBasesAllele.fromVcf(string);
	}

}
