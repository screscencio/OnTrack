package br.com.oncast.ontrack.client.services.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.com.oncast.ontrack.client.ui.generalwidgets.utils.Color;

import com.google.gwt.user.client.Random;

public class ColorPicker {

	private final List<Color> availableColors;

	public ColorPicker() {
		availableColors = new ArrayList<Color>();
		availableColors.add(new Color("#FF4D50"));
		availableColors.add(new Color("#00DD00"));
		availableColors.add(new Color("#9970FF"));
		availableColors.add(new Color("#FFAACC"));
		availableColors.add(new Color("#EE7F2B"));
		availableColors.add(new Color("#07D2FF"));
		availableColors.add(new Color("#227FFF"));
		availableColors.add(new Color("#AAFA55"));
		availableColors.add(new Color("#FFC14B"));
		availableColors.add(new Color("#A8C102"));
		availableColors.add(new Color("#9C7835"));
		availableColors.add(new Color("#BB8F73"));
		availableColors.add(new Color("#AACCFF"));
		availableColors.add(new Color("#CCF56F"));
		availableColors.add(new Color("#CC6FF5"));
		availableColors.add(new Color("#FA55AA"));

		// IMPORTANT workarround because of GWT limitations
		Collections.sort(availableColors, new Comparator<Color>() {
			@Override
			public int compare(final Color o1, final Color o2) {
				return Random.nextInt();
			}
		});
	}

	private int index = 0;

	public Color pick() {
		return availableColors.get(index++ % availableColors.size());
	}

	public Color pick(final double alpha) {
		return pick().setAlpha(alpha);
	}
}