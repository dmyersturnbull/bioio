package org.pharmgkb.parsers.pedigree;

import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class PedigreeBuilderTest {

	private static Family m_family;

	@Before
	public void setUp() throws Exception {
		m_family = buildFamily();
	}

	/**
	 * Tests traversals on this family:
	 *
	 * gen 0: (A0_fc)                  [A0_ma] ----------- (A0_fa)       (A0_fb)      (A0_f_)
	 *          |                                  |                        |
	 * gen 1:   |                (?) ----------- [A1_ma]                    |
	 *          |                         |                                 |
	 * gen 2:   |       (?) ----------- [A2_ma] ----------------------------#-------------------------------[?]
	 *          |                |                 |        |        |                             |
	 * gen 3:   \------------ [A3_ma]            (A3_fa)  (A3_fb)  (A3_fc) ----------- [?]       {A3ua}
	 *                |                                                        |
	 * gen 4:       (A4_fa) ----------------------------------------------- [A4_ma]
	 *                                             |
	 * gen 5:                                   {A5_ua}
	 */
	private static Family buildFamily() throws Exception {
		PedigreeBuilder builder = new PedigreeBuilder(true);
		builder.addIndividual("f1", "A0_fb", null, null, Sex.FEMALE, Collections.emptyList());
		builder.addIndividual("f1", "A0_ma", null, null, Sex.MALE, Collections.emptyList());
		builder.addIndividual("f1", "A0_fa", null, null, Sex.FEMALE, Collections.emptyList());
		builder.addIndividual("f1", "A0_fc", null, null, Sex.FEMALE, Collections.emptyList());
		builder.addIndividual("f1", "A0_f_", null, null, Sex.FEMALE, Collections.emptyList());
		builder.addIndividual("f1", "A1_ma", "A0_ma", "A0_fa", Sex.MALE, Collections.emptyList());
		builder.addIndividual("f1", "A2_ma", "A1_ma", null, Sex.MALE, Collections.emptyList());
		builder.addIndividual("f1", "A3_fa", "A2_ma", "A0_fb", Sex.FEMALE, Collections.emptyList());
		builder.addIndividual("f1", "A3_fb", "A2_ma", "A0_fb", Sex.FEMALE, Collections.emptyList());
		builder.addIndividual("f1", "A3_fc", "A2_ma", "A0_fb", Sex.FEMALE, Collections.emptyList());
		builder.addIndividual("f1", "A3_ua", null, "A0_fb", Sex.UNKNOWN, Collections.emptyList());
		builder.addIndividual("f1", "A3_ma", "A2_ma", null, Sex.MALE, Collections.emptyList());
		builder.addIndividual("f1", "A4_ma", null, "A3_fc", Sex.MALE, Collections.emptyList());
		builder.addIndividual("f1", "A4_fa", "A3_ma", "A0_fc", Sex.FEMALE, Collections.emptyList());
		builder.addIndividual("f1", "A5_ua", "A4_ma", "A4_fa", Sex.UNKNOWN, Collections.emptyList());

		Pedigree pedigree = builder.build();
		assertNotNull(pedigree);
		Family family = pedigree.getFamily("f1");
		assertNotNull(family);

		return family;
	}

	@Test
	public void testRoots() {
		Iterator<Individual> roots = m_family.getRoots().iterator();
		assertEquals("A0_f_", roots.next().getId());
		assertEquals("A0_fa", roots.next().getId());
		assertEquals("A0_fb", roots.next().getId());
		assertEquals("A0_fc", roots.next().getId());
		assertEquals("A0_ma", roots.next().getId());
		assertFalse(roots.hasNext());
	}

	@Test
	public void testBfs() {
		Iterator<Individual> breadthFirst = m_family.breadthFirst();
		assertEquals("A0_f_", breadthFirst.next().getId());
		assertEquals("A0_fa", breadthFirst.next().getId());
		assertEquals("A0_fb", breadthFirst.next().getId());
		assertEquals("A0_fc", breadthFirst.next().getId());
		assertEquals("A0_ma", breadthFirst.next().getId());
		assertEquals("A1_ma", breadthFirst.next().getId());
		assertEquals("A3_fa", breadthFirst.next().getId());
		assertEquals("A3_fb", breadthFirst.next().getId());
		assertEquals("A3_fc", breadthFirst.next().getId());
		assertEquals("A3_ua", breadthFirst.next().getId());
		assertEquals("A4_fa", breadthFirst.next().getId());
		assertEquals("A2_ma", breadthFirst.next().getId());
		assertEquals("A4_ma", breadthFirst.next().getId());
		assertEquals("A5_ua", breadthFirst.next().getId());
		assertEquals("A3_ma", breadthFirst.next().getId());
		assertFalse(breadthFirst.hasNext());
	}

	@Test
	public void testDfs() {
		Iterator<Individual> depthFirst = m_family.depthFirst();
		assertEquals("A0_f_", depthFirst.next().getId());
		assertEquals("A3_fa", depthFirst.next().getId());
		assertEquals("A3_fb", depthFirst.next().getId());
		assertEquals("A5_ua", depthFirst.next().getId());
		assertEquals("A4_ma", depthFirst.next().getId());
		assertEquals("A3_fc", depthFirst.next().getId());
		assertEquals("A4_fa", depthFirst.next().getId());
		assertEquals("A3_ma", depthFirst.next().getId());

		assertEquals("A2_ma", depthFirst.next().getId());
		assertEquals("A1_ma", depthFirst.next().getId());
		assertEquals("A0_fa", depthFirst.next().getId());
		assertEquals("A3_ua", depthFirst.next().getId());
		assertEquals("A0_fb", depthFirst.next().getId());
		assertEquals("A0_fc", depthFirst.next().getId());
		assertEquals("A0_ma", depthFirst.next().getId());
		assertFalse(depthFirst.hasNext());
	}

	@Test
	public void testInOrder() {
		SortedSet<String> set = new TreeSet<>();
		m_family.forEach(s -> set.add(s.getId()));
		Iterator<Individual> inOrder = m_family.inOrder();
		for (String s : set) {
			assertEquals(s, inOrder.next().getId());
		}
		assertFalse(inOrder.hasNext());
	}

	@Test
	public void testTopologicalOrder() {
		Iterator<Individual> topologicalOrder = m_family.topologicalOrder();
		assertEquals("A0_f_", topologicalOrder.next().getId());
		assertEquals("A0_fa", topologicalOrder.next().getId());
		assertEquals("A0_fb", topologicalOrder.next().getId());
		assertEquals("A0_fc", topologicalOrder.next().getId());
		assertEquals("A0_ma", topologicalOrder.next().getId());
		assertEquals("A3_ua", topologicalOrder.next().getId());
		assertEquals("A1_ma", topologicalOrder.next().getId());
		assertEquals("A2_ma", topologicalOrder.next().getId());
		assertEquals("A3_fa", topologicalOrder.next().getId());
		assertEquals("A3_fb", topologicalOrder.next().getId());
		assertEquals("A3_fc", topologicalOrder.next().getId());
		assertEquals("A3_ma", topologicalOrder.next().getId());
		assertEquals("A4_ma", topologicalOrder.next().getId());
		assertEquals("A4_fa", topologicalOrder.next().getId());
		assertEquals("A5_ua", topologicalOrder.next().getId());
		assertFalse(topologicalOrder.hasNext());
	}

	@Test
	public void buildFamilyOutOfOrder() throws Exception {
		PedigreeBuilder builder = new PedigreeBuilder(false);
		builder.addIndividual("f1", "A3_fb", "A2_ma", "A0_fb", Sex.FEMALE, Collections.emptyList());
		builder.addIndividual("f1", "A0_fc", null, null, Sex.FEMALE, Collections.emptyList());
		builder.addIndividual("f1", "A2_ma", "A1_ma", null, Sex.MALE, Collections.emptyList());
		builder.addIndividual("f1", "A0_f_", null, null, Sex.FEMALE, Collections.emptyList());
		builder.addIndividual("f1", "A4_fa", "A3_ma", "A0_fc", Sex.FEMALE, Collections.emptyList());
		builder.addIndividual("f1", "A1_ma", "A0_ma", "A0_fa", Sex.MALE, Collections.emptyList());
		builder.addIndividual("f1", "A3_ma", "A2_ma", null, Sex.MALE, Collections.emptyList());
		builder.addIndividual("f1", "A0_fa", null, null, Sex.FEMALE, Collections.emptyList());
		builder.addIndividual("f1", "A0_fb", null, null, Sex.FEMALE, Collections.emptyList());
		builder.addIndividual("f1", "A4_ma", null, "A3_fc", Sex.MALE, Collections.emptyList());
		builder.addIndividual("f1", "A5_ua", "A4_ma", "A4_fa", Sex.UNKNOWN, Collections.emptyList());
		builder.addIndividual("f1", "A3_fa", "A2_ma", "A0_fb", Sex.FEMALE, Collections.emptyList());
		builder.addIndividual("f1", "A3_ua", null, "A0_fb", Sex.UNKNOWN, Collections.emptyList());
		builder.addIndividual("f1", "A0_ma", null, null, Sex.MALE, Collections.emptyList());
		builder.addIndividual("f1", "A3_fc", "A2_ma", "A0_fb", Sex.FEMALE, Collections.emptyList());

		Pedigree pedigree = builder.build(); // this is really the check
		assertNotNull(pedigree);
		Family family = pedigree.getFamily("f1");
		assertNotNull(family);
	}

	@Test
	public void testFind1() {
		Optional<Individual> found = m_family.find("A2_ma");
		assertTrue(found.isPresent());
		assertEquals("A2_ma", found.get().getId());
		assertEquals(Sex.MALE, found.get().getSex());
	}

	@Test
	public void testFind2() {
		Optional<Individual> found = m_family.find("A5_ua");
		assertTrue(found.isPresent());
		assertEquals("A5_ua", found.get().getId());
		assertTrue(found.get().getChildren().isEmpty());
		assertEquals(Sex.UNKNOWN, found.get().getSex());
	}

	@Test
	public void testTwoFamilies() throws Exception {

		PedigreeBuilder builder = new PedigreeBuilder(true);
		builder.addIndividual("f1", "A0_ma", null, null, Sex.MALE, Collections.emptyList());
		builder.addIndividual("f1", "A0_fa", null, null, Sex.FEMALE, Collections.emptyList());
		builder.addIndividual("f1", "A1_fa", "A0_ma", "A0_fa", Sex.FEMALE, Collections.emptyList());

		builder.addIndividual("f2", "B0_ma", null, null, Sex.MALE, Collections.emptyList());
		builder.addIndividual("f2", "B0_fa", null, null, Sex.FEMALE, Collections.emptyList());
		builder.addIndividual("f2", "B1_fa", "B0_ma", "B0_fa", Sex.FEMALE, Collections.emptyList());

		Pedigree pedigree = builder.build();
		assertNotNull(pedigree);

		Family family1 = pedigree.getFamily("f1");
		assertNotNull(family1);
		Iterator<Individual> depthFirst1 = family1.depthFirst();
		assertEquals("A1_fa", depthFirst1.next().getId());
		assertEquals("A0_fa", depthFirst1.next().getId());
		assertEquals("A0_ma", depthFirst1.next().getId());

		Family family2 = pedigree.getFamily("f2");
		assertNotNull(family2);
		Iterator<Individual> depthFirst2 = family2.depthFirst();
		assertEquals("B1_fa", depthFirst2.next().getId());
		assertEquals("B0_fa", depthFirst2.next().getId());
		assertEquals("B0_ma", depthFirst2.next().getId());

	}

	@Test(expected=IllegalArgumentException.class)
	public void testMissingParent() throws Exception {
		PedigreeBuilder builder = new PedigreeBuilder(true);
		builder.addIndividual("f1", "A0_fa", "asdf", null, Sex.FEMALE, Collections.emptyList()); // this is ok
		builder.build(); // this should break
	}

	@Test(expected=IllegalArgumentException.class)
	public void testDifferentFamilies() throws Exception {
		PedigreeBuilder builder = new PedigreeBuilder(true);
		builder.addIndividual("f1", "A0_fa", null, null, Sex.FEMALE, Collections.emptyList());
		builder.addIndividual("f2", "B0_fa", null, "A0_fa", Sex.FEMALE, Collections.emptyList()); // this is ok
		builder.build(); // this should break
	}
}