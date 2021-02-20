package org.pharmgkb.parsers.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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

	@Test
	public void testContainsBad() {
		LocusRange range = new LocusRange(new Locus("chrX", 0, Strand.MINUS), new Locus("chrX", 5, Strand.MINUS));
		assertTrue(range.contains(new Locus("chrX", 0, Strand.MINUS)));
		assertThrows(IllegalArgumentException.class, () -> range.contains(new Locus("chrX", 3, Strand.PLUS)));
	}

	@Test
	public void testLength() {
		LocusRange a = new LocusRange(new Locus("chrX", 0, Strand.MINUS), new Locus("chrX", 5, Strand.MINUS));
		assertEquals(5, a.length());
	}

	@Test
	public void testZeroLength() {
		LocusRange a = new LocusRange(new Locus("chrX", 1, Strand.MINUS), new Locus("chrX",1, Strand.MINUS));
		assertEquals(0, a.length());
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

	@Test
	public void testNOverlappingBad1() {
		LocusRange a = new LocusRange(new Locus("chrX", 0, Strand.MINUS), new Locus("chrX", 5, Strand.MINUS));
		LocusRange b = new LocusRange(new Locus("chrX", 4, Strand.PLUS), new Locus("chrX", 10, Strand.PLUS));
		assertThrows(IllegalArgumentException.class, () -> a.calcOverlappingDensity(b));
	}

	@Test
	public void testNOverlappingBad2() {
		LocusRange a = new LocusRange(new Locus("chrX", 0, Strand.MINUS), new Locus("chrX", 5, Strand.MINUS));
		LocusRange b = new LocusRange(new Locus("chrX", 4, Strand.PLUS), new Locus("chrX", 10, Strand.PLUS));
		assertThrows(IllegalArgumentException.class, () -> a.calcOverlappingDensity(b));
	}

	@Test
	public void testBad1() {
		assertThrows(IllegalArgumentException.class, () -> LocusRange.parse("chrX(-):0-"));
	}

	@Test
	public void testBad2() {
		assertThrows(IllegalArgumentException.class, () -> LocusRange.parse("chrX(-):-5-10"));
	}

}