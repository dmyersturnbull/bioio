package org.pharmgkb.parsers.bgee;

import java.util.Arrays;

/**
 * @author Douglas Myers-Turnbull
 */
public enum Quality {

	Silver, Gold, Unknown;

	public static Quality find(String name) {
		return Arrays.stream(Quality.values())
				.filter(e -> e.name().equalsIgnoreCase(name)).findAny()
				.orElse(Unknown);
	}
}
