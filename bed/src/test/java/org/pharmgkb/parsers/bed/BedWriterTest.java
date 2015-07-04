package org.pharmgkb.parsers.bed;

import org.junit.Test;
import org.pharmgkb.parsers.Strand;

import java.awt.Color;
import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class BedWriterTest {

	@Test
	public void testWrite() throws Exception {

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
		List<String> lines = Arrays.asList(first, second, third).stream()
				.map(new BedWriter())
				.collect(Collectors.toList());

		File expectedFile = Paths.get(getClass().getResource("bed1.bed").toURI()).toFile();
		List<String> expectedLines = Files.readAllLines(expectedFile.toPath());
		assertEquals(expectedLines.size(), lines.size());
		for (int i = 0; i < lines.size(); i++) {
			assertEquals(expectedLines.get(i), lines.get(i));
		}
	}
}