package org.pharmgkb.parsers.genbank;

import com.google.common.collect.ImmutableList;
import org.junit.Test;
import org.pharmgkb.parsers.model.Locus;
import org.pharmgkb.parsers.model.Strand;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class GenbankParserTest {

	@Test
	public void test() throws Exception {
//		Path input = new File("/home/dmyerstu/desktop/repos/genome-sequence-io/genbank/src/test/resources/example.genbank").toPath();
		Path input = new File("/home/dmyerstu/desktop/14xUAS-BGi-epNTR-TagRFPT-UTR-zb3.ape").toPath();
		new GenbankParser().parseAll(input).forEach(System.out::println);
	}
}
