package org.pharmgkb.parsers.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LocusTest {

	@Test
	public void testParse() {
		Locus locus = Locus.parse("chrX(+):5");
		assertEquals(new Locus("chrX", 5, Strand.PLUS), locus);
	}

	@Test
	public void testNegative() {
		Locus locus = Locus.parse("chrX(-):-5");
		assertEquals(new Locus("chrX", -5, Strand.MINUS), locus);
	}

	@Test
	public void testIsCompatibleWith() {
		Locus a = Locus.parse("chrX(+):5");
		Locus b = Locus.parse("chrY(+):10");
		Locus c = Locus.parse("chrX(+):15");
		Locus d = Locus.parse("chrY(-):20");
		assertFalse(a.isCompatibleWith(b));
		assertFalse(b.isCompatibleWith(a));
		assertTrue(a.isCompatibleWith(c));
		assertTrue(c.isCompatibleWith(a));
		assertFalse(a.isCompatibleWith(d));
		assertFalse(d.isCompatibleWith(a));
	}
}