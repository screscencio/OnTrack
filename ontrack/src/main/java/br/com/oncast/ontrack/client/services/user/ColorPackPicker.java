package br.com.oncast.ontrack.client.services.user;

import br.com.oncast.ontrack.shared.model.color.ColorPack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gwt.user.client.Random;

public class ColorPackPicker {

	private List<ColorPack> available;

	public ColorPackPicker() {
		available = new ArrayList<ColorPack>(ColorPack.getDefaultColorPacks());

		// IMPORTANT Workaround because of GWT limitations
		Collections.sort(available, new Comparator<ColorPack>() {
			@Override
			public int compare(final ColorPack o1, final ColorPack o2) {
				return Random.nextInt();
			}
		});
	}

	private int index = 0;

	public ColorPack pick() {
		return available.get(index++ % available.size());
	}
}
