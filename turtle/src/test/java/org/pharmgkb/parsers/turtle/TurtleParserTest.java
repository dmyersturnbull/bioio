package org.pharmgkb.parsers.turtle;

import org.junit.Test;
import org.pharmgkb.parsers.turtle.model.Triple;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Author Douglas Myers-Turnbull
 */
public class TurtleParserTest {

	@Test
	public void apply() throws IOException {
		Path input = new File("/home/dmyerstu/desktop/chembl-rdf/chembl-moa-short.ttl").toPath();
		Path input2 = new File("/home/dmyerstu/desktop/chembl-rdf/chembl_24.1_activity.ttl").toPath();
		//
		TurtleParser parser = new TurtleParser();
		System.out.println(input);
		Stream<Triple> parse = parser.parseAll(input);
		List<Triple> triples = parse.collect(Collectors.toList());
		//assertEquals(15, triples.size());
		triples.stream().forEach(System.out::println);
	}
}
