package br.com.oncast.ontrack.client.ui.generalwidgets;

import java.util.HashMap;
import java.util.Map.Entry;

import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.Label;

public class ShortcutLabel extends Label {

	private static final String REPLACEMENT_DELIMITER = "";
	public static final HashMap<String, String> replacementMap = new HashMap<String, String>();

	static {
		replacementMap.put("CONTROL", PlatformDependantKeys.getControlKey());
		replacementMap.put("ALT", PlatformDependantKeys.getAltKey());
		replacementMap.put("ENTER", "↵");
		replacementMap.put("SHIFT", "⇧");
		replacementMap.put("ARROWS", "←, →, ↑, ↓");
	}

	@UiConstructor
	public ShortcutLabel(final String text) {
		super();
		setText(text);
	}

	@Override
	public void setText(final String text) {
		super.setText(getPlatformDependantRepresentation(text));
	}

	private static String getPlatformDependantRepresentation(final String string) {
		String replacedString = string;
		for (final Entry<String, String> entry : replacementMap.entrySet())
			replacedString = replacedString.replaceAll(REPLACEMENT_DELIMITER + entry.getKey() + REPLACEMENT_DELIMITER, entry.getValue());
		return replacedString;
	}
}
