package br.com.oncast.ontrack.client.utils;

import br.com.oncast.ontrack.shared.model.color.Color;

public class ColorUtil {

	public static Color getTransitionColor(final Color from, final Color to, final double percent) {
		final int r = calculateTransition(from.getRed(), to.getRed(), percent);
		final int g = calculateTransition(from.getGreen(), to.getGreen(), percent);
		final int b = calculateTransition(from.getBlue(), to.getBlue(), percent);
		final double a = from.getAlpha() + (to.getAlpha() - from.getAlpha()) * percent;

		return new Color(r, g, b, a);
	}

	private static int calculateTransition(final int from, final int to, final double percent) {
		return (int) (from + (to - from) * percent);
	}

}
