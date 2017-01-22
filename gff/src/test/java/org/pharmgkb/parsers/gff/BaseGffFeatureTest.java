package org.pharmgkb.parsers.gff;

import org.junit.Test;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

	@Test
	public void testEscapeCoordinateSystemId() throws Exception {
		Feature feature = new Builder("this/needs/unescaping", "type", 0, 1).build();
	}

	@Test
	public void testZeroLength() throws Exception {
		Feature feature = new Builder("chr1", "type", 0, 0).build();
		assertEquals(0, feature.getStart());
		assertEquals(0, feature.getEnd());
	}

	@Test
	public void testNegativeStart() throws Exception {
		assertThatThrownBy(() -> new Builder("chr1", "type", -1, 1))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("Start " + -1 + " < 0");
	}

	@Test
	public void testNegativeEnd() throws Exception {
		assertThatThrownBy(() -> new Builder("chr1", "type", 1, -1))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessage("End " + -1 + " < 0");
	}

	@Test
	public void testEndBeforeStart() throws Exception {
		assertThatThrownBy(() -> new Builder("chr1", "type", 2, 1))
				.isInstanceOf(IllegalArgumentException.class)
				.hasMessageContaining("before");
	}

	private static class Feature extends BaseGffFeature {
		protected Feature(@Nonnull Builder<Feature, BaseGffFeatureTest.Builder> builder) {
			super(builder);
		}
	}

	private static class Builder extends BaseGffFeature.Builder<Feature, Builder> {

		public Builder(@Nonnull String coordinateSystemId, @Nonnull String type, @Nonnegative long start, @Nonnegative long end) {
			super(coordinateSystemId, type, start, end);
		}

		@Nonnull
		@Override
		public Feature build() {
			return new Feature(this);
		}
	}
}