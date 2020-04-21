package org.pharmgkb.parsers.gff.gff3;

import org.junit.Test;
import org.pharmgkb.parsers.gff.model.Gff3Feature;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.Assert.assertEquals;

/**
 * Test {@link Gff3Feature}.
 * @author Douglas Myers-Turnbull
 */
public class Gff3FeatureTest {

	@Test
	public void testAttributes() throws Exception {
		Gff3Feature feature = new Gff3Feature.Builder("chr1", "ttt", 0, 5).setSource("source")
				.putAttributes("x", Arrays.asList("a", "b", "c"))
				.putAttributes("y", Collections.emptyList())
				.build();
		assertEquals(Arrays.asList("a", "b", "c"), feature.getAttributes("x"));
		assertEquals(Collections.emptyList(), feature.getAttributes("z"));
		assertEquals(Collections.emptyList(), feature.getAttributes("notinthemap"));
	}

	@Test
	public void testCopyConstructor() throws Exception {
		Gff3Feature a = new Gff3Feature.Builder("chr1", "ttt", 0, 5).setSource("source")
				.putAttributes("x", Arrays.asList("a", "b", "c"))
				.putAttributes("y", Collections.emptyList())
				.build();
		Gff3Feature b = new Gff3Feature.Builder(a)
				.setScore(new BigDecimal("1"))
				.setEnd(10)
				.putAttributes("extra", Collections.singletonList("extra"))
				.build();
		assertEquals(0, b.getStart());
		assertEquals(10, b.getEnd());
		assertEquals(Optional.of(new BigDecimal("1")), b.getScore());
		assertEquals(Arrays.asList("a", "b", "c"), b.getAttributes("x"));
		assertEquals(Collections.emptyList(), b.getAttributes("z"));
		assertEquals(Collections.emptyList(), b.getAttributes("notinthemap"));
		assertEquals(Collections.singletonList("extra"), b.getAttributes("extra"));
	}

}