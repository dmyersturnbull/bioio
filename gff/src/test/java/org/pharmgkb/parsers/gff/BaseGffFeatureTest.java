package org.pharmgkb.parsers.gff;

import org.junit.jupiter.api.Test;
import org.pharmgkb.parsers.gff.model.BaseGffFeature;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test {@link BaseGffFeature}.
 * @author Douglas Myers-Turnbull
 */
public class BaseGffFeatureTest {

	@Test
	public void testBasic() {
		Feature feature = new Builder("chr1", "type", 0, 1).build();
		assertEquals("chr1", feature.getCoordinateSystemName());
		assertEquals("type", feature.getType());
		assertEquals(0, feature.getStart());
		assertEquals(1, feature.getEnd());
	}

	@Test
	public void testEscapeCoordinateSystemId() {
		Feature feature = new Builder("this/needs/unescaping", "type", 0, 1).build();
	}

	@Test
	public void testZeroLength() {
		Feature feature = new Builder("chr1", "type", 0, 0).build();
		assertEquals(0, feature.getStart());
		assertEquals(0, feature.getEnd());
	}

	@Test
	public void testNegativeStart() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new Builder("chr1", "type", -1, 1));
		assertTrue(e.getMessage().toLowerCase(Locale.ROOT).contains("start " + -1 + " < 0"));
	}

	@Test
	public void testNegativeEnd() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new Builder("chr1", "type", 1, -1));
		assertTrue(e.getMessage().toLowerCase(Locale.ROOT).contains("end " + -1 + " < 0"));
	}

	@Test
	public void testEndBeforeStart() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> new Builder("chr1", "type", 2, 1));
		assertTrue(e.getMessage().toLowerCase(Locale.ROOT).contains("before"));
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
		public Feature build() {
			return new Feature(this);
		}
	}
}