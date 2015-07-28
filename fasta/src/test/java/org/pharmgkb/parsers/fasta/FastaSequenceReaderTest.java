package org.pharmgkb.parsers.fasta;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link FastaSequenceReader}.
 * @author Douglas Myers-Turnbull
 */
public class FastaSequenceReaderTest {

	@Test
	public void testRead() throws Exception {
		StringReader sw = new StringReader(">header1\ns1p1\ns1p2\n>header2\ns2p1".replace("\n", System.lineSeparator()));
		try (FastaSequenceReader reader = new FastaSequenceReader.Builder(new BufferedReader(sw)).build()) {
			List<FastaSequence> list = reader.read().collect(Collectors.toList());
			assertEquals(2, list.size());
			assertEquals(new FastaSequence("header1", "s1p1s1p2"), list.get(0));
			assertEquals(new FastaSequence("header2", "s2p1"), list.get(1));
		}
	}

	@Test
	public void testReadWithComments() throws Exception {
		StringReader sw = new StringReader(">header1\ns1p1\ns1p2\n;acomment\n>header2\ns2p1".replace("\n", System.lineSeparator()));
		try (FastaSequenceReader reader = new FastaSequenceReader.Builder(new BufferedReader(sw)).allowComments().build()) {
			List<FastaSequence> list = reader.read().collect(Collectors.toList());
			assertEquals(2, list.size());
			assertEquals(new FastaSequence("header1", "s1p1s1p2"), list.get(0));
			assertEquals(new FastaSequence("header2", "s2p1"), list.get(1));
		}
	}

	@Test
	public void testReadWithoutComments() throws Exception {
		StringReader sw = new StringReader(">header1\ns1p1\ns1p2\n;acomment\n>header2\ns2p1".replace("\n", System.lineSeparator()));
		try (FastaSequenceReader reader = new FastaSequenceReader.Builder(new BufferedReader(sw)).build()) {
			List<FastaSequence> list = reader.read().collect(Collectors.toList());
			assertEquals(2, list.size());
			assertEquals(new FastaSequence("header1", "s1p1s1p2;acomment"), list.get(0));
			assertEquals(new FastaSequence("header2", "s2p1"), list.get(1));
		}
	}
}