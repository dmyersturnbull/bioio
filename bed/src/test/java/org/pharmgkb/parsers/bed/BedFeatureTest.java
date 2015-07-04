package org.pharmgkb.parsers.bed;

import org.junit.Test;
import org.pharmgkb.parsers.Strand;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


/**
 * Tests {@link BedFeature}.
 * @author Douglas Myers-Turnbull
 */
public class BedFeatureTest {

	@Test
	public void testColor() throws Exception {
		Optional<Color> color = new BedFeature.Builder("chr1", 1, 2)
				.setColor("2,3,4").build().getColor();
		assertTrue(color.isPresent());
		assertEquals(new Color(2, 3, 4), color.get());
		assertFalse(new BedFeature.Builder("chr1", 1, 2)
				            .build().getColor().isPresent());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBadColor1() throws Exception {
		new BedFeature.Builder("chr1", 1, 2).setColor("2,3,4,5");
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBadColor2() throws Exception {
		new BedFeature.Builder("chr1", 1, 2).setColor(new Color(2, 3, 4, 5));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testNegativeScore() throws Exception {
		new BedFeature.Builder("chr1", 0, 15).setScore(new BigDecimal("-0.00001"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testLargeScore() throws Exception {
		new BedFeature.Builder("chr1", 0, 15).setScore(new BigDecimal("1000.00001"));
	}

	@Test
	public void testNoBlocks() throws Exception {
		assertTrue(new BedFeature.Builder("chr1", 0, 15)
				           .build().getBlocks().isEmpty());
	}

	@Test
	public void testBlocks() throws Exception {
		BedBlock block1 = new BedBlock(0, 8);
		BedBlock block2 = new BedBlock(8, 15);
		assertEquals(Arrays.asList(block1, block2),
		             new BedFeature.Builder("chr1", 0, 15)
				             .addBlock(0, 8).addBlock(8, 15)
				             .build().getBlocks());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testOverlappingBlocks1() throws Exception {
		new BedFeature.Builder("chr1", 0, 15).addBlock(0, 8).addBlock(7, 15);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testOverlappingBlocks2() throws Exception {
		new BedFeature.Builder("chr1", 0, 15).addBlock(0, 5).addBlock(9, 12).addBlock(8, 10).addBlock(12, 15);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBadBlockStart() throws Exception {
		new BedFeature.Builder("chr1", 0, 15).addBlock(1, 8).addBlock(8, 15);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBadBlockEnd() throws Exception {
		new BedFeature.Builder("chr1", 0, 15).addBlock(0, 8).addBlock(8, 14).build();
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBadBlockOutOfOrder() throws Exception {
		new BedFeature.Builder("chr1", 0, 15).addBlock(8, 15).addBlock(0, 8).build();
	}

	@Test(expected = IllegalStateException.class)
	public void testRebuild() throws Exception {
		BedFeature.Builder builder = new BedFeature.Builder("chr1", 0, 15);
		builder.build();
		builder.build();
	}

	@Test
	public void testCopyConstructor() throws Exception {
		BedFeature one = new BedFeature.Builder("chr1", 0, 15)
				.setStrand(Strand.PLUS)
				.setScore(new BigDecimal("4.12e-10"))
				.setThickStart(5l).setThickEnd(8l)
				.setColor(Color.RED)
				.addBlock(0, 8).addBlock(8, 15).build();
		BedFeature two = new BedFeature.Builder(one).build();
		assertEquals(one, two);
	}

	@Test
	public void testCopyConstructorAlterBlocks() throws Exception {
		BedFeature one = new BedFeature.Builder("chr1", 0, 15).addBlock(0, 8).addBlock(8, 15).build();
		// if the copy constructor doesn't copy the blocks, we'll get an exception for modifying an immutable collection
		BedFeature two = new BedFeature.Builder(one).clearBlocks().addBlock(0, 8).addBlock(8, 15).build();
		assertEquals(one, two);
	}

}