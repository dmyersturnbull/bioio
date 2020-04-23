package org.pharmgkb.parsers.vcf.model.metadata;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * A reserved value for the Number field in INFO and FORMAT metadata entries.
 * @author Douglas Myers-Turnbull
 */
public enum VcfNumberFlag {

	ONE_PER_ALT("A"),
	ONE_PER_ALT_OR_REF("R"),
	ONE_PER_GENOTYPE("G"),
	UNKNOWN_OR_UNBOUNDED(".");

	@Nonnull
	public static Optional<VcfNumberFlag> fromId(@Nonnull String id) {
		return switch (id) {
			case "A" -> Optional.of(ONE_PER_ALT);
			case "R" -> Optional.of(ONE_PER_ALT_OR_REF);
			case "G" -> Optional.of(ONE_PER_GENOTYPE);
			case "." -> Optional.of(UNKNOWN_OR_UNBOUNDED);
			default -> Optional.empty();
		};
	}

	private final String m_id;

	VcfNumberFlag(String id) {
		m_id = id;
	}

	@Nonnull
	public String getId() {
		return m_id;
	}
}
