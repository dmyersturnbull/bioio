package org.pharmgkb.parsers.chain;

import org.junit.Test;
import org.pharmgkb.parsers.model.Locus;
import org.pharmgkb.parsers.model.LocusRange;
import org.pharmgkb.parsers.model.Strand;

import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link GenomeChain}.
 * @author Douglas Myers-Turnbull
 */
public class GenomeChainTest {

	@Test
	public void test1() {
		GenomeChain.Builder chain = new GenomeChain.Builder();
		addToChain(chain, 1, 5, 3, 7);
		GenomeChain c = chain.build();
		Optional<Locus> got = c.apply(new Locus("chr1", 2, Strand.PLUS));
		assertEquals(Optional.of(new Locus("chr1", 4, Strand.PLUS)), got);
	}

	@Test
	public void test2() {
		GenomeChain.Builder chain = new GenomeChain.Builder();
		addToChain(chain, 1, 5, 3, 7);
		addToChain(chain, 5, 10, 7, 12);
		GenomeChain c = chain.build();
		Optional<Locus> got1 = c.apply(new Locus("chr1", 2, Strand.PLUS));
		assertEquals(Optional.of(new Locus("chr1", 4, Strand.PLUS)), got1);
		Optional<Locus> got2 = c.apply(new Locus("chr1", 6, Strand.PLUS));
		assertEquals(Optional.of(new Locus("chr1", 8, Strand.PLUS)), got2);
	}

	@Test
	public void testComplex() {
		GenomeChain.Builder chain = new GenomeChain.Builder();
		addToChain(chain, 5, 10, 7, 12);
		addToChain(chain, 1, 5, 3, 7);
		addToChain(chain, 20, 25, 15, 20);
		GenomeChain c = chain.build();
		Optional<Locus> got1 = c.apply(new Locus("chr1", 2, Strand.PLUS));
		assertEquals(Optional.of(new Locus("chr1", 4, Strand.PLUS)), got1);
		Optional<Locus> got2 = c.apply(new Locus("chr1", 6, Strand.PLUS));
		assertEquals(Optional.of(new Locus("chr1", 8, Strand.PLUS)), got2);
		Optional<Locus> got3 = c.apply(new Locus("chr1", 21, Strand.PLUS));
		assertEquals(Optional.of(new Locus("chr1", 16, Strand.PLUS)), got3);
		Optional<Locus> got4 = c.apply(new Locus("chr1", 17, Strand.PLUS));
		assertEquals(Optional.empty(), got4);
	}

	@Test
	public void testComplexInverted() {
		GenomeChain.Builder chain = new GenomeChain.Builder();
		addToChain(chain, 5, 10, 7, 12);
		addToChain(chain, 1, 5, 3, 7);
		addToChain(chain, 20, 25, 15, 20);
		GenomeChain c = chain.build().invert();
		Optional<Locus> got1 = c.apply(new Locus("chr1", 4, Strand.PLUS));
		assertEquals(Optional.of(new Locus("chr1", 2, Strand.PLUS)), got1);
		Optional<Locus> got2 = c.apply(new Locus("chr1", 8, Strand.PLUS));
		assertEquals(Optional.of(new Locus("chr1", 6, Strand.PLUS)), got2);
		Optional<Locus> got3 = c.apply(new Locus("chr1", 16, Strand.PLUS));
		assertEquals(Optional.of(new Locus("chr1", 21, Strand.PLUS)), got3);
	}

	@Test(expected=IllegalArgumentException.class)
	public void testSourceOverlapBefore() {
		GenomeChain.Builder chain = new GenomeChain.Builder();
		addToChain(chain, 4, 6, 10, 11);
		addToChain(chain, 1, 5, 3, 7);
		chain.build();
	}

	@Test(expected=IllegalArgumentException.class)
	public void testSourceOverlapAfter() {
		GenomeChain.Builder chain = new GenomeChain.Builder();
		addToChain(chain, 1, 5, 3, 7);
		addToChain(chain, 4, 6, 10, 11);
		chain.build();
	}

	@Test(expected=IllegalArgumentException.class)
	public void testTargetOverlapBefore() {
		GenomeChain.Builder chain = new GenomeChain.Builder();
		addToChain(chain, 6, 7, 6, 8);
		addToChain(chain, 1, 5, 3, 7);
		chain.build();
	}

	@Test(expected=IllegalArgumentException.class)
	public void testTargetOverlapAfter() {
		GenomeChain.Builder chain = new GenomeChain.Builder();
		addToChain(chain, 1, 5, 3, 7);
		addToChain(chain, 6, 7, 6, 8);
		chain.build();
	}

	@Test(expected=IllegalArgumentException.class)
	public void testWrongSize() {
		GenomeChain.Builder chain = new GenomeChain.Builder();
		addToChain(chain, 1, 5, 3, 6);
		chain.build();
	}

	@Test(expected=IllegalArgumentException.class)
	public void testWrongChromosome() {
		GenomeChain.Builder chain = new GenomeChain.Builder();
		LocusRange source = new LocusRange(new Locus("chr1", 5, Strand.PLUS), new Locus("chr2", 10, Strand.PLUS));
		chain.add(source, source);
	}

	@Test
	public void testDifferentChromosomeAndStrand() {
		GenomeChain.Builder chain = new GenomeChain.Builder();
		LocusRange source = new LocusRange(new Locus("chr1", 5, Strand.PLUS), new Locus("chr1", 10, Strand.PLUS));
		LocusRange target = new LocusRange(new Locus("chr2", 5, Strand.MINUS), new Locus("chr2", 10, Strand.MINUS));
		chain.add(source, target);
		GenomeChain c = chain.build();
		Locus sourceLocus = new Locus("chr1", 5, Strand.PLUS);
		Locus targetLocus = new Locus("chr2", 5, Strand.MINUS);
		assertNotNull(targetLocus);
		assertEquals(Optional.of(targetLocus), c.apply(sourceLocus));
	}

	@Test
	public void testCopyConstructor() {
		GenomeChain.Builder builder1 = new GenomeChain.Builder();
		GenomeChain.Builder builder2 = new GenomeChain.Builder(builder1);
		LocusRange source = new LocusRange(new Locus("chr1", 5, Strand.PLUS), new Locus("chr1", 10, Strand.PLUS));
		LocusRange target = new LocusRange(new Locus("chr2", 5, Strand.MINUS), new Locus("chr2", 10, Strand.MINUS));
		builder1.add(source, target); // shouldn't affect builder2
		assertEquals(Optional.empty(), builder2.build().apply(new Locus("chr1", 6, Strand.PLUS)));
	}

	@Test
	public void testRemove() {
		GenomeChain.Builder builder = new GenomeChain.Builder();
		LocusRange source = new LocusRange(new Locus("chr1", 5, Strand.PLUS), new Locus("chr1", 10, Strand.PLUS));
		LocusRange target = new LocusRange(new Locus("chr2", 5, Strand.MINUS), new Locus("chr2", 10, Strand.MINUS));
		builder.add(source, target);
		Optional<Locus> result1 = builder.build().apply(new Locus("chr1", 6, Strand.PLUS));
		assertTrue(result1.isPresent());
		builder.remove(source);
		Optional<Locus> result2 = builder.build().apply(new Locus("chr1", 6, Strand.PLUS));
		assertEquals(Optional.empty(), result2);
	}

	private void addToChain(GenomeChain.Builder chain, int sourceStart, int sourceStop, int targetStart, int targetStop) {
		LocusRange source = new LocusRange(new Locus("chr1", sourceStart, Strand.PLUS), new Locus("chr1", sourceStop, Strand.PLUS));
		LocusRange target = new LocusRange(new Locus("chr1", targetStart, Strand.PLUS), new Locus("chr1", targetStop, Strand.PLUS));
		chain.add(source, target);
	}
}