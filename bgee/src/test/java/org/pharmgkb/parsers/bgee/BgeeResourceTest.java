package org.pharmgkb.parsers.bgee;

import org.junit.Test;
import org.pharmgkb.parsers.model.CommonSpecies;

import java.io.BufferedReader;
import java.io.IOException;

import static org.junit.Assert.*;

public class BgeeResourceTest {

	@Test
	public void ofSpecies() throws IOException {
		try (BufferedReader reader = BgeeResource.ofSpecies(CommonSpecies.Zebrafish).newReader()) {
			reader.lines().flatMap(new BgeeExpressionParser()).forEach(System.out::println);
		}
	}

}