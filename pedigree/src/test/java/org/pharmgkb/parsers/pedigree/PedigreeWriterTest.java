package org.pharmgkb.parsers.pedigree;

import org.junit.jupiter.api.Test;
import org.pharmgkb.parsers.pedigree.model.Pedigree;
import org.pharmgkb.parsers.pedigree.model.PedigreeBuilder;
import org.pharmgkb.parsers.pedigree.model.Sex;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Tests {@link PedigreeWriter}.
 * @author Douglas Myers-Turnbull
 */
public class PedigreeWriterTest {

	@Test
	public void testWrite() {

		PedigreeBuilder builder = new PedigreeBuilder(true);
		builder.add("f1", "A0_fb", null, null, Sex.FEMALE, Collections.singletonList("no disease"));
		builder.add("f1", "A0_ma", null, null, Sex.MALE, Collections.singletonList("disease"));
		builder.add("f1", "A0_fa", null, null, Sex.FEMALE, Collections.singletonList("disease"));
		builder.add("f1", "A0_fc", null, null, Sex.FEMALE, Collections.singletonList("disease"));
		builder.add("f1", "A0_f_", null, null, Sex.FEMALE, Collections.singletonList("disease"));
		builder.add("f1", "A1_ma", "A0_ma", "A0_fa", Sex.MALE, Collections.singletonList("disease"));
		builder.add("f1", "A2_ma", "A1_ma", null, Sex.MALE, Collections.singletonList("disease"));
		builder.add("f1", "A3_fa", "A2_ma", "A0_fb", Sex.FEMALE, Collections.singletonList("disease"));
		builder.add("f1", "A3_fb", "A2_ma", "A0_fb", Sex.FEMALE, Collections.singletonList("disease"));
		builder.add("f1", "A3_fc", "A2_ma", "A0_fb", Sex.FEMALE, Collections.singletonList("disease"));
		builder.add("f1", "A3_ua", null, "A0_fb", Sex.UNKNOWN, Collections.singletonList("disease"));
		builder.add("f1", "A3_ma", "A2_ma", null, Sex.MALE, Collections.singletonList("disease"));
		builder.add("f1", "A4_ma", null, "A3_fc", Sex.MALE, Collections.singletonList("disease"));
		builder.add("f1", "A4_fa", "A3_ma", "A0_fc", Sex.FEMALE, Collections.singletonList("disease"));
		builder.add("f1", "A5_ua", "A4_ma", "A4_fa", Sex.UNKNOWN, Arrays.asList("disease", "red hair"));

		Pedigree pedigree = builder.build();
		assertNotNull(pedigree);
		List<String> lines = new PedigreeWriter.Builder().build().apply(pedigree).collect(Collectors.toList());
		assertEquals(15, lines.size());
		assertEquals("f1\tA0_f_\t0\t0\t2\tdisease", lines.get(0));
		assertEquals("f1\tA5_ua\tA4_ma\tA4_fa\t2\tdisease\tred hair", lines.get(14));
	}
}