package br.com.oncast.ontrack.server.util.mindmapconverter.freemind;

public enum Icon {
	CLOCK("clock"),
	PENCIL("pencil"),
	IDEA("idea"),
	WIZARD("wizard");

	private final String freemindCode;

	private Icon(final String freemindCode) {
		this.freemindCode = freemindCode;
	}

	String getFreemindCode() {
		return freemindCode;
	}
}