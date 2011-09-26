package br.com.oncast.ontrack.shared.model.uuid;

import java.io.Serializable;

// TODO Test this class
public class UUID implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id = "";

	// TODO +++Verify the algorithm use of the CHARS constant bellow. UUIDs should be represented by Hexadecimal characters only, but our own unique id could
	// expand this universe as we are storing it as a string (then it should have another name, eg. UniqueId). AAs we save this as a string, our UUID could use
	// more characters so that it can express more possibilities.
	private static final char[] CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

	public UUID() {
		this.id = generatedId();
	}

	public UUID(final String uuid) {
		this.id = uuid;
	}

	@Override
	public String toString() {
		return this.id;
	}

	@Override
	public int hashCode() {
		return this.id.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof UUID)) return false;
		return this.id.equals(obj.toString());
	}

	private String generatedId() {
		final char[] generatedId = new char[36];
		int r;

		// rfc4122 requires these characters
		generatedId[8] = generatedId[13] = generatedId[18] = generatedId[23] = '-';
		generatedId[14] = '4';

		// Fill in random data. At i==19 set the high bits of clock sequence as
		// per rfc4122, sec. 4.1.5
		for (int i = 0; i < 36; i++) {
			if (generatedId[i] == 0) {
				r = (int) (Math.random() * 16);
				generatedId[i] = CHARS[(i == 19) ? (r & 0x3) | 0x8 : r & 0xf];
			}
		}
		return new String(generatedId);
	}

	public String toStringRepresentation() {
		return id;
	}
}