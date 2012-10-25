package br.com.oncast.ontrack.client.services.user;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.user.client.Random;

public class ColorPicker {

	private final List<String> availableColors;

	public ColorPicker() {
		availableColors = new ArrayList<String>();
		availableColors.add("#FF4D50");
		availableColors.add("#00DD00");
		availableColors.add("#9970FF");
		availableColors.add("#FFAACC");
		availableColors.add("#EE7F2B");
		availableColors.add("#07D2FF");
		availableColors.add("#227FFF");
		availableColors.add("#AAFA55");
		availableColors.add("#FFC14B");
		availableColors.add("#A8C102");
		availableColors.add("#9C7835");
		availableColors.add("#BB8F73");
		availableColors.add("#AACCFF");
		availableColors.add("#CCF56F");
		availableColors.add("#CC6FF5");
		availableColors.add("#FA55AA");

		Collections.sort(availableColors, new Comparator<String>() {
			@Override
			public int compare(final String o1, final String o2) {
				return Random.nextInt();
			}
		});
	}

	private int index = 0;

	public String pick() {
		return availableColors.get(index++ % availableColors.size());
	}

	public String pick(final double alpha) {
		final String colorString = availableColors.get(index++ % availableColors.size());
		final int r = Integer.parseInt(colorString.substring(1, 3), 16);
		final int g = Integer.parseInt(colorString.substring(3, 5), 16);
		final int b = Integer.parseInt(colorString.substring(5, 7), 16);
		return "rgba(" + r + ", " + g + ", " + b + ", " + alpha + ")";
	}
}