package org.pharmgkb.parsers.fasta;

import org.junit.jupiter.api.Test;
import org.pharmgkb.parsers.fasta.model.FastaSequence;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Reads a FASTA file line by line.
 *
 * The key difference between this class and {@link FastaSequenceParser} is that this allows multi-line sequences.
 * @author Douglas Myers-Turnbull
 */
public class MultilineFastaSequenceParserTest {

	@Test
	public void testApply1() {
		MultilineFastaSequenceParser parser = new MultilineFastaSequenceParser.Builder().setTermination((char)0x01).build();
		List<FastaSequence> seqs = parser.collectAll(Stream.of(">header1", "ns1p1", "ns1p2", ">header2", "ns2p1").onClose(parser.getCloseHandler()));
		assertEquals(2, seqs.size());
		assertEquals(new FastaSequence("header1", "ns1p1ns1p2"), seqs.get(0));
		assertEquals(new FastaSequence("header2", "ns2p1"), seqs.get(1));
	}

	@Test
	public void testApply2() {
		MultilineFastaSequenceParser parser = new MultilineFastaSequenceParser.Builder().setTermination((char)0x01).build();
		Stream<String> lines = Stream.of(">header1", "ns1p1", "ns1p2", ">header2", "ns2p1");
		List<FastaSequence> seqs = parser.parseAll(lines).collect(Collectors.toList());
		assertEquals(2, seqs.size());
		assertEquals(new FastaSequence("header1", "ns1p1ns1p2"), seqs.get(0));
		assertEquals(new FastaSequence("header2", "ns2p1"), seqs.get(1));
	}

	@Test
	public void testApply3() {
		MultilineFastaSequenceParser parser = new MultilineFastaSequenceParser.Builder().setTermination((char)0x01).build();
		Stream<FastaSequence> stream = Stream.of(">header1", "ns1p1", "ns1p2", ">header2", "ns2p1").flatMap(parser);
		assertThrows(IllegalStateException.class, () -> stream.collect(Collectors.toList()));
	}
}