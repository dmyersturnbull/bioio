package org.pharmgkb.parsers.bed;

import org.junit.jupiter.api.Test;
import org.pharmgkb.parsers.bed.model.BedBlock;
import org.pharmgkb.parsers.bed.model.BedFeature;
import org.pharmgkb.parsers.model.Strand;

import java.awt.*;
import java.util.Arrays;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Tests {@link BedFeature}.
 * @author Douglas Myers-Turnbull
 */
public class BedFeatureTest {

	@Test
	public void testColor() {
		Optional<Color> color = new BedFeature.Builder("chr1", 1, 2)
				.setColorFromString("2,3,4").build().getColor();
		assertTrue(color.isPresent());
		assertEquals(new Color(2, 3, 4), color.get());
		assertFalse(
				new BedFeature.Builder("chr1", 1, 2)
				.build().getColor().isPresent()
		);
	}

	@Test
	public void testBadColor1() {
		IllegalArgumentException e = assertThrows(
				IllegalArgumentException.class,
				() -> new BedFeature.Builder("chr1", 1, 2).setColorFromString("2,3,4,5")
		);
		assertTrue(e.getMessage().toLowerCase(Locale.ROOT).contains("color"));
	}

	@Test
	public void testContainsTab() {
		IllegalArgumentException e = assertThrows(
				IllegalArgumentException.class,
				() -> new BedFeature.Builder("a\tb", 1, 2)
		);
		assertTrue(e.getMessage().toLowerCase(Locale.ROOT).contains("tab"));
	}

	@Test
	public void testContainsNewline1() {
		IllegalArgumentException e = assertThrows(
				IllegalArgumentException.class,
				() -> new BedFeature.Builder("a\nb", 1, 2)
		);
		assertTrue(e.getMessage().toLowerCase(Locale.ROOT).contains("newline"));
	}

	@Test
	public void testContainsNewline2() {
		IllegalArgumentException e = assertThrows(
				IllegalArgumentException.class,
				() -> new BedFeature.Builder("a\rb", 1, 2)
		);
		assertTrue(e.getMessage().toLowerCase(Locale.ROOT).contains("newline"));
	}

	@Test
	public void testBadColor2() {
		IllegalArgumentException e = assertThrows(
				IllegalArgumentException.class,
				() -> new BedFeature.Builder("chr1", 1, 2).setColor(new Color(2, 3, 4, 5))
		);
		assertTrue(e.getMessage().toLowerCase(Locale.ROOT).contains("color"));
	}

	@Test
	public void testNegativeScore() {
		IllegalArgumentException e = assertThrows(
				IllegalArgumentException.class,
				() -> new BedFeature.Builder("chr1", 0, 15).setScore(-1)
		);
		assertTrue(e.getMessage().toLowerCase(Locale.ROOT).contains("score"));
		assertTrue(e.getMessage().contains("< 0"));
	}

	@Test
	public void testLargeScore() {
		IllegalArgumentException e = assertThrows(
				IllegalArgumentException.class,
				() -> new BedFeature.Builder("chr1", 0, 15).setScore(1001)
		);
		assertTrue(e.getMessage().toLowerCase(Locale.ROOT).contains("score"));
		assertTrue(e.getMessage().contains("> 1000"));
	}

	@Test
	public void testNoBlocks() {
		assertTrue(new BedFeature.Builder("chr1", 0, 15).build()
				.getBlocks().isEmpty());
	}

	@Test
	public void testBlocks() {
		BedBlock block1 = new BedBlock(0, 8);
		BedBlock block2 = new BedBlock(8, 15);
		assertEquals(Arrays.asList(block1, block2),
				new BedFeature.Builder("chr1", 0, 15)
						.addBlock(0, 8).addBlock(8, 15)
						.build().getBlocks());
	}

	@Test
	public void testOverlappingBlocks1() {
		BedFeature.Builder builder = new BedFeature.Builder("chr1", 0, 15).addBlock(0, 8);
		IllegalArgumentException e = assertThrows(
				IllegalArgumentException.class,
				() -> builder.addBlock(7, 15)
		);
		assertTrue(e.getMessage().toLowerCase(Locale.ROOT).contains("overlap"));
	}

	@Test
	public void testOverlappingBlocks2() {
		BedFeature.Builder builder = new BedFeature.Builder("chr1", 0, 15).addBlock(0, 5).addBlock(9, 12);
		IllegalArgumentException e = assertThrows(
				IllegalArgumentException.class,
				() -> builder.addBlock(8, 10)
		);
		assertTrue(e.getMessage().toLowerCase(Locale.ROOT).contains("overlap"));
	}

	@Test
	public void testBadBlockStart() {
		BedFeature.Builder builder = new BedFeature.Builder("chr1", 0, 15);
		IllegalArgumentException e = assertThrows(
				IllegalArgumentException.class,
				() -> builder.addBlock(1, 8)
		);
		assertTrue(e.getMessage().toLowerCase(Locale.ROOT).contains("start"));
		assertTrue(e.getMessage().contains("!= 0"));
	}

	@Test
	public void testBadBlockEnd() {
		BedFeature.Builder builder = new BedFeature.Builder("chr1", 0, 15).addBlock(0, 8).addBlock(8, 14);
		IllegalArgumentException e = assertThrows(
				IllegalArgumentException.class,
				builder::build
		);
		assertTrue(e.getMessage().toLowerCase(Locale.ROOT).contains("end of the last block must be the end of the feature"));
		assertTrue(e.getMessage().toLowerCase(Locale.ROOT).contains("instead of 15"));
	}

	@Test
	public void testRebuild() {
		BedFeature.Builder builder = new BedFeature.Builder("chr1", 0, 15).setScore(200);
		BedFeature one = builder.setScore(200).build();
		assertTrue(one.getScore().isPresent());
		assertEquals(200, (int)one.getScore().get());
		BedFeature two = builder.setScore(500).build();
		assertTrue(two.getScore().isPresent());
		assertEquals(500, (int)two.getScore().get());
	}

	@Test
	public void testCopyConstructor() {
		BedFeature one = new BedFeature.Builder("chr1", 0, 15)
				.setStrand(Strand.PLUS)
				.setScore(200)
				.setThickStart(5l).setThickEnd(8l)
				.setColor(Color.RED)
				.addBlock(0, 8).addBlock(8, 15).build();
		BedFeature two = new BedFeature.Builder(one).build();
		assertEquals(one, two);
	}

	@Test
	public void testCopyConstructorAlterBlocks() {
		BedFeature one = new BedFeature.Builder("chr1", 0, 15).addBlock(0, 8).addBlock(8, 15).build();
		// if the copy constructor doesn't copy the blocks, we'll get an exception for modifying an immutable collection
		BedFeature two = new BedFeature.Builder(one).clearBlocks().addBlock(0, 8).addBlock(8, 15).build();
		assertEquals(one, two);
	}

}