package org.pharmgkb.parsers.gff.gff3;

import org.pharmgkb.parsers.escape.Rfc3986Escaper;

/**
 * Escaping and unescaping of illegal characters in GFF3.
 * This class is not intended for everyday use because {@link Gff3Parser} automatically unescapes and {@link Gff3Writer}
 * automatically escapes.
 * Therefore, use this class only if you're implementing a new GFF3-like format (e.g. GTF or GVF).
 * @author Douglas Myers-Turnbull
 */
public class Gff3Escapers {

	public static final Rfc3986Escaper FIELDS = new Rfc3986Escaper.Builder()
			.addChars('\n', '\t', '\r', '%', ';', '=', '&', ',')
			.addCharRange(0x0, 0x20)
			.build();

	public static final Rfc3986Escaper COORDINATE_SYSTEM_IDS = new Rfc3986Escaper.Builder()
			.inverseLegality()
			.addChars('.', ':', '^', '*', '$', '@', '!', '+', '_', '?', '-', '|')
			.addCharRange(0x30, 0x39)
			.addCharRange(0x41, 0x5a)
			.addCharRange(0x61, 0x7a)
			.build();

}
