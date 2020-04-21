package org.pharmgkb.parsers.gff.gff3;

import org.junit.Test;
import org.pharmgkb.parsers.gff.Gff3Writer;
import org.pharmgkb.parsers.gff.model.CdsPhase;
import org.pharmgkb.parsers.gff.model.Gff3Feature;
import org.pharmgkb.parsers.gff.model.GffStrand;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link Gff3Writer}.
 * @author Douglas Myers-Turnbull
 */
public class Gff3WriterTest {

	@Test
	public void testSimple() throws Exception {
		Gff3Feature feature = new Gff3Feature.Builder("chr1", "ttt", 0, 5).setSource("source")
				.putAttributes("x", Arrays.asList("a", "b", "c"))
				.putAttributes("y", Collections.emptyList())
				.build();
		String line = new Gff3Writer().apply(feature);
		assertEquals("chr1\tsource\tttt\t1\t6\t.\t.\t.\tx=a,b,c;y=", line);
	}

	@Test
	public void testMoreFields() throws Exception {
		Gff3Feature feature = new Gff3Feature.Builder("chr1", "ttt", 0, 5).setSource("source")
				.setPhase(CdsPhase.ONE)
				.setScore(new BigDecimal("5.5e-11"))
				.setSource("the_source")
				.setStrand(GffStrand.UNKNOWN)
				.build();
		String line = new Gff3Writer().apply(feature);
		assertEquals("chr1\tthe_source\tttt\t1\t6\t5.5E-11\t?\tONE\t.", line);
	}

	@Test
	public void testEscaping() throws Exception {
		Gff3Feature feature = new Gff3Feature.Builder("has#unescaped#chars\ttoo", "ttt", 0, 5).setSource("has;semicolons\ntoo")
				.setSource("has;semicolons\rtoo")
				.putAttributes("x", Arrays.asList("a=a", "b=b"))
				.build();
		String line = new Gff3Writer().apply(feature);
		assertEquals("has%23unescaped%23chars%09too\thas%3bsemicolons%0dtoo\tttt\t1\t6\t.\t.\t.\tx=a%3da,b%3db", line);
	}
}