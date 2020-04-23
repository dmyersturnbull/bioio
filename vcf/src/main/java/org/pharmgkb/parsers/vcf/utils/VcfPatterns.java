package org.pharmgkb.parsers.vcf.utils;

import java.util.regex.Pattern;

/**
 * Contains static methods for regex in VCF.
 * @author Douglas Myers-Turnbull
 */
public class VcfPatterns {

	private VcfPatterns() {}

	public static final Pattern FILE_FORMAT_PATTERN = Pattern.compile("VCFv[\\d.]+");

	public static final Pattern BASES_ALT_PATTERN = Pattern.compile("(?:[AaCcGgTtNn]+)");

	public static final Pattern SYMBOLIC_ALT_PATTERN = Pattern.compile("(?:<[^>]+>)");

	private static final String sf_simpleAltPattern =
			"(?:"                   + // wrap the whole expression
				"(?:"                 + // allow nucleotides, symbolic IDs, or both
					"(?:[AaCcGgTtNn]+)" + // nucleotides
					"|(?:<.+>)"         + // symbolic IDs (declared in ALT metadata)
					")+"                + // allow things like C<ctg1> (apparently)
				"|\\*"                + // indicates that the position doesn't exist due to an upstream deletion
			")";

	private static final String sf_number =
			"(?:"                 + // wrap the whole expression
				"(?:\\d+|(?:<.+>))" + // numbers or symbolic IDs
				"(?::\\d+)?"        + // optional insertion
			")";                    // ends the nc group of the first line

	public static final Pattern ALT_BREAKPOINT_PATTERN = Pattern.compile(
			"(?:"                                                         + // wrap the whole expression
				"\\.?"                                                      + // optional opening dot
				"(?:"                                                       + // start breakpoint types
					"(?:" + sf_simpleAltPattern + "?\\[" + sf_number + "\\[)"  + // breakpoint type 1: t[p[
					"|(?:" + sf_simpleAltPattern + "?]" + sf_number + "])" + // breakpoint type 2: t]p]
					"|(?:]" + sf_number + "]" + sf_simpleAltPattern + "?)" + // breakpoint type 3: ]p]t
					"|(?:\\[" + sf_number + "\\[" + sf_simpleAltPattern + "?)" + // breakpoint type 4: [p[t
				")"                                                         + // end breakpoint types
				"\\.?"                                                      + // optional closing dot
			")"                                                             // ends the nc group of the first line
	);

	public static final Pattern UNQUOTED_EQUAL_SIGN_PATTERN = Pattern.compile("=(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

	public static final Pattern SINGLE_FORMAT_PATTERN = Pattern.compile("[A-Za-z][0-9A-Za-z.]*");
	public static final Pattern SINGLE_INFO_KEY_PATTERN = Pattern.compile("[A-Za-z][0-9A-Za-z.]*");

	public static final Pattern CONTIG_ID_PATTERN = Pattern.compile("[!-)+-<>-~][!-~]*");

}
