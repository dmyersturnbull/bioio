package org.pharmgkb.parsers.fasta.model;

import javax.annotation.Nonnegative;
import javax.annotation.concurrent.Immutable;
import java.math.BigDecimal;
import java.util.Objects;

@Immutable
public class Phred33Score implements Comparable<Phred33Score> {

    private final char m_character;

    public static Phred33Score fromChar(char character) {
        return new Phred33Score(character);
    }

    public static Phred33Score fromNumeric(int score) {
        return new Phred33Score((char)(score + 33));
    }

    private Phred33Score(char character) {
        if (character < (char)33 || character > (char)126) {
            throw new IllegalArgumentException("Value " + character + " is out of range for Phred33");
        }
        this.m_character = character;
    }

    public char getCharacter() {
        return m_character;
    }

    @Nonnegative
    public int getValue() {
        return (int)this.m_character + 33;
    }

    @Nonnegative
    public BigDecimal toLog10SangerProbability() {
        return new BigDecimal(getValue()).subtract(new BigDecimal(10));
    }

    @Override
    public String toString() {
        return String.valueOf(this.m_character);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Phred33Score that = (Phred33Score) o;
        return m_character == that.m_character;
    }

    @Override
    public int hashCode() {
        return Objects.hash(m_character);
    }

    @Override
    public int compareTo(Phred33Score o) {
        return Integer.compare(this.getValue(), o.getValue());
    }
}
