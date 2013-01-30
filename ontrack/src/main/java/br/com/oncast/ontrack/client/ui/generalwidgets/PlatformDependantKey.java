package br.com.oncast.ontrack.client.ui.generalwidgets;

import br.com.oncast.ontrack.client.utils.RuntimeEnvironment;

enum PlatformDependantKey {
	CONTROL("⌘", "Ctrl"),
	ALT("⌥", "Alt"),
	DELETE("Fn + Delete", "Delete");

	private final String mac;
	private final String others;

	private PlatformDependantKey(final String mac, final String others) {
		this.mac = mac;
		this.others = others;
	}

	String getCurrentPlatformText() {
		return RuntimeEnvironment.isMac() ? mac : others;
	}
}