package org.pharmgkb.parsers.gff.gff3;

import org.pharmgkb.parsers.Rfc3986Escaper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Escaping and unescaping of illegal characters in GFF3.
 * This class is not intended for everyday use because {@link Gff3Parser} automatically unescapes and {@link Gff3Writer}
 * automatically escapes.
 * Therefore, use this class only if you're implementing a new GFF3-like format (e.g. GTF or GVF).
 * @author Douglas Myers-Turnbull
 */
public class Gff3Escapers {

	public static final Rfc3986Escaper FIELDS = new Fields();

	public static final Rfc3986Escaper COORDINATE_SYSTEM_IDS = new CoordinateSystemIds();

	private static class Fields extends Rfc3986Escaper {

		private static Set<Character> sf_illegals = new HashSet<>();

		static {
			sf_illegals.addAll(Arrays.asList('\n', '\t', '\r', '%', ';', '=', '&', ','));
			for (int i = 0x0; i < 0x20; i++) {
				sf_illegals.add((char)i);
			}
		}

		public Fields() {
			super(false, sf_illegals);
		}

	}

	private static class CoordinateSystemIds extends Rfc3986Escaper {

		private static Set<Character> sf_legals = new HashSet<>();

		static {
			sf_legals.addAll(Arrays.asList('.', ':', '^', '*', '$', '@', '!', '+', '_', '?', '-', '|'));
			for (int i = 0x30; i <= 0x39; i++) {
				sf_legals.add((char) i);
			}
			for (int i = 0x41; i <= 0x5a; i++) {
				sf_legals.add((char) i);
			}
			for (int i = 0x61; i <= 0x7a; i++) {
				sf_legals.add((char) i);
			}
		}

		public CoordinateSystemIds() {
			super(true, sf_legals);
		}

	}

}
