package org.pharmgkb.parsers.bed;

import org.junit.jupiter.api.Test;
import org.pharmgkb.parsers.BadDataFormatException;
import org.pharmgkb.parsers.bed.model.BedFeature;
import org.pharmgkb.parsers.model.Strand;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Tests {@link BedParser}.
 * @author Douglas Myers-Turnbull
 */
public class BedParserTest {

	@Test
	public void testParse() throws Exception {
		Path file = Paths.get(getClass().getResource("bed1.bed").toURI());
		List<BedFeature> features = Files.lines(file).map(new BedParser()).collect(Collectors.toList());
			assertEquals(3, features.size());
			BedFeature first = new BedFeature.Builder("chr1", 0, 5).build();
			BedFeature second = new BedFeature.Builder("chr2", 10, 20)
					.setName("xxx")
					.setScore(0)
					.setStrand(Strand.PLUS)
					.setThickStart(12L).setThickEnd(18L)
					.setColor(Color.BLACK)
					.build();
			BedFeature third = new BedFeature.Builder("chr2", 30, 50)
					.setName("yyy")
					.setScore(1000)
					.setStrand(Strand.MINUS)
					.setThickStart(30L).setThickEnd(40L)
					.setColor(Color.WHITE)
					.addBlock(0, 5).addBlock(10, 20)
					.build();
			assertEquals(first, features.get(0));
			assertEquals(second, features.get(1));
			assertEquals(third, features.get(2));
	}

	@Test
	public void testJunkLine() {
		assertThrows(BadDataFormatException.class, () -> Stream.of("asdf").map(new BedParser()).collect(Collectors.toList()));
	}

	@Test
	public void testEmptyLine() {
		assertThrows(BadDataFormatException.class, () -> Stream.of("").map(new BedParser()).collect(Collectors.toList()));
	}

	@Test
	public void testNegativeStart() {
		String line = "chr2\t-2\t20";
		assertThrows(BadDataFormatException.class, () -> Stream.of(line).map(new BedParser()).collect(Collectors.toList()));
	}

	@Test
	public void testTransparentColor() {
		String line = "chr2\t10\t20\txxx\t0\t+\t12\t18\t0,0,0,2";
		assertThrows(BadDataFormatException.class, () -> Stream.of(line).map(new BedParser()).collect(Collectors.toList()));
	}

}