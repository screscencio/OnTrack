package br.com.oncast.ontrack.client.services.user;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.com.oncast.ontrack.shared.model.color.Color;

import com.google.gwt.user.client.Random;

public class ColorPicker {

	private List<Color> availableColors;

	public ColorPicker() {
		availableColors = Color.getPresetColors();

		// IMPORTANT Workaround because of GWT limitations
		Collections.sort(availableColors, new Comparator<Color>() {
			@Override
			public int compare(final Color o1, final Color o2) {
				return Random.nextInt();
			}
		});
	}

	private int index = 0;

	public Color pick() {
		return availableColors.get(index++ % availableColors.size()).copy();
	}

	public Color pick(final double alpha) {
		return pick().setAlpha(alpha);
	}
}