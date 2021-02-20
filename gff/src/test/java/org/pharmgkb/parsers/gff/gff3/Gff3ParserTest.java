package org.pharmgkb.parsers.gff.gff3;

import org.junit.jupiter.api.Test;
import org.pharmgkb.parsers.gff.Gff3Parser;
import org.pharmgkb.parsers.gff.model.CdsPhase;
import org.pharmgkb.parsers.gff.model.Gff3Feature;
import org.pharmgkb.parsers.gff.model.GffStrand;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test {@link Gff3Parser}.
 * @author Douglas Myers-Turnbull
 */
public class Gff3ParserTest {

	@Test
	public void testSeveralLines() throws Exception {
		Path file = Paths.get(getClass().getResource("test.gff3").toURI());
		List<Gff3Feature> features = new Gff3Parser().collectAll(file);
		assertEquals(4, features.size());
		assertEquals("a", features.get(0).getCoordinateSystemName());
		assertEquals("b", features.get(1).getCoordinateSystemName());
		assertEquals("c", features.get(2).getCoordinateSystemName());
		assertEquals("d", features.get(3).getCoordinateSystemName());
	}

	@Test
	public void testCorrectlySet() throws Exception {
		Gff3Feature feature = new Gff3Parser().apply("the-seq-id\tthe-source\tthe-type\t1\t11\t5.3e-11\t+\t1\t.");
		Gff3Feature expected = new Gff3Feature.Builder("the-seq-id", "the-type", 0, 10)
				.setSource("the-source")
				.setScore(new BigDecimal("5.3e-11"))
				.setStrand(GffStrand.PLUS)
				.setPhase(CdsPhase.ONE)
				.build();
		assertEquals(expected, feature);
	}

	@Test
	public void testUnescapeCoordinateSystemId() throws Exception {
		Gff3Feature feature = new Gff3Parser().apply("this%23has%7eother%7echars\t.\tgene\t1\t11\t.\t.\t.\t.");
		assertEquals("this#has~other~chars", feature.getCoordinateSystemName());
	}

	@Test
	public void testUnescapeField() throws Exception {
		Gff3Feature feature = new Gff3Parser().apply("a\t.\tthis-has%3bsemicolons!\t1\t11\t.\t.\t.\t.");
		assertEquals("this-has;semicolons!", feature.getType());
	}

	@Test
	public void testAttributes() throws Exception {
		Gff3Feature feature = new Gff3Parser().apply("a\t.\tgene\t1\t11\t.\t.\t.\tAAA=1;BBB=1,2,3;CCC=this%3dneeds%3descaping");
		Map<String, List<String>> expected = new HashMap<>();
		expected.put("AAA", Collections.singletonList("1"));
		expected.put("BBB", Arrays.asList("1", "2", "3"));
		expected.put("CCC", Collections.singletonList("this=needs=escaping"));
		assertEquals(expected, feature.getAttributes());
	}

	@Test
	public void testStrand() throws Exception {
		assertEquals(GffStrand.UNSTRANDED,
		             new Gff3Parser().apply("a\t.\tgene\t1\t11\t.\t.\t.\t.")
				             .getStrand()
		);
		assertEquals(GffStrand.UNKNOWN,
		             new Gff3Parser().apply("a\t.\tgene\t1\t11\t.\t?\t.\t.")
				             .getStrand()
		);
	}
}