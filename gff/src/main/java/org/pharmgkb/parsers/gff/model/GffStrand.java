package org.pharmgkb.parsers.gff.model;

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
		return switch (this) {
			case PLUS -> Optional.of(Strand.PLUS);
			case MINUS -> Optional.of(Strand.MINUS);
			default -> Optional.empty();
		};
	}

	@Nonnull
	public static Optional<GffStrand> lookupBySymbol(@Nonnull String symbol) {
		return switch (symbol) {
			case "+" -> Optional.of(PLUS);
			case "-" -> Optional.of(MINUS);
			case "." -> Optional.of(UNSTRANDED);
			case "?" -> Optional.of(UNKNOWN);
			default -> Optional.empty();
		};
	}
}
