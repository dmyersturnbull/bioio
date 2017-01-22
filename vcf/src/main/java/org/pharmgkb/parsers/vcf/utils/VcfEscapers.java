package org.pharmgkb.parsers.vcf.utils;

import org.pharmgkb.parsers.escape.BackslashEscaper;
import org.pharmgkb.parsers.escape.Rfc3986Escaper;

/**
 * Escaping and unescaping of illegal characters in VCF.
 * This class is not intended for everyday use because the package automatically escaapes and unescapes.
 * Therefore, use this class only if you're implementing a new VCF parser.
 * @author Douglas Myers-Turnbull
 */
public class VcfEscapers {

	private VcfEscapers() {}

	public static final BackslashEscaper METADATA = new BackslashEscaper.Builder()
			.addChars('\"', '\r', '\n', '\\')
			.build();

	public static final Rfc3986Escaper SAMPLE_NAME = new Rfc3986Escaper.Builder()
			.addChars('\t', '\n', '\r', '%')
			.build();

	public static final Rfc3986Escaper CHROMOSOME = new Rfc3986Escaper.Builder()
			.addChars(':', ' ', '\t', '\n', '\r', '%')
			.build();

	public static final Rfc3986Escaper ID = new Rfc3986Escaper.Builder()
			.addChars(';', ' ', '\t', '\n', '\r', '%')
			.build();

	public static final Rfc3986Escaper SYMBOLIC_ALT_ID = new Rfc3986Escaper.Builder()
			.addChars(',', ' ', '<', '>', '\t', '\n', '\r', '%')
			.build();

	public static final Rfc3986Escaper FILTER = new Rfc3986Escaper.Builder()
			.addChars(';', ' ', '\t', '\n', '\r', '%')
			.build();

	public static final Rfc3986Escaper INFO_VALUE = new Rfc3986Escaper.Builder()
			.addChars(';', ',', '=', '\t', '\n', '\r', '%')
			.build();

	public static final Rfc3986Escaper FORMAT = new Rfc3986Escaper.Builder()
			.addChars(':', ',', '\t', '\n', '\r', '%')
			.build();

	public static final Rfc3986Escaper SAMPLE = new Rfc3986Escaper.Builder()
			.addChars(':', '\t', '\n', '\r', '%')
			.build();

}
