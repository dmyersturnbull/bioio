package org.pharmgkb.parsers.fasta;

import org.junit.jupiter.api.Test;
import org.pharmgkb.parsers.BadDataFormatException;
import org.pharmgkb.parsers.fasta.model.FastaSequence;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Tests {@link FastaSequenceParser}.
 * @author Douglas Myers-Turnbull
 */
public class FastaSequenceParserTest {

	@Test
	public void testApply() {
		Stream<String> lines = Stream.of(
				">header1",
				"sequence1",
				">header2",
				"sequence2",
				">header3",
				"sequence3"
		);
		List<FastaSequence> seqs = lines.flatMap(new FastaSequenceParser())
				.collect(Collectors.toList());
		List<FastaSequence> expected = Arrays.asList(
				new FastaSequence("header1", "sequence1"),
				new FastaSequence("header2", "sequence2"),
				new FastaSequence("header3", "sequence3")
		);
		assertEquals(expected, seqs);
	}

	@Test
	public void testParallelStream() {
		List<String> lines = Arrays.asList(
				">header1",
				"sequence1"
		);
		Stream<FastaSequence> stream = new FastaSequenceParser().parseAll(lines.parallelStream());
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, stream::count);
		assertTrue(e.getMessage().toLowerCase(Locale.ROOT).contains("parallel"));
	}

	@Test
	public void testMissingHeader() {
		Stream<String> lines = Stream.of(
				"sequence1"
		);
		Stream<FastaSequence> stream = new FastaSequenceParser().parseAll(lines);
		BadDataFormatException e = assertThrows(BadDataFormatException.class, stream::count);
		assertTrue(e.getMessage().toLowerCase(Locale.ROOT).contains("header"));
	}

	@Test
	public void testMissingSequence() {
		Stream<String> lines = Stream.of(
				">header1",
				">header2"
		);
		Stream<FastaSequence> stream = new FastaSequenceParser().parseAll(lines);
		BadDataFormatException e = assertThrows(BadDataFormatException.class, stream::count);
		assertTrue(e.getMessage().toLowerCase(Locale.ROOT).contains("sequence"));
	}

	@Test
	public void testSanityCheckFinished() {

		FastaSequenceParser parser = new FastaSequenceParser();
		parser.parseAll(Stream.of(">xxx")).count();
		IllegalStateException e = assertThrows(IllegalStateException.class, () -> parser.parseAll(Stream.of(">xxx")).count());
		assertTrue(e.getMessage().toLowerCase(Locale.ROOT).contains("last line processed"));
	}
}