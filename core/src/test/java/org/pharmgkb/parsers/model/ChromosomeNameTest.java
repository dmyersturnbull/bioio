package org.pharmgkb.parsers.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChromosomeNameTest {

	@Test
	public void test1() {
		assertEquals("chr1", new ChromosomeName("chr1").toString());
	}

	@Test
	public void testUcsc() {
		assertEquals("chr1", ChromosomeName.ucscWithFailure("1").toString());
		assertEquals("chrM", ChromosomeName.ucscWithFailure("MT").toString());
	}

	@Test
	public void testId() {
		assertEquals("chrX_AGAFASF55v22", new ChromosomeName("chrX_AGAFASF55v22").toString());
	}

	@Test
	public void testRandom() {
		assertEquals("chrX_AGAFASF55v22_random", new ChromosomeName("chrX_AGAFASF55v22_random").toString());
	}

	@Test
	public void testAlt() {
		assertEquals("chrX_AGAFASF55v22_alt", new ChromosomeName("chrX_AGAFASF55v22_alt").toString());
	}

	@Test
	public void testNonstandard() {
		assertEquals("CHR_HSCHR3_1_CTG2_1", new ChromosomeName("CHR_HSCHR3_1_CTG2_1").toString());
		assertEquals("HSCHR3_1_CTG2_1", new ChromosomeName("HSCHR3_1_CTG2_1").toString());
	}
}