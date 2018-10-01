package org.pharmgkb.parsers.vcf.model.allele;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * A simple VcfAllele that only contains A, T, G, C, and N (and lowercase variants).
 * @author Douglas Myers-Turnbull
 */
public class VcfBasesAllele implements VcfAllele, Serializable {

	private static final Pattern sf_pattern = Pattern.compile("[AaCcGgTtNn]+");
	private static final long serialVersionUID = 5408492328766377110L;
	private final String m_string;

	@Nonnull
	public static VcfBasesAllele fromVcf(@Nonnull String string) {
		Preconditions.checkNotNull(string, "Allele string cannot be null");
		return new VcfBasesAllele(string);
	}

	/**
	 * @param string A string following the VCF specification for the REF or ALT columns
	 */
	public VcfBasesAllele(@Nonnull String string) {
		Preconditions.checkNotNull(string, "Allele string cannot be null");
		Preconditions.checkArgument(sf_pattern.matcher(string).matches(),
				string + " does not look like an allele");
		m_string = string;
	}

	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("string", m_string)
				.toString();
	}

	/**
	 * @return The number of bases in this Allele, or empty if this allele is symbolic, deleted upstream, or a breakpoint
	 */
	@Nonnegative
	public int length() {
		return m_string.length();
	}

	public boolean isNoVariation() {
		return m_string.isEmpty();
	}

	/**
	 * @return Whether this Allele contains 'N' or 'n' as a base (does not include symbolic names)
	 */
	public boolean isAmbigious() {
		return containsBase('N', 'n');
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		VcfBasesAllele allele = (VcfBasesAllele) o;
		return m_string.equals(allele.m_string);
	}

	@Override
	public int hashCode() {
		return m_string.hashCode();
	}

	@Nonnull
	@Override
	public String toVcfString() {
		if (isNoVariation()) return ".";
		return m_string;
	}

}
