package org.pharmgkb.parsers.gff;

import org.pharmgkb.parsers.model.Strand;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * A strand of a chromosome in a GFF-like file.
 * Every GFF line must have a non-null, non-empty GffStrand.
 * Distinguishes between two empty types: unstranded or irrelevant (.), and relevant but unknown (?)
 * <ul>
 *     <li>PLUS (+)</li>
 *     <li>MINUS (-)</li>
 *     <li>UNSTRANDED (.)</li>
 *     <li>UNKNOWN (?)</li>
 * </ul>
 * @author Douglas Myers-Turnbull
 */
public enum GffStrand {

	PLUS("+"), MINUS("-"), UNSTRANDED("."), UNKNOWN("?");

	private final String m_symbol;

	GffStrand(@Nonnull String symbol) {
		m_symbol = symbol;
	}

	@Nonnull
	public String getSymbol() {
		return m_symbol;
	}

	/**
	 * @return A Strand if this GffStrand is + or -, and empty otherwise
	 */
	public Optional<Strand> toGeneralStrand() {
		switch(this) {
			case PLUS:
				return Optional.of(Strand.PLUS);
			case MINUS:
				return Optional.of(Strand.MINUS);
			case UNSTRANDED:
				return Optional.empty();
			case UNKNOWN:
				return Optional.empty();
			default:
				return Optional.empty();
		}
	}

	@Nonnull
	public static Optional<GffStrand> lookupBySymbol(@Nonnull String symbol) {
		switch (symbol) {
			case "+": return Optional.of(PLUS);
			case "-": return Optional.of(MINUS);
			case ".": return Optional.of(UNSTRANDED);
			case "?": return Optional.of(UNKNOWN);
			default: return Optional.empty();
		}
	}
}
