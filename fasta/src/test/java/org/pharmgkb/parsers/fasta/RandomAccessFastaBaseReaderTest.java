package org.pharmgkb.parsers.fasta;

import org.junit.Test;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link RandomAccessFastaBaseReader}.
 * @author Douglas Myers-Turnbull
 */
public class RandomAccessFastaBaseReaderTest {

	@Test
	public void testHeaders() throws Exception {
		File file = Paths.get(RandomAccessFastaBaseReaderTest.class.getResource("test1.fasta").toURI()).toFile();
		RandomAccessFastaBaseReader stream = new RandomAccessFastaBaseReader.Builder(file).setnCharsInBuffer(5).build();
		assertEquals(Arrays.asList("1", "2", "3"), stream.getHeaders());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testFileExists() throws Exception {
		File file = Paths.get(RandomAccessFastaBaseReaderTest.class.getResource("test1.fasta").toURI()).toFile();
		new RandomAccessFastaBaseReader.Builder(file).setnCharsInBuffer(5).keepTempFileOnExit().build();
	}

	public void debugTest() throws Exception {
		File file = Paths.get(RandomAccessFastaBaseReaderTest.class.getResource("test1.fasta").toURI()).toFile();
		RandomAccessFastaBaseReader stream = new RandomAccessFastaBaseReader.Builder(file).setnCharsInBuffer(5).build();
		System.out.println("=========================1=========================");
		for (int i = 0; i < 4; i++) {
			System.out.println(i + " = " + stream.read("1", i));
		}
		System.out.println("=========================2=========================");
		for (int i = 0; i < 12; i++) {
			System.out.println(i + " = " + stream.read("2", i));
		}
		System.out.println("=========================3=========================");
		for (int i = 0; i < 32; i++) {
			System.out.println(i + " = " + stream.read("3", i));
		}
	}

	@Test
	public void testRead() throws Exception {
		File file = Paths.get(RandomAccessFastaBaseReaderTest.class.getResource("test1.fasta").toURI()).toFile();
		RandomAccessFastaBaseReader stream = new RandomAccessFastaBaseReader.Builder(file).setnCharsInBuffer(5).build();
		assertEquals(new Character('a'), (Character)stream.read("1", 0));
		assertEquals(new Character('t'), (Character)stream.read("1", 1));
		assertEquals(new Character('c'), (Character)stream.read("1", 3));

		assertEquals(new Character('a'), (Character)stream.read("2", 0));
		assertEquals(new Character('t'), (Character)stream.read("2", 1));
		assertEquals(new Character('t'), (Character)stream.read("2", 5));
		assertEquals(new Character('t'), (Character)stream.read("1", 1));
		assertEquals(new Character('c'), (Character)stream.read("3", 16));
	}
}