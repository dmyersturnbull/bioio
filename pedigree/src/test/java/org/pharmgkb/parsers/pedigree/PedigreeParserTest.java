package org.pharmgkb.parsers.pedigree;

import org.junit.Test;
import org.pharmgkb.parsers.pedigree.model.Family;
import org.pharmgkb.parsers.pedigree.model.Individual;
import org.pharmgkb.parsers.pedigree.model.Pedigree;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests {@link PedigreeParser}.
 * @author Douglas Myers-Turnbull
 */
public class PedigreeParserTest {

	/**
	 <pre>
	  gen 1:        [1] ----------- (2)             [3]
	                        |                       |
	  gen 2:               (4) ---------------------/
	                               |           |
	  gen 3:                      (5)         [6]


	  1   1   0  0  1
	  1   2   0  0  2
	  1   3   0  0  1
	  1   4   1  2  2
	  1   5   3  4  2
	  1   6   3  4  1
	  </pre>

	 * @throws Exception For issues
	 */
	@Test
	public void testWithoutData() throws Exception {

		Path file = Paths.get(getClass().getResource("without_data.ped").toURI());
		PedigreeParser parser = new PedigreeParser.Builder().build();
		Pedigree pedigree = parser.apply(Files.lines(file));
		assertEquals(1, pedigree.getFamilies().size());
		Family family = pedigree.getFamily("1");
		assertNotNull(family);

		Iterator<Individual> roots = family.getRoots().iterator();
		assertEquals("1", roots.next().getId());
		assertEquals("2", roots.next().getId());
		assertEquals("3", roots.next().getId());
		assertFalse(roots.hasNext());

		Optional<Individual> three = family.find("3");
		Optional<Individual> four = family.find("4");
		Optional<Individual> five = family.find("5");
		Optional<Individual> six = family.find("6");

		assertTrue(three.isPresent());
		assertTrue(four.isPresent());
		assertTrue(five.isPresent());
		assertTrue(six.isPresent());

		assertEquals(four, five.get().getMother());
		assertEquals(three, five.get().getFather());
		assertTrue(three.get().getChildren().contains(five.get()));
		assertTrue(three.get().getChildren().contains(six.get()));
		assertTrue(four.get().getChildren().contains(five.get()));
		assertTrue(four.get().getChildren().contains(six.get()));

	}

	/**
	  <pre>
	  gen 1:        [1] ----------- (2)             [3]
	                        |                       |
	  gen 2:               (4) ---------------------/
	                               |           |
	  gen 3:                      (5)         [6]


	 1   1   0  0  1   1      x   3 3   x x
	 1   2   0  0  2   1      x   4 4   x x
	 1   3   0  0  1   1      x   1 2   x x
	 1   4   1  2  2   1      x   4 3   x x
	 1   5   3  4  2   2  1.234   1 3   2 2
	 1   6   3  4  1   2  4.321   2 4   2 2
	 * </pre>
	 * @throws Exception For issues
	 */
	@Test
	public void testWithData() throws Exception {

		Path file = Paths.get(getClass().getResource("with_data.ped").toURI());
		PedigreeParser parser = new PedigreeParser.Builder().build();
		Pedigree pedigree = parser.apply(Files.lines(file));
		Family family = pedigree.getFamily("1");
		assertNotNull(family);

		Optional<Individual> six = family.find("6");
		assertTrue(six.isPresent());
		List<String> expected6 = Arrays.asList("4.321", "2", "4", "2", "2");
		assertEquals(expected6, six.get().getInfo());
	}
}