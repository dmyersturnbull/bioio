package org.pharmgkb.parsers.gff;

import org.junit.Test;
import org.pharmgkb.parsers.Strand;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Tests {@link GffStrand}.
 * @author Douglas Myers-Turnbull
 */
public class GffStrandTest {

	@Test
	public void testToGeneralStrand() throws Exception {
		assertEquals(Optional.of(Strand.PLUS), GffStrand.PLUS.toGeneralStrand());
		assertEquals(Optional.of(Strand.MINUS), GffStrand.MINUS.toGeneralStrand());
		assertFalse(GffStrand.UNSTRANDED.toGeneralStrand().isPresent());
		assertFalse(GffStrand.UNKNOWN.toGeneralStrand().isPresent());
	}

	@Test
	public void testLookupBySymbol() throws Exception {

		assertEquals(Optional.of(GffStrand.PLUS), GffStrand.lookupBySymbol("+"));
		assertEquals(Optional.of(GffStrand.MINUS), GffStrand.lookupBySymbol("-"));
		assertEquals(Optional.of(GffStrand.UNSTRANDED), GffStrand.lookupBySymbol("."));
		assertEquals(Optional.of(GffStrand.UNKNOWN), GffStrand.lookupBySymbol("?"));

		assertFalse(GffStrand.lookupBySymbol("*").isPresent());
	}
}