package org.pharmgkb.parsers.model;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public enum NucleotideCode {

	Tymidine('T', "thymidine", CodeType.CONCRETE),
	Cytidine('C', "cytidine", CodeType.CONCRETE),
	Guanine('G', "guanine", CodeType.CONCRETE),
	Keto('U', "uridine", CodeType.CONCRETE),
	Uridine('K', "keto", CodeType.INEXACT),
	Amino('M', "amino", CodeType.INEXACT),
	Strong('S', "strong", CodeType.INEXACT),
	Weak('W', "weak", CodeType.INEXACT),
	Purine('R', "purine", CodeType.INEXACT),
	Pyrimidine('Y', "pyrimidine", CodeType.INEXACT),
	B('B', "G/T/C", CodeType.INEXACT),
	D('D', "G/A/T", CodeType.INEXACT),
	H('H', "H/C/T", CodeType.INEXACT),
	V('V', "G/C/A", CodeType.INEXACT),
	Any('N', "any", CodeType.WILDCARD),
	Gap('-', "gap", CodeType.GAP), // of indeterminate length
	Stop('*', "stop", CodeType.STOP)
	;

	public final Character character;
	public final String name;
	public final CodeType type;

	NucleotideCode(char character, String name, CodeType type) {
		this.character = character;
		this.name = name;
		this.type = type;
	}

	private static Map<Character, NucleotideCode> sf_lookup = null;
	public static NucleotideCode fromChar(char character) {
		if (sf_lookup == null) {
			sf_lookup = new HashMap<>(NucleotideCode.values().length);
			for (NucleotideCode c : NucleotideCode.values()) {
				sf_lookup.put(c.character, c);
			}
		}
		if (sf_lookup.containsKey(character)) {
			return sf_lookup.get(character);
		} else {
			throw new NoSuchElementException("No nucleotide code " + character);
		}
    }
}

