package org.pharmgkb.parsers.vcf;

import org.junit.jupiter.api.Test;
import org.pharmgkb.parsers.vcf.model.VcfPosition;
import org.pharmgkb.parsers.vcf.model.extra.ReservedFormatProperty;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author Douglas Myers-Turnbull
 */
public class VcfDataIntegrationTest {

	@Test
	public void testApply() throws Exception {

		Path input = new File("/Users/student/genome-sequence-io/vcf/src/test/resources/org/pharmgkb/parsers/vcf/example.vcf").toPath();

		VcfDataParser parser = new VcfDataParser();
		List<VcfPosition> positions = parser.parseAll(input).collect(Collectors.toList());

		List<String> expectedGenotypes = positions.stream()
				.map(p -> p.getSamples().get(0))
				.filter(s -> s.containsKey(ReservedFormatProperty.Genotype))
				.map(s -> s.get(ReservedFormatProperty.Genotype).get())
				.collect(Collectors.toList());
		List<String> genotypes = positions.stream()
				.map(p -> p.getGenotype(0))
				.filter(Optional::isPresent)
				.map(o -> o.get().toVcfString())
				.collect(Collectors.toList());
		for (int i = 0; i < expectedGenotypes.size(); i++) {
			assertEquals("Genotype " + i + " is wrong", expectedGenotypes.get(i), genotypes.get(i));
		}


		List<String> expected = Files.lines(input).skip(22L).collect(Collectors.toList());
		List<String> actual = positions.stream().map(new VcfDataWriter()).collect(Collectors.toList());
		assertEquals(expected.size(), actual.size());

		for (int i = 0; i < expected.size(); i++) {
			assertEquals("Line " + (i + 21) + " is wrong", expected.get(i), actual.get(i));
		}
	}


}