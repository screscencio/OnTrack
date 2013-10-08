package br.com.oncast.ontrack.client.ui.generalwidgets;

import br.com.oncast.ontrack.client.ui.keyeventhandler.Shortcut;
import br.com.oncast.ontrack.client.ui.keyeventhandler.modifier.AltModifier;
import br.com.oncast.ontrack.client.ui.keyeventhandler.modifier.ControlModifier;
import br.com.oncast.ontrack.client.ui.keyeventhandler.modifier.ShiftModifier;
import br.com.oncast.ontrack.client.ui.keyeventhandlers.ShortcutsSet;

import java.util.HashMap;
import java.util.Map.Entry;

import com.google.common.base.Joiner;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.uibinder.client.UiConstructor;
import com.google.gwt.user.client.ui.Label;

import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ALT;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_BACKSPACE;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_CTRL;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_DELETE;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_DOWN;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ENTER;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_F2;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_LEFT;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_RIGHT;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_SHIFT;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_SPACE;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_UP;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.getString;

public class ShortcutLabel extends Label {

	private static final ShorcutLabelMessages MESSAGES = GWT.create(ShorcutLabelMessages.class);

	private static final String REPLACEMENT_DELIMITER = "";

	public static final HashMap<String, String> replacementMap = new HashMap<String, String>();

	static {
		replacementMap.put(getString(KEY_CTRL), PlatformDependantKey.CONTROL.getCurrentPlatformText());
		replacementMap.put(getString(KEY_ALT), PlatformDependantKey.ALT.getCurrentPlatformText());
		replacementMap.put(getString(KEY_DELETE), PlatformDependantKey.DELETE.getCurrentPlatformText());
		replacementMap.put(getString(KEY_F2), PlatformDependantKey.F2.getCurrentPlatformText());
		replacementMap.put(getString(KEY_BACKSPACE), PlatformDependantKey.BACKSPACE.getCurrentPlatformText());
		replacementMap.put(getString(KEY_ENTER), "↵");
		replacementMap.put(getString(KEY_SHIFT), "⇧");
		replacementMap.put(getString(KEY_UP), "↑");
		replacementMap.put(getString(KEY_DOWN), "↓");
		replacementMap.put(getString(KEY_LEFT), "←");
		replacementMap.put(getString(KEY_RIGHT), "→");
		replacementMap.put(getString(KEY_SPACE), "Space");
		replacementMap.put("ARROWS", "←, →, ↑, ↓");
	}

	public ShortcutLabel(final Shortcut shortcut) {
		this(toString(shortcut));
	}

	public ShortcutLabel(final ShortcutsSet shortcuts) {
		this(Joiner.on(" " + MESSAGES.or() + " ").join(shortcuts));
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

	public static String toString(final Shortcut s) {
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
