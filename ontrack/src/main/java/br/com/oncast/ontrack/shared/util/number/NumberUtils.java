package br.com.oncast.ontrack.shared.util.number;

public class NumberUtils {

	// TODO Review this number formating method;
	public static String roundToStringMaybeWithOneFractionalDigit(final float number) {
		final String[] strings = (number + "").split("[.]");

		final char charAt = strings[1].charAt(0);
		return strings[0] + (charAt != '0' ? "." + charAt : "");
	}

}
