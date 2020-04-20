package org.pharmgkb.parsers.bed;

import org.junit.Test;
import org.pharmgkb.parsers.model.Strand;

import java.awt.Color;
import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


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
		assertFalse(new BedFeature.Builder("chr1", 1, 2)
				            .build().getColor().isPresent());
	}

	@Test
	public void testBadColor1() {
		assertThatThrownBy(() -> new BedFeature.Builder("chr1", 1, 2).setColorFromString("2,3,4,5"))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("color");
	}

	@Test
	public void testContainsTab() {
		assertThatThrownBy(() -> new BedFeature.Builder("a\tb", 1, 2))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("tab");
	}

	@Test
	public void testContainsNewline() {
		assertThatThrownBy(() -> new BedFeature.Builder("a\nb", 1, 2))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("newline");
		assertThatThrownBy(() -> new BedFeature.Builder("a\rb", 1, 2))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("newline");
	}

	@Test
	public void testBadColor2() {
		assertThatThrownBy(() -> new BedFeature.Builder("chr1", 1, 2).setColor(new Color(2, 3, 4, 5)))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Color");
	}

	@Test
	public void testNegativeScore() {
		assertThatThrownBy(() -> new BedFeature.Builder("chr1", 0, 15).setScore(-1))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Score")
				.hasMessageContaining("< 0");
	}

	@Test
	public void testLargeScore() {
		assertThatThrownBy(() -> new BedFeature.Builder("chr1", 0, 15).setScore(1001))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("Score")
				.hasMessageContaining("> 1000");
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
		assertThatThrownBy(() -> builder.addBlock(7, 15))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("overlap");
	}

	@Test
	public void testOverlappingBlocks2() {
		BedFeature.Builder builder = new BedFeature.Builder("chr1", 0, 15).addBlock(0, 5).addBlock(9, 12);
		assertThatThrownBy(() -> builder.addBlock(8, 10))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("overlap");
	}

	@Test
	public void testBadBlockStart() {
		BedFeature.Builder builder = new BedFeature.Builder("chr1", 0, 15);
		assertThatThrownBy(() -> builder.addBlock(1, 8))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("start")
				.hasMessageContaining("!= 0");
	}

	@Test
	public void testBadBlockEnd() {
		BedFeature.Builder builder = new BedFeature.Builder("chr1", 0, 15).addBlock(0, 8).addBlock(8, 14);
		assertThatThrownBy(builder::build)
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("The end of the last block must be the end of the feature")
				.hasMessageEndingWith("instead of 15");
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