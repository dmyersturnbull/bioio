package org.pharmgkb.parsers.gff;

import org.junit.Test;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import static org.junit.Assert.assertEquals;

/**
 * Test {@link BaseGffFeature}.
 * @author Douglas Myers-Turnbull
 */
public class BaseGffFeatureTest {

	@Test
	public void testBasic() throws Exception {
		Feature feature = new Builder("chr1", "type", 0, 1).build();
		assertEquals("chr1", feature.getCoordinateSystemName());
		assertEquals("type", feature.getType());
		assertEquals(0, feature.getStart());
		assertEquals(1, feature.getEnd());
	}

	@Test
	public void testHard() throws Exception {
		Feature feature = new Builder("chr1", "type", 0, 1).build();
		assertEquals("chr1", feature.getCoordinateSystemName());
		assertEquals("type", feature.getType());
		assertEquals(0, feature.getStart());
		assertEquals(1, feature.getEnd());
	}

	private static class Feature extends BaseGffFeature {

		protected Feature(@Nonnull Builder builder) {
			super(builder);
		}
	}

	private static class Builder extends BaseGffFeature.Builder<Feature, Builder> {

		public Builder(@Nonnull String coordinateSystemId, @Nonnull String type, @Nonnegative long start, @Nonnegative long end) {
			super(coordinateSystemId, type, start, end);
		}

		@Override
		public Feature build() {
			return new Feature(this);
		}
	}
}