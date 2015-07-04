package org.pharmgkb.parsers.fasta;

import com.google.common.io.Files;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link SimpleFastaBaseReader}.
 * @author Douglas Myers-Turnbull
 */
public class SimpleFastaBaseReaderTest {

	@Test
	public void testReadNext() throws Exception {
		File file = Paths.get(SimpleFastaBaseReaderTest.class.getResource("test1.fasta").toURI()).toFile();
		try (SimpleFastaBaseReader stream = new SimpleFastaBaseReader.Builder(file).build()) {
			test(stream);
		}
	}

	@Test
	public void testReadNextWithRefill() throws Exception {
		File file = Paths.get(SimpleFastaBaseReaderTest.class.getResource("test1.fasta").toURI()).toFile();
		try (SimpleFastaBaseReader stream = new SimpleFastaBaseReader.Builder(file)
				.setnCharsInBuffer(3)
				.build()) {
			test(stream);
		}
	}

	@Test
	public void testReadNextWithBlankLine() throws Exception {
		File file = Paths.get(SimpleFastaBaseReaderTest.class.getResource("test1.fasta").toURI()).toFile();
		String string = Files.toString(file, Charset.defaultCharset()) + "\n";
		try (SimpleFastaBaseReader stream = new SimpleFastaBaseReader.Builder(new StringReader(string))
				.setnCharsInBuffer(3)
				.build()) {
			test(stream);
		}
	}

	private void test(SimpleFastaBaseReader stream) throws IOException {

		Map<String, String> expected = new LinkedHashMap<>();
		expected.put("1", "atgc");
		expected.put("2", repeat("atgc", 3));
		expected.put("3", repeat("a", 16) + repeat("c", 16));

		// we always add 1 because i starts at 0
		// for getnBytesReadTotal, we have to count the line breaks and headers

		for (int i = 0; i < 4; i++) {
			Optional<Character> next = stream.readNextBase();
			assertTrue(next.isPresent());
			assertEquals("i=" + i, "1", stream.currentHeader().get());
			assertEquals("i=" + i, 1 + i, stream.nBasesSinceHeader());
			assertEquals("i=" + i, 1 + i, stream.nBasesReadTotal());
			assertEquals("i=" + i, 1, stream.nHeadersRead());
			assertEquals("i=" + i, expected.get("1").charAt(i), (char)next.get());
			assertEquals("i=" + i, 4 + i, stream.nBytesReadTotal());
		}
		long prevBytesRead = stream.nBytesReadTotal();

		for (int i = 0; i < 4 * 3; i++) {
			Optional<Character> next = stream.readNextBase();
			assertTrue(next.isPresent());
			assertEquals("i=" + i, "2", stream.currentHeader().get());
			assertEquals("i=" + i, 1 + i, stream.nBasesSinceHeader());
			assertEquals("i=" + i, 1 + 4 + i, stream.nBasesReadTotal());
			assertEquals("i=" + i, 2, stream.nHeadersRead());
			assertEquals("i=" + i, expected.get("2").charAt(i), (char)next.get());
			assertEquals("i=" + i, 1 + 4 + prevBytesRead + i + (i / 4), stream.nBytesReadTotal());
		}

		for (int i = 0; i < 16 * 2; i++) {
			Optional<Character> next = stream.readNextBase();
			assertTrue(next.isPresent());
			assertEquals("i=" + i, "3", stream.currentHeader().get());
			assertEquals("i=" + i, 1 + i, stream.nBasesSinceHeader());
			assertEquals("i=" + i, 1 + 4 + 4 * 3 + i, stream.nBasesReadTotal());
			assertEquals("i=" + i, 3, stream.nHeadersRead());
			assertEquals("i=" + i, expected.get("3").charAt(i), (char)next.get());
		}
	}

	private String repeat(String string, int n) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < n; i++) {
			sb.append(string);
		}
		return sb.toString();
	}
}