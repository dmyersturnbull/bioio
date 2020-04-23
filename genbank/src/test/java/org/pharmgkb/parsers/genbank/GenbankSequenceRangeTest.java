package org.pharmgkb.parsers.genbank;

import org.junit.Test;
import org.pharmgkb.parsers.genbank.model.GenbankSequenceRange;

import static org.junit.Assert.*;

/**
 * Author Douglas Myers-Turnbull
 */
public class GenbankSequenceRangeTest {

	@Test
	public void testSimple() {
		String text = "-10..50";
		GenbankSequenceRange range = new GenbankSequenceRange(text);
		assertFalse(range.isComplement());
		assertFalse(range.isEndPartial());
		assertFalse(range.isStartPartial());
		assertEquals(text, range.getText());
		assertEquals(-10L, range.start());
		assertEquals(50, range.end());
	}

	@Test
	public void testPartial(){
		String text = "<-10..50>";
		GenbankSequenceRange range = new GenbankSequenceRange(text);
		assertFalse(range.isComplement());
		assertTrue(range.isEndPartial());
		assertTrue(range.isStartPartial());
		assertEquals(text, range.getText());
		assertEquals(-10L, range.start());
		assertEquals(50, range.end());
	}

	@Test
	public void testComplement() {
		String text = "complement(<-10..50>)";
		GenbankSequenceRange range = new GenbankSequenceRange(text);
		assertTrue(range.isComplement());
		assertTrue(range.isEndPartial());
		assertTrue(range.isStartPartial());
		assertEquals(text, range.getText());
		assertEquals(-10L, range.start());
		assertEquals(50, range.end());
	}
}