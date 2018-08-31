package org.pharmgkb.parsers.genbank;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.pharmgkb.parsers.model.Locus;
import org.pharmgkb.parsers.model.Strand;

import static org.junit.Assert.*;

public class GenbankSequenceRangeTest {

	@Test
	public void testSimple() throws Exception {
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
	public void testPartial() throws Exception {
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
	public void testComplement() throws Exception {
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