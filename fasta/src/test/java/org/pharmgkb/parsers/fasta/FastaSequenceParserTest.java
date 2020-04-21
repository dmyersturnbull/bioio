package org.pharmgkb.parsers.fasta;

import org.junit.Test;
import org.pharmgkb.parsers.BadDataFormatException;
import org.pharmgkb.parsers.fasta.model.FastaSequence;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;

/**
 * Tests {@link FastaSequenceParser}.
 * @author Douglas Myers-Turnbull
 */
public class FastaSequenceParserTest {

	@Test
	public void testApply() throws Exception {
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
	public void testParallelStream() throws Exception {
		List<String> lines = Arrays.asList(
				">header1",
				"sequence1"
		);
		assertThatThrownBy(() -> new FastaSequenceParser().parseAll(lines.parallelStream())
				.count())
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("parallel");
	}

	@Test
	public void testMissingHeader() throws Exception {
		Stream<String> lines = Stream.of(
				"sequence1"
		);
		assertThatThrownBy(() -> new FastaSequenceParser().parseAll(lines).count())
				.isInstanceOf(BadDataFormatException.class)
				.hasMessageContaining("No header");
	}

	@Test
	public void testMissingSequence() throws Exception {
		Stream<String> lines = Stream.of(
				">header1",
				">header2"
		);
		assertThatThrownBy(() -> new FastaSequenceParser().parseAll(lines).count())
				.isInstanceOf(BadDataFormatException.class)
				.hasMessageContaining("No sequence");
	}

	@Test
	public void testSanityCheckFinished() throws Exception {

		FastaSequenceParser parser = new FastaSequenceParser();
		parser.parseAll(Stream.of(">xxx")).count();

		assertThatThrownBy(parser::sanityCheckFinished)
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("last line processed");
	}
}