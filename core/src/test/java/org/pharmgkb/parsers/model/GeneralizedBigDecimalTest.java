package org.pharmgkb.parsers.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link GeneralizedBigDecimal}.
 * @author Douglas Myers-Turnbull
 */
public class GeneralizedBigDecimalTest {

	@Test
	public void testGetValue() {
		assertEquals(new GeneralizedBigDecimal("0.5").getValue().get(), new BigDecimal("0.5"));
		assertFalse(new GeneralizedBigDecimal("Inf").getValue().isPresent());
		assertFalse(new GeneralizedBigDecimal("+Inf").getValue().isPresent());
		assertFalse(new GeneralizedBigDecimal("-Inf").getValue().isPresent());
		assertFalse(new GeneralizedBigDecimal("NaN").getValue().isPresent());
	}

	@Test
	public void testToString() {
		assertEquals(new GeneralizedBigDecimal("0.5").toString(), "0.5");
		assertEquals(new GeneralizedBigDecimal("Inf").toString(), "Inf");
		assertEquals(new GeneralizedBigDecimal("+Inf").toString(), "+Inf");
		assertEquals(new GeneralizedBigDecimal("-Inf").toString(), "-Inf");
	}

	@Test
	public void testEquals() {
		assertEquals(new GeneralizedBigDecimal("0.5").toString(), new GeneralizedBigDecimal("0.5").toString());
		assertNotEquals(new GeneralizedBigDecimal("0.4").toString(), new GeneralizedBigDecimal("0.5").toString());
		assertNotEquals(new GeneralizedBigDecimal("0.5").toString(), new GeneralizedBigDecimal("Inf").toString());
		assertNotEquals(new GeneralizedBigDecimal("0.5").toString(), new GeneralizedBigDecimal("NaN").toString());
		assertNotEquals(new GeneralizedBigDecimal("Inf").toString(), new GeneralizedBigDecimal("-Inf").toString());
		assertEquals(new GeneralizedBigDecimal("+Inf").toString(), new GeneralizedBigDecimal("+Inf").toString());
	}

	@Test
	public void testCompareTo() {

		assertTrue(new GeneralizedBigDecimal("0.4").lessThan(new GeneralizedBigDecimal("0.5")));
		assertFalse(new GeneralizedBigDecimal("0.5").lessThan(new GeneralizedBigDecimal("0.5")));
		assertTrue(new GeneralizedBigDecimal("0.5").lessThanOrEqual(new GeneralizedBigDecimal("0.5")));
		assertTrue(GeneralizedBigDecimal.NEGATIVE_INFINITY.lessThan(new GeneralizedBigDecimal("0.5")));
		assertTrue(GeneralizedBigDecimal.POSITIVE_INFINITY.greaterThan(new GeneralizedBigDecimal("0.5")));

		assertFalse(GeneralizedBigDecimal.POSITIVE_INFINITY.greaterThan(GeneralizedBigDecimal.POSITIVE_INFINITY));
		assertTrue(GeneralizedBigDecimal.POSITIVE_INFINITY.greaterThanOrEqual(GeneralizedBigDecimal.POSITIVE_INFINITY));
		assertFalse(GeneralizedBigDecimal.NEGATIVE_INFINITY.greaterThan(GeneralizedBigDecimal.NEGATIVE_INFINITY));
		assertTrue(GeneralizedBigDecimal.NEGATIVE_INFINITY.greaterThanOrEqual(GeneralizedBigDecimal.NEGATIVE_INFINITY));

		UnsupportedOperationException e = assertThrows(
				UnsupportedOperationException.class,
				() -> GeneralizedBigDecimal.NAN.compareTo(GeneralizedBigDecimal.POSITIVE_INFINITY)
		);
		assertTrue(e.getMessage().contains("NaN"));

		UnsupportedOperationException e2 = assertThrows(
				UnsupportedOperationException.class,
				() -> GeneralizedBigDecimal.POSITIVE_INFINITY.compareTo(GeneralizedBigDecimal.NAN)
		);
		assertTrue(e2.getMessage().contains("NaN"));

	}
}