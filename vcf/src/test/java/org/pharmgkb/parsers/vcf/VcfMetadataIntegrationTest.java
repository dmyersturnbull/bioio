package org.pharmgkb.parsers.vcf;

import java.io.IOException;


/**
 * @author Douglas Myers-Turnbull
 */
public class VcfMetadataIntegrationTest {

	public void testLines() throws IOException {
		// TODO: Move to integration test package
//
//		Path path = new File("/Users/student/genome-sequence-io/vcf/src/test/resources/org/pharmgkb/parsers/vcf/example.vcf").toPath();
////    	Path path = Paths.get(getClass().getResource("example.vcf").toURI());
//		Stream<String> input = Files.lines(path);
//
//		VcfMetadataParser parser = new VcfMetadataParser();
//		VcfMetadataCollection metadata = parser.apply(input);
//		List<String> expectedLines = Files.readAllLines(path);
//
//		assertEquals("4.2", metadata.getVcfVersion());
//
//		assertEquals(22, parser.nLinesProcessed()); // make sure it didn't read the data lines
//		assertEquals(22, metadata.getLines().size());
//
//		VcfMetadataWriter writer = new VcfMetadataWriter();
//		List<String> actualLines = metadata.getLines().stream()
//				.map(writer)
//				.collect(Collectors.toList());
//
//		for (int i = 0; i < 22; i++) {
//			assertEquals("Line " + i + " is wrong", expectedLines.get(i), actualLines.get(i));
//		}
//
//		assertEquals(Arrays.asList("NA00001", "NA00002", "NA00003"), metadata.getSampleNames());
	}
}