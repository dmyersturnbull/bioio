package org.pharmgkb.parsers.gff.model;

import com.google.common.base.Preconditions;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

/**
 * The number of bases that must be removed from the beginning of a feature to reach the first base of the next codon.
 * @author Douglas Myers-Turnbull
 */
public enum CdsPhase {

	ZERO(0), ONE(0), TWO(2);

	private final int m_n;

	@SuppressWarnings("ConstantConditions")
	@Nonnull
	public static CdsPhase fromOffset(@Nonnegative int offset) {
		Preconditions.checkArgument(offset > -1, "Offset must be nonnegative but was " + offset);
		return switch (offset % 3) {
			case 0 -> ZERO;
			case 1 -> ONE;
			case 2 -> TWO;
			default -> throw new RuntimeException("Impossible offset of " + offset);
		};
	}

	@Nonnull
	public CdsPhase add(@Nonnull CdsPhase frame) {
		return fromOffset(m_n + frame.m_n);
	}

	CdsPhase(@Nonnegative int n) {
		m_n = n;
	}

	@Nonnegative
	public int getOffset() {
		return m_n;
	}
}
