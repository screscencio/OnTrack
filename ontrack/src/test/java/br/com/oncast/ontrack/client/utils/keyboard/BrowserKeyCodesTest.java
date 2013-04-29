package br.com.oncast.ontrack.client.utils.keyboard;

import java.lang.reflect.Field;

import org.junit.Test;

public class BrowserKeyCodesTest {

	@Test
	public void allRegisteredKeyCodesShouldHaveStringRepresentation() throws Exception {
		for (final Field field : BrowserKeyCodes.class.getDeclaredFields()) {
			if (!isKeyCode(field)) continue;

			assertHasStringRepresentation(getKeyCode(field));
		}
	}

	private void assertHasStringRepresentation(final int keyCode) {
		BrowserKeyCodes.getString(keyCode, true);
		BrowserKeyCodes.getString(keyCode);
	}

	private boolean isKeyCode(final Field field) {
		return field.getType().isAssignableFrom(int.class);
	}

	private Integer getKeyCode(final Field field) throws IllegalAccessException {
		return (Integer) field.get(null);
	}

}
