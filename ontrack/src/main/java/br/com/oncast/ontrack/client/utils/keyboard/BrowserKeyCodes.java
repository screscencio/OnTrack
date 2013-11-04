package br.com.oncast.ontrack.client.utils.keyboard;

import java.util.HashMap;
import java.util.Map;

public class BrowserKeyCodes {

	private BrowserKeyCodes() {}

	public static final int KEY_BACKSPACE = 8;

	public static final int KEY_TAB = 9;

	public static final int KEY_ENTER = 13;

	public static final int KEY_SHIFT = 16;

	public static final int KEY_CTRL = 17;

	public static final int KEY_ALT = 18;

	public static final int KEY_ESCAPE = 27;

	public static final int KEY_SPACE = 32;

	public static final int KEY_PAGEUP = 33;

	public static final int KEY_PAGEDOWN = 34;

	public static final int KEY_END = 35;

	public static final int KEY_HOME = 36;

	public static final int KEY_LEFT = 37;

	public static final int KEY_UP = 38;

	public static final int KEY_RIGHT = 39;

	public static final int KEY_DOWN = 40;

	public static final int KEY_INSERT = 45;

	public static final int KEY_DELETE = 46;

	/**
	 * This key with Shift Modifier pressed is the KEY_EXCLAMATION_MARK
	 */
	public static final int KEY_1 = 49;

	/**
	 * This key with Shift Modifier pressed is the KEY_AT
	 */
	public static final int KEY_2 = 50;

	/**
	 * This key with Shift Modifier pressed is the KEY_SHARP
	 */
	public static final int KEY_3 = 51;

	/**
	 * This key with Shift Modifier pressed is the KEY_DOLLAR
	 */
	public static final int KEY_4 = 52;

	/**
	 * This key with Shift Modifier pressed is the KEY_PERCENT
	 */
	public static final int KEY_5 = 53;

	public static final int KEY_7 = 55;

	public static final int KEY_A = 65;

	public static final int KEY_C = 67;

	public static final int KEY_E = 69;

	public static final int KEY_F = 70;

	public static final int KEY_M = 77;

	public static final int KEY_N = 78;

	public static final int KEY_P = 80;

	public static final int KEY_R = 82;

	public static final int KEY_U = 85;

	public static final int KEY_V = 86;

	public static final int KEY_X = 88;

	public static final int KEY_Y = 89;

	public static final int KEY_Z = 90;

	public static final int KEY_F2 = 113;

	public static final int KEY_SLASH = 191;

	public static boolean isArrowKey(final int nativeKeyCode) {
		if (nativeKeyCode == KEY_LEFT || nativeKeyCode == KEY_UP || nativeKeyCode == KEY_RIGHT || nativeKeyCode == KEY_DOWN) return true;
		return false;
	}

	public static String getString(final int keyCode) {
		return KeyRepresentationMapper.getString(keyCode);
	}

	public static String getString(final int keyCode, final boolean shiftPressed) {
		return KeyRepresentationMapper.getString(keyCode, shiftPressed);
	}

	private static final class KeyRepresentationMapper {
		private static final Map<Integer, String> keysRepresentations = new HashMap<Integer, String>();
		private static final Map<Integer, String> shiftedKeysRepresentations = new HashMap<Integer, String>();

		static {

			// Same string representation with or without the shift key pressed
			keysRepresentations.put(KEY_ALT, "ALT");
			keysRepresentations.put(KEY_SPACE, "SPACE");
			keysRepresentations.put(KEY_BACKSPACE, "BACKSPACE");
			keysRepresentations.put(KEY_CTRL, "CONTROL");
			keysRepresentations.put(KEY_DELETE, "Delete");
			keysRepresentations.put(KEY_END, "End");
			keysRepresentations.put(KEY_ENTER, "ENTER");
			keysRepresentations.put(KEY_ESCAPE, "Esc");
			keysRepresentations.put(KEY_HOME, "Home");
			keysRepresentations.put(KEY_PAGEDOWN, "Page Down");
			keysRepresentations.put(KEY_PAGEUP, "Page Up");
			keysRepresentations.put(KEY_SHIFT, "SHIFT");
			keysRepresentations.put(KEY_TAB, "TAB");
			keysRepresentations.put(KEY_INSERT, "Insert");
			keysRepresentations.put(KEY_F2, "F2");
			keysRepresentations.put(KEY_LEFT, "LEFT");
			keysRepresentations.put(KEY_UP, "UP");
			keysRepresentations.put(KEY_RIGHT, "RIGHT");
			keysRepresentations.put(KEY_DOWN, "DOWN");

			// The string representation without SHIFT key pressed
			keysRepresentations.put(KEY_1, "1");
			keysRepresentations.put(KEY_2, "2");
			keysRepresentations.put(KEY_3, "3");
			keysRepresentations.put(KEY_4, "4");
			keysRepresentations.put(KEY_5, "5");
			keysRepresentations.put(KEY_7, "7");
			keysRepresentations.put(KEY_SLASH, "/");
			keysRepresentations.put(KEY_A, "a");
			keysRepresentations.put(KEY_C, "c");
			keysRepresentations.put(KEY_E, "e");
			keysRepresentations.put(KEY_F, "f");
			keysRepresentations.put(KEY_M, "m");
			keysRepresentations.put(KEY_N, "n");
			keysRepresentations.put(KEY_P, "p");
			keysRepresentations.put(KEY_R, "r");
			keysRepresentations.put(KEY_U, "u");
			keysRepresentations.put(KEY_V, "v");
			keysRepresentations.put(KEY_Y, "y");
			keysRepresentations.put(KEY_Z, "z");
			keysRepresentations.put(KEY_X, "x");

			// The string representation with SHIFT key pressed
			shiftedKeysRepresentations.put(KEY_1, "!");
			shiftedKeysRepresentations.put(KEY_2, "@");
			shiftedKeysRepresentations.put(KEY_3, "#");
			shiftedKeysRepresentations.put(KEY_4, "$");
			shiftedKeysRepresentations.put(KEY_5, "%");
			shiftedKeysRepresentations.put(KEY_7, "&");
			shiftedKeysRepresentations.put(KEY_SLASH, "?");
			shiftedKeysRepresentations.put(KEY_A, "A");
			shiftedKeysRepresentations.put(KEY_C, "C");
			shiftedKeysRepresentations.put(KEY_E, "E");
			shiftedKeysRepresentations.put(KEY_F, "F");
			shiftedKeysRepresentations.put(KEY_M, "M");
			shiftedKeysRepresentations.put(KEY_N, "N");
			shiftedKeysRepresentations.put(KEY_P, "P");
			shiftedKeysRepresentations.put(KEY_R, "R");
			shiftedKeysRepresentations.put(KEY_U, "U");
			shiftedKeysRepresentations.put(KEY_V, "V");
			shiftedKeysRepresentations.put(KEY_Y, "Y");
			shiftedKeysRepresentations.put(KEY_Z, "Z");
			shiftedKeysRepresentations.put(KEY_X, "X");

		}

		private static String getString(final int keyCode) {
			if (!keysRepresentations.containsKey(keyCode)) throw new RuntimeException(getExceptionMessage(keyCode));

			return keysRepresentations.get(keyCode);
		}

		private static String getString(final int keyCode, final boolean shiftPressed) {
			if (!keysRepresentations.containsKey(keyCode)) throw new RuntimeException(getExceptionMessage(keyCode));

			if (!shiftPressed) return getString(keyCode);

			return shiftedKeysRepresentations.containsKey(keyCode) ? shiftedKeysRepresentations.get(keyCode) : keysRepresentations.get(keyCode);
		}

		private static String getExceptionMessage(final int keyCode) {
			return "The keyCode (\"" + (char) keyCode + "\", " + keyCode + ") was not mapped";
		}
	}

	public static boolean isModifierKey(final int code) {
		return code == KEY_SHIFT || code == KEY_CTRL || code == KEY_ALT;
	}

}
