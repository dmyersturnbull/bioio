package org.pharmgkb.parsers.gff.gff3;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

/**
 * Test {@link Gff3Feature}.
 * @author Douglas Myers-Turnbull
 */
public class Gff3FeatureTest {

	@Test
	public void testAttributes() throws Exception {
		Gff3Feature feature = new Gff3Feature.Builder("chr1", "ttt", 0, 5)
				.putAttributes("x", Arrays.asList("a", "b", "c"))
				.putAttributes("y", Collections.emptyList())
				.build();
		assertEquals(Arrays.asList("a", "b", "c"), feature.getAttributes("x"));
		assertEquals(Collections.emptyList(), feature.getAttributes("z"));
		assertEquals(Collections.emptyList(), feature.getAttributes("notinthemap"));
	}
}