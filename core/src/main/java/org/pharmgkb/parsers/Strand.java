package org.pharmgkb.parsers;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * A strand of a chromosome.
 * <ul>
 *     <li>PLUS (+)</li>
 *     <li>MINUS (-)</li>
 * </ul>
 * @author Douglas Myers-Turnbull
 */
public enum Strand {

	PLUS("+"), MINUS("-");

	private final String m_symbol;

	Strand(@Nonnull String symbol) {
		m_symbol = symbol;
	}

	@Nonnull
	public String getSymbol() {
		return m_symbol;
	}

	@Nonnull
	public static Optional<Strand> lookupBySymbol(String symbol) {
		switch (symbol) {
			case "+": return Optional.of(PLUS);
			case "-": return Optional.of(MINUS);
			default: return Optional.empty();
		}
	}
}
