package org.pharmgkb.parsers.model;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public enum AminoAcidCode {

	Alanine('A', "alanine", CodeType.CONCRETE),
	Cystine('C', "cystine", CodeType.CONCRETE),
	Aspartate('D', "aspartate", CodeType.CONCRETE),
	Glutamate('E', "glutamate", CodeType.CONCRETE),
	Phenylalanine('F', "phenylalanine", CodeType.CONCRETE),
	Glycine('G', "glycine", CodeType.CONCRETE),
	Histidine('H', "histidine", CodeType.CONCRETE),
	Isoleucine('I', "isoleucine", CodeType.CONCRETE),
	Lysine('K', "lysine", CodeType.CONCRETE),
	Leucine('L', "leucine", CodeType.CONCRETE),
	Methionine('M', "methionine", CodeType.CONCRETE),
	Asparagine('N', "asparagine", CodeType.CONCRETE),
	Proline('P', "proline", CodeType.CONCRETE),
	Glutamine('Q', "glutamine", CodeType.CONCRETE),
	Arginine('R', "arginine", CodeType.CONCRETE),
	Serine('S', "serine", CodeType.CONCRETE),
	Threonine('T', "threonine", CodeType.CONCRETE),
	Selenocysteine('U', "selenocysteine", CodeType.CONCRETE),
	Valine('V', "valine", CodeType.CONCRETE),
	Tryptophan('W', "tryptophan", CodeType.CONCRETE),
	AspartateOrAsparagine('B', "aspartate/asparagine", CodeType.INEXACT),
	GlutamateOrGlutamine('Z', "glutamate/glutamine", CodeType.INEXACT),
	Any('X', "any", CodeType.WILDCARD),
	Gap('-', "gap", CodeType.GAP),
	Stop('*', "stop", CodeType.STOP)
	;

	public final Character character;
	public final String name;
	public final CodeType type;

	AminoAcidCode(char character, String name, CodeType type) {
		this.character = character;
		this.name = name;
		this.type = type;
	}

	private static Map<Character, AminoAcidCode> sf_lookup = null;
	public static AminoAcidCode fromChar(char character) {
		if (sf_lookup == null) {
			sf_lookup = new HashMap<>(AminoAcidCode.values().length);
			for (AminoAcidCode c : AminoAcidCode.values()) {
				sf_lookup.put(c.character, c);
			}
		}
        if (sf_lookup.containsKey(character)) {
            return sf_lookup.get(character);
        } else {
            throw new NoSuchElementException("No amino acid code " + character);
        }
	}
}
