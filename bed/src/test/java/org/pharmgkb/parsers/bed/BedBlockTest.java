package org.pharmgkb.parsers.bed;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link BedBlock}.
 * @author Douglas Myers-Turnbull
 */
public class BedBlockTest {

	@Test
	public void test() throws Exception {
		BedBlock block = new BedBlock(5, 9);
		assertEquals(5, block.getStart());
		assertEquals(9, block.getEnd());
		assertEquals(4, block.getLength());
		assertEquals("5-9", block.toString());
	}

	@Test
	public void testEquals() throws Exception {
		BedBlock block1 = new BedBlock(5, 9);
		BedBlock block2 = new BedBlock(5, 9);
		BedBlock block3 = new BedBlock(18, 20);
		assertTrue(block1.equals(block2));
		assertFalse(block1.equals(block3));
	}
}