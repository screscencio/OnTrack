package br.com.oncast.ontrack.client.ui.generalwidgets;

import br.com.oncast.ontrack.client.utils.RuntimeEnvironment;

enum PlatformDependantKeys {
	MAC("⌘", "⌥"),
	OTHER("Ctrl", "Alt");

	private final String ctrl;
	private final String alt;

	private PlatformDependantKeys(final String ctrl, final String alt) {
		this.ctrl = ctrl;
		this.alt = alt;
	}

	public static String getAltKey() {
		return getCurrentPlatform().alt;
	}

	public static String getControlKey() {
		return getCurrentPlatform().ctrl;
	}

	static PlatformDependantKeys getCurrentPlatform() {
		return RuntimeEnvironment.isMac() ? MAC : OTHER;
	}
}