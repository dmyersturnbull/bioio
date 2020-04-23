package org.pharmgkb.parsers.utils;

import org.junit.Test;

import java.io.File;
import java.net.URISyntaxException;
import java.nio.file.Paths;

public class IoUtilsTest {

	@Test
	public void test() throws URISyntaxException {
		File file = Paths.get(IoUtils.class.getResource("file.txt").toURI()).toFile();
	}
}
