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
		switch(id) {
			case "DEL": return Optional.of(Deletion);
			case "INS": return Optional.of(Insertion);
			case "DUP": return Optional.of(Duplication);
			case "INV": return Optional.of(Inversion);
			case "CNV": return Optional.of(Cnv);
			case "DUP:TANDEM": return Optional.of(Tandem);
			case "DEL:ME": return Optional.of(MobileElementDeletion);
			case "DEL:INS": return Optional.of(MobileElementInsertion);
			default: return Optional.empty();
		}
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
