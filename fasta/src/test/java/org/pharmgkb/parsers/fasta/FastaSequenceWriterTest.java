package org.pharmgkb.parsers.fasta;

import org.junit.Test;
import org.pharmgkb.parsers.fasta.model.FastaSequence;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link FastaSequenceWriter}.
 * @author Douglas Myers-Turnbull
 */
public class FastaSequenceWriterTest {

	@Test
	public void testApply() {

		List<FastaSequence> seqs = Arrays.asList(
				new FastaSequence("header1", "sequence1"),
				new FastaSequence("header2", "sequence2"),
				new FastaSequence("header3", "sequence3")
		);
		List<String> expected = Arrays.asList(
				">header1",
				"sequence1",
				">header2",
				"sequence2",
				">header3",
				"sequence3"
		);
		List<String> lines = seqs.stream().flatMap(new FastaSequenceWriter()).collect(Collectors.toList());
		assertEquals(expected, lines);
	}
}