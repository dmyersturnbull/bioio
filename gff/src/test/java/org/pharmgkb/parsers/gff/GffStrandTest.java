package org.pharmgkb.parsers.gff;

import org.junit.jupiter.api.Test;
import org.pharmgkb.parsers.gff.model.GffStrand;
import org.pharmgkb.parsers.model.Strand;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Tests {@link GffStrand}.
 * @author Douglas Myers-Turnbull
 */
public class GffStrandTest {

	@Test
	public void testToGeneralStrand() {
		assertEquals(Optional.of(Strand.PLUS), GffStrand.PLUS.toGeneralStrand());
		assertEquals(Optional.of(Strand.MINUS), GffStrand.MINUS.toGeneralStrand());
		assertFalse(GffStrand.UNSTRANDED.toGeneralStrand().isPresent());
		assertFalse(GffStrand.UNKNOWN.toGeneralStrand().isPresent());
	}

	@Test
	public void testLookupBySymbol() {

		assertEquals(Optional.of(GffStrand.PLUS), GffStrand.lookupBySymbol("+"));
		assertEquals(Optional.of(GffStrand.MINUS), GffStrand.lookupBySymbol("-"));
		assertEquals(Optional.of(GffStrand.UNSTRANDED), GffStrand.lookupBySymbol("."));
		assertEquals(Optional.of(GffStrand.UNKNOWN), GffStrand.lookupBySymbol("?"));

		assertFalse(GffStrand.lookupBySymbol("*").isPresent());
	}
}