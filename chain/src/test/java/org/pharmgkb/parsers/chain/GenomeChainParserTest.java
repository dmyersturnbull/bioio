package org.pharmgkb.parsers.chain;

import org.junit.Test;
import org.pharmgkb.parsers.Strand;

import java.io.File;
import java.nio.file.Paths;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link GenomeChainParser}.
 * @author Douglas Myers-Turnbull
 */
public class GenomeChainParserTest {

	@Test
	public void testFromChainFile() throws Exception {
		File file = Paths.get(GenomeChainTest.class.getResource("1.chain").toURI()).toFile();
		GenomeChain chain = new GenomeChainParser().parse(file);

		Locus b = new Locus("chr1", 50, Strand.PLUS);
		Optional<Locus> b_ = chain.apply(b);
		assertEquals(Optional.of(b), b_);

		Locus c = new Locus("chr1", 180, Strand.PLUS);
		Optional<Locus> c_ = chain.apply(c);
		assertEquals(Optional.of(new Locus("chr1", 280, Strand.PLUS)), c_);

		Locus d = new Locus("chr1", 230, Strand.PLUS);
		Optional<Locus> d_ = chain.apply(d);
		assertEquals(Optional.of(new Locus("chr1", 120 + 230, Strand.PLUS)), d_);

		Locus a = new Locus("chr1", 500, Strand.PLUS);
		assertEquals(Optional.empty(), chain.apply(a));

		Locus e = new Locus("chr2", 200, Strand.PLUS);
		assertEquals(Optional.empty(), chain.apply(e));

		Locus f = new Locus("chr2", 50, Strand.PLUS);
		Optional<Locus> f_ = chain.apply(f);
		assertEquals(Optional.of(new Locus("chr2", 200, Strand.PLUS)), f_);
	}

}