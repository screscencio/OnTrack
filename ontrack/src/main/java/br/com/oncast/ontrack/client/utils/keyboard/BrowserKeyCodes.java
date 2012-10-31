package br.com.oncast.ontrack.client.utils.keyboard;

public class BrowserKeyCodes {

	private BrowserKeyCodes() {}

	public static final int KEY_ALT = 18;

	public static final int KEY_BACKSPACE = 8;

	public static final int KEY_CTRL = 17;

	public static final int KEY_DELETE = 46;

	public static final int KEY_END = 35;

	public static final int KEY_ENTER = 13;

	public static final int KEY_ESCAPE = 27;

	public static final int KEY_HOME = 36;

	public static final int KEY_PAGEDOWN = 34;

	public static final int KEY_PAGEUP = 33;

	public static final int KEY_SHIFT = 16;

	public static final int KEY_TAB = 9;

	public static final int KEY_INSERT = 45;

	public static final int KEY_F2 = 113;

	public static final int KEY_LEFT = 37;

	public static final int KEY_UP = 38;

	public static final int KEY_RIGHT = 39;

	public static final int KEY_DOWN = 40;

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

	public static final int KEY_A = 65;

	public static final int KEY_F = 70;

	public static final int KEY_M = 77;

	public static final int KEY_N = 78;

	public static final int KEY_P = 80;

	public static final int KEY_R = 82;

	public static final int KEY_U = 85;

	public static final int KEY_Y = 89;

	public static final int KEY_Z = 90;

	public static final int KEY_SLASH = 191;

	public static boolean isArrowKey(final int nativeKeyCode) {
		if (nativeKeyCode == KEY_LEFT || nativeKeyCode == KEY_UP || nativeKeyCode == KEY_RIGHT || nativeKeyCode == KEY_DOWN) return true;
		return false;
	}
}
