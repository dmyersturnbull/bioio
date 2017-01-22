package org.pharmgkb.parsers.vcf.model;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.pharmgkb.parsers.model.Locus;
import org.pharmgkb.parsers.model.Strand;
import org.pharmgkb.parsers.vcf.model.allele.VcfAllele;
import org.pharmgkb.parsers.vcf.model.allele.VcfBasesAllele;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.*;

/**
 * Tests {@link VcfPosition}.
 * @author Douglas Myers-Turnbull
 */
public class VcfPositionTest {

	@Test
	public void test() throws Exception {

		VcfPosition position = new VcfPosition.Builder("chr1", -1, "A")
				.addAlt("T")
				.addFilter("filter")
				.build();

		assertEquals(new Locus("chr1", -1, Strand.PLUS), position.getLocus());
		assertEquals("A", position.getRef().toVcfString());
		assertEquals(new ImmutableList.Builder<VcfAllele>()
				.add(new VcfBasesAllele("T")).build(),
				position.getAlts());
		assertEquals(new ImmutableList.Builder<VcfAllele>()
				.add(new VcfBasesAllele("A"))
				.add(new VcfBasesAllele("T")).build(),
				position.getAllAlleles());
		assertEquals(new ImmutableList.Builder<String>().add("filter").build(), position.getFilters());
	}
}