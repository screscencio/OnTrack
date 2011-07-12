package br.com.oncast.ontrack.shared.util.number;

public class NumberUtils {

	public static String roundToStringWithOneFractionalDigit(final float number) {
		final String[] strings = (number + "").split("[.]");

		final char charAt = strings[1].charAt(0);
		return strings[0] + (charAt != '0' ? "." + charAt : "");
	}

}
