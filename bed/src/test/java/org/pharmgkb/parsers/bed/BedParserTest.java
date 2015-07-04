package org.pharmgkb.parsers.bed;

import org.junit.Test;
import org.pharmgkb.parsers.BadDataFormatException;
import org.pharmgkb.parsers.Strand;

import java.awt.Color;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class BedParserTest {

	@Test
	public void testParse() throws Exception {
		Path file = Paths.get(getClass().getResource("bed1.bed").toURI());
		List<BedFeature> features = Files.lines(file).map(new BedParser()).collect(Collectors.toList());
					assertEquals(3, features.size());
			BedFeature first = new BedFeature.Builder("chr1", 0, 5).build();
			BedFeature second = new BedFeature.Builder("chr2", 10, 20)
					.setName("xxx")
					.setScore(new BigDecimal(0))
					.setStrand(Strand.PLUS)
					.setThickStart(12l).setThickEnd(18l)
					.setColor(Color.BLACK)
					.build();
			BedFeature third = new BedFeature.Builder("chr2", 30, 40)
					.setName("yyy")
					.setScore(new BigDecimal(1000))
					.setStrand(Strand.MINUS)
					.setThickStart(30l).setThickEnd(40l)
					.setColor(Color.WHITE)
					.addBlock(0, 5).addBlock(5, 15)
					.build();
			assertEquals(first, features.get(0));
			assertEquals(second, features.get(1));
			assertEquals(third, features.get(2));
	}

	@Test(expected = BadDataFormatException.class)
	public void testUnknownStrand() throws Exception {
		String line = "chr2\t10\t20\txxx\t0\t?";
		Collections.singletonList(line).stream().map(new BedParser()).count();
	}

	@Test(expected = BadDataFormatException.class)
	public void testNegativeStart() throws Exception {
		String line = "chr2\t-1\t20";
		Collections.singletonList(line).stream().map(new BedParser()).count();
	}

	@Test(expected = BadDataFormatException.class)
	public void testTransparentColor() throws Exception {
		String line = "chr2\t10\t20\txxx\t0\t+\t12\t18\t0,0,0,2";
		Collections.singletonList(line).stream().map(new BedParser()).count();
	}

}