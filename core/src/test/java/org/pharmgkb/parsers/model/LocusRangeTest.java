package org.pharmgkb.parsers.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LocusRangeTest {

	@Test
	public void testParse() {
		LocusRange range = LocusRange.parse("chrX(-):0-5");
		assertEquals(new LocusRange(new Locus("chrX", 0, Strand.MINUS), new Locus("chrX", 5, Strand.MINUS)), range);
	}

	@Test
	public void testContains() {
		LocusRange range = new LocusRange(new Locus("chrX", 0, Strand.MINUS), new Locus("chrX", 5, Strand.MINUS));
		assertTrue(range.contains(new Locus("chrX", 0, Strand.MINUS)));
		assertTrue(range.contains(new Locus("chrX", 5, Strand.MINUS)));
		assertTrue(range.contains(new Locus("chrX", 3, Strand.MINUS)));
		assertFalse(range.contains(new Locus("chrY", 0, Strand.MINUS)));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testContainsBad() {
		LocusRange range = new LocusRange(new Locus("chrX", 0, Strand.MINUS), new Locus("chrX", 5, Strand.MINUS));
		assertTrue(range.contains(new Locus("chrX", 0, Strand.MINUS)));
		range.contains(new Locus("chrX", 3, Strand.PLUS));
	}

	@Test
	public void testLength() {
		LocusRange a = new LocusRange(new Locus("chrX", 0, Strand.MINUS), new Locus("chrX", 5, Strand.MINUS));
		assertEquals(5, a.length());
	}

	@Test
	public void testNOverlapping() {
		LocusRange a = new LocusRange(new Locus("chrX", 0, Strand.MINUS), new Locus("chrX", 5, Strand.MINUS));
		LocusRange b = new LocusRange(new Locus("chrX", 4, Strand.MINUS), new Locus("chrX", 10, Strand.MINUS));
		assertEquals(1, a.calcOverlappingDensity(b));
	}

	@Test
	public void testNOverlappingDifferentChr() {
		LocusRange a = new LocusRange(new Locus("chrX", 0, Strand.MINUS), new Locus("chrX", 5, Strand.MINUS));
		LocusRange b = new LocusRange(new Locus("chr1", 4, Strand.MINUS), new Locus("chr1", 10, Strand.MINUS));
		assertEquals(0, a.calcOverlappingDensity(b));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNOverlappingBad1() {
		LocusRange a = new LocusRange(new Locus("chrX", 0, Strand.MINUS), new Locus("chrX", 5, Strand.MINUS));
		LocusRange b = new LocusRange(new Locus("chrX", 4, Strand.PLUS), new Locus("chrX", 10, Strand.PLUS));
		a.calcOverlappingDensity(b);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNOverlappingBad2() {
		LocusRange a = new LocusRange(new Locus("chrX", 0, Strand.MINUS), new Locus("chrX", 5, Strand.MINUS));
		LocusRange b = new LocusRange(new Locus("chrX", 4, Strand.PLUS), new Locus("chrX", 10, Strand.PLUS));
		a.calcOverlappingDensity(b);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBad1() {
		LocusRange.parse("chrX(-):0-");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBad2() {
		LocusRange.parse("chrX(-):-5-10");
	}

}