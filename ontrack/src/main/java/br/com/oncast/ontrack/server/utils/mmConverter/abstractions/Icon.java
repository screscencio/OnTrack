package br.com.oncast.ontrack.server.utils.mmConverter.abstractions;

public enum Icon {
	INFO("info"),
	CALENDAR("calendar"),
	LAUNCH("launch"),
	WIZARD("wizard"),
	HOURGLASS("hourglass"),
	LIST("list"),
	CLOCK("clock"),
	PENCIL("pencil"),
	IDEA("idea"),
	DOWN("down"),
	UP("up"),
	OK("button_ok"),
	KEY("password");

	private final String freemindCode;

	private Icon(final String freemindCode) {
		this.freemindCode = freemindCode;
	}

	String getFreemindCode() {
		return freemindCode;
	}
}
