package br.com.oncast.ontrack.client.ui.generalwidgets;

import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ALT;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_CTRL;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_DOWN;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ENTER;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_LEFT;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_RIGHT;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_SHIFT;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_UP;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.getString;

import java.util.HashMap;
import java.util.Map.Entry;

import br.com.oncast.ontrack.client.ui.keyeventhandler.Shortcut;
import br.com.oncast.ontrack.client.ui.keyeventhandler.modifier.AltModifier;
import br.com.oncast.ontrack.client.ui.keyeventhandler.modifier.ControlModifier;
import br.com.oncast.ontrack.client.ui.keyeventhandler.modifier.ShiftModifier;

import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.Label;

public class ShortcutLabel extends Label {

	private static final String REPLACEMENT_DELIMITER = "";
	public static final HashMap<String, String> replacementMap = new HashMap<String, String>();

	static {
		replacementMap.put(getString(KEY_CTRL), PlatformDependantKeys.getControlKey());
		replacementMap.put(getString(KEY_ALT), PlatformDependantKeys.getAltKey());
		replacementMap.put(getString(KEY_ENTER), "↵");
		replacementMap.put(getString(KEY_SHIFT), "⇧");
		replacementMap.put(getString(KEY_UP), "↑");
		replacementMap.put(getString(KEY_DOWN), "↓");
		replacementMap.put(getString(KEY_LEFT), "←");
		replacementMap.put(getString(KEY_RIGHT), "→");
		replacementMap.put("ARROWS", "←, →, ↑, ↓");
	}

	public ShortcutLabel(final Shortcut shortcut) {
		this(toString(shortcut));
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

	private static String toString(final Shortcut s) {
		String text = "";
		text += s.getCtrl() == ControlModifier.PRESSED ? "CONTROL + " : "";
		text += s.getShift() == ShiftModifier.PRESSED ? "SHIFT + " : "";
		text += s.getAlt() == AltModifier.PRESSED ? "ALT + " : "";
		text += getString(s.getKeyCode(), ShiftModifier.UNPRESSED != s.getShift());

		return text;
	}

	private static String getPlatformDependantRepresentation(final String string) {
		String replacedString = string;
		for (final Entry<String, String> entry : replacementMap.entrySet())
			replacedString = replacedString.replaceAll(REPLACEMENT_DELIMITER + entry.getKey() + REPLACEMENT_DELIMITER, entry.getValue());
		return replacedString;
	}
}
