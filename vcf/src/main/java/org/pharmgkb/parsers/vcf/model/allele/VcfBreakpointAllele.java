package org.pharmgkb.parsers.vcf.model.allele;

import com.google.common.base.MoreObjects;
import com.google.common.base.Preconditions;
import org.pharmgkb.parsers.model.Locus;
import org.pharmgkb.parsers.model.Strand;
import org.pharmgkb.parsers.vcf.utils.VcfEscapers;
import org.pharmgkb.parsers.vcf.utils.VcfPatterns;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * A VCF ALT or REF that follows the breakpoint specification (with {@code [} and {@code ]}).
 * @author Douglas Myers-Turnbull
 */
@Immutable
public class VcfBreakpointAllele implements VcfAllele, Serializable {

	private static final long serialVersionUID = -8875925263270240877L;
	private final String m_replacementString;
	private final Locus m_locus;
	private final JoinSequencePlacement m_placement;
	private final Orientation m_orientation;

	@Nonnull
	public String getReplacementString() {
		return m_replacementString;
	}

	@Nonnull
	public Locus getLocus() {
		return m_locus;
	}

	@Nonnull
	public JoinSequencePlacement getPlacement() {
		return m_placement;
	}

	@Nonnull
	public Orientation getOrientation() {
		return m_orientation;
	}

	@Nonnull
	@Override
	public String toVcfString() {
		String locusString = handleSymbolicChromsomeName(m_locus) + ":" + (m_locus.getPosition() + 1);
		char bracket = m_orientation == Orientation.Forward? '[' : ']';
		switch (m_placement) {
			case Prefix:
				return bracket + locusString + bracket + m_replacementString;
			case Suffix:
				return m_replacementString + bracket + locusString + bracket;
			default: throw new UnsupportedOperationException("Unknown placement type " + m_placement);
		}
	}

	@Nonnull
	public static VcfBreakpointAllele fromVcfAlt(@Nonnull String string) {

		Preconditions.checkNotNull(string, "Allele string cannot be null");
		Preconditions.checkArgument(VcfPatterns.ALT_BREAKPOINT_PATTERN.matcher(string).matches(), "Invalid VCF breakpoint " + string);

		Orientation orientation = string.contains("[")? Orientation.Forward : Orientation.Reverse;
		String[] parts = string.split("\\[\\]");
		String replacementString;
		Locus locus;
		JoinSequencePlacement placement;

		try {
			locus = Locus.parse(parts[0] + "(+)");
			replacementString = parts[1];
			placement = JoinSequencePlacement.Suffix;
		} catch (IllegalArgumentException ignored) {
			locus = Locus.parse(parts[1] + "(+)");
			replacementString = parts[0];
			placement = JoinSequencePlacement.Prefix;
		}

		locus = new Locus(handleSymbolicChromsomeName(locus), locus.getPosition() - 1, Strand.PLUS);
		return new VcfBreakpointAllele(replacementString, locus, placement, orientation);
	}

	/**
	 * Handle unescaping.
	 */
	private static String handleSymbolicChromsomeName(@Nonnull Locus locus) {
		String chrName = locus.getChromosome().getOriginalName();
		try {
			return VcfSymbolicAllele.fromVcfAlt(chrName).toVcfString();
		} catch (IllegalArgumentException ignored) {
			return chrName; // assume it's not symbolic
		}
	}

	public VcfBreakpointAllele(@Nonnull String replacementString, @Nonnull Locus locus,
							   @Nonnull JoinSequencePlacement placement, @Nonnull Orientation orientation) {
		Preconditions.checkNotNull(locus, "Locus cannot be null");
		Preconditions.checkNotNull(replacementString, "Replacement string cannot be null");
		Preconditions.checkNotNull(placement, "Placement cannot be null");
		Preconditions.checkNotNull(orientation, "Orientation cannot be null");
		m_replacementString = replacementString;
		m_locus = locus;
		m_placement = placement;
		m_orientation = orientation;
	}

	@Nonnull
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
				.add("replacementString", m_replacementString)
				.add("locus", m_locus)
				.add("placement", m_placement)
				.add("orientation", m_orientation)
				.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		VcfBreakpointAllele that = (VcfBreakpointAllele) o;
		return Objects.equals(m_replacementString, that.m_replacementString) &&
				Objects.equals(m_locus, that.m_locus) &&
				m_placement == that.m_placement &&
				m_orientation == that.m_orientation;
	}

	@Override
	public int hashCode() {
		return Objects.hash(m_replacementString, m_locus, m_placement, m_orientation);
	}

	public enum Orientation {
		Forward, Reverse
	}

	public enum JoinSequencePlacement {
		Prefix, Suffix
	}
}
