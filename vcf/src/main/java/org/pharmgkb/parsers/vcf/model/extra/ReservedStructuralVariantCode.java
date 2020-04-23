package org.pharmgkb.parsers.vcf.model.extra;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * A reserved identifier for VCF ALT codes of structural variants.
 * @see AltStructuralVariant
 * @author Douglas Myers-Turnbull
 */
public enum ReservedStructuralVariantCode {

	Deletion("DEL"),
	Insertion("INS"),
	Duplication("DUP"),
	Inversion("INV"),
	Cnv("CNV"),
	Tandem("DUP", "TANDEM"),
	MobileElementDeletion("DEL", "ME"),
	MobileElementInsertion("INS", "ME");

	private final List<String> m_codes;

	@Nonnull
	public static Optional<ReservedStructuralVariantCode> fromId(@Nonnull String id) {
		return switch (id) {
			case "DEL" -> Optional.of(Deletion);
			case "INS" -> Optional.of(Insertion);
			case "DUP" -> Optional.of(Duplication);
			case "INV" -> Optional.of(Inversion);
			case "CNV" -> Optional.of(Cnv);
			case "DUP:TANDEM" -> Optional.of(Tandem);
			case "DEL:ME" -> Optional.of(MobileElementDeletion);
			case "DEL:INS" -> Optional.of(MobileElementInsertion);
			default -> Optional.empty();
		};
	}

  ReservedStructuralVariantCode(@Nonnull String... codes) {
		m_codes = Arrays.asList(codes);
	}

	/**
	 * @return The code (e.g. CNV)
	 */
	@Nonnull
	public String getId() {
		return String.join(":", m_codes);
	}

	@Nonnull
	public List<String> getCodes() {
		return m_codes;
	}
}
