package br.com.oncast.ontrack.shared.util.uuid;

public class UUID {

	private final String uuid;
	private static final char[] CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

	public UUID(final String uuid) {
		this.uuid = uuid;
	}

	public UUID() {
		this.uuid = create();
	}

	@Override
	public String toString() {
		return this.uuid;
	}

	@Override
	public int hashCode() {
		return this.uuid.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof UUID)) return false;

		return this.uuid.equals(obj.toString());
	}

	private String create() {
		final char[] uuid = new char[36];
		int r;

		// rfc4122 requires these characters
		uuid[8] = uuid[13] = uuid[18] = uuid[23] = '-';
		uuid[14] = '4';

		// Fill in random data. At i==19 set the high bits of clock sequence as
		// per rfc4122, sec. 4.1.5
		for (int i = 0; i < 36; i++) {
			if (uuid[i] == 0) {
				r = (int) (Math.random() * 16);
				uuid[i] = CHARS[(i == 19) ? (r & 0x3) | 0x8 : r & 0xf];
			}
		}
		return new String(uuid);
	}
}