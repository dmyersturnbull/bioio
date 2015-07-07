package org.pharmgkb.parsers.pedigree;

import org.junit.Test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PedigreeWriterTest {

	@Test
	public void testWrite() throws Exception {

		PedigreeBuilder builder = new PedigreeBuilder(true);
		builder.addIndividual("f1", "A0_fb", null, null, Sex.FEMALE, Arrays.asList("no disease"));
		builder.addIndividual("f1", "A0_ma", null, null, Sex.MALE, Arrays.asList("disease"));
		builder.addIndividual("f1", "A0_fa", null, null, Sex.FEMALE, Arrays.asList("disease"));
		builder.addIndividual("f1", "A0_fc", null, null, Sex.FEMALE, Arrays.asList("disease"));
		builder.addIndividual("f1", "A0_f_", null, null, Sex.FEMALE, Arrays.asList("disease"));
		builder.addIndividual("f1", "A1_ma", "A0_ma", "A0_fa", Sex.MALE, Arrays.asList("disease"));
		builder.addIndividual("f1", "A2_ma", "A1_ma", null, Sex.MALE, Arrays.asList("disease"));
		builder.addIndividual("f1", "A3_fa", "A2_ma", "A0_fb", Sex.FEMALE, Arrays.asList("disease"));
		builder.addIndividual("f1", "A3_fb", "A2_ma", "A0_fb", Sex.FEMALE, Arrays.asList("disease"));
		builder.addIndividual("f1", "A3_fc", "A2_ma", "A0_fb", Sex.FEMALE, Arrays.asList("disease"));
		builder.addIndividual("f1", "A3_ua", null, "A0_fb", Sex.UNKNOWN, Arrays.asList("disease"));
		builder.addIndividual("f1", "A3_ma", "A2_ma", null, Sex.MALE, Arrays.asList("disease"));
		builder.addIndividual("f1", "A4_ma", null, "A3_fc", Sex.MALE, Arrays.asList("disease"));
		builder.addIndividual("f1", "A4_fa", "A3_ma", "A0_fc", Sex.FEMALE, Arrays.asList("disease"));
		builder.addIndividual("f1", "A5_ua", "A4_ma", "A4_fa", Sex.UNKNOWN, Arrays.asList("disease", "red hair"));

		Pedigree pedigree = builder.build();
		assertNotNull(pedigree);
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		try (PedigreeWriter writer = new PedigreeWriter.Builder(pw, pedigree).build()) {
			writer.write();
		}
		String[] lines = sw.toString().split("\n");
		assertEquals(15, lines.length);
		assertEquals("f1\tA0_f_\t0\t0\t1\tdisease", lines[0]);
		assertEquals("f1\tA0_f_\t0\t0\t1\tdisease", lines[0]);
		assertEquals("f1\tA5_ua\tA4_ma\tA4_fa\t1\tdisease\tred hair", lines[14]);
	}
}