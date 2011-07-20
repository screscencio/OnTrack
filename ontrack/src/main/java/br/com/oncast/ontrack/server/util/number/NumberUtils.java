package br.com.oncast.ontrack.server.util.number;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class NumberUtils {
	public static String roundEffort(final float number) {
		final DecimalFormat decimalFormat = new DecimalFormat("#.#");
		decimalFormat.setRoundingMode(RoundingMode.DOWN);

		return decimalFormat.format(number);
	}
}
