package org.pharmgkb.parsers.model;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ChromosomeNameTest {

	@Test
	public void test1() throws Exception {
		assertEquals("chr1", new ChromosomeName("chr1").toString());
		assertEquals("chr1", new ChromosomeName("1").toString());
	}

	@Test
	public void testM() throws Exception {
		assertEquals("chrM", new ChromosomeName("chrM").toString());
		assertEquals("chrM", new ChromosomeName("chrMT").toString());
		assertEquals("chrM", new ChromosomeName("MT").toString());
	}

	@Test
	public void testId() throws Exception {
		assertEquals("chrX_AGAFASF55v22", new ChromosomeName("chrX_AGAFASF55v22").toString());
	}

	@Test
	public void testRandom() throws Exception {
		assertEquals("chrX_AGAFASF55v22_random", new ChromosomeName("chrX_AGAFASF55v22_random").toString());
	}

	@Test
	public void testAlt() throws Exception {
		assertEquals("chrX_AGAFASF55v22_alt", new ChromosomeName("chrX_AGAFASF55v22_alt").toString());
	}

	@Test
	public void testNonstandard() throws Exception {
		assertEquals("CHR_HSCHR3_1_CTG2_1", new ChromosomeName("CHR_HSCHR3_1_CTG2_1").toString());
		assertEquals("HSCHR3_1_CTG2_1", new ChromosomeName("HSCHR3_1_CTG2_1").toString());
	}
}