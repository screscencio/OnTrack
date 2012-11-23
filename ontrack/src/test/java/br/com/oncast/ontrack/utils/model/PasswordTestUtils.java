package br.com.oncast.ontrack.utils.model;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.server.services.authentication.Password;
import br.com.oncast.ontrack.shared.model.user.User;

public class PasswordTestUtils {

	public static List<Password> createPasswordsFor(final List<User> users) {
		final List<Password> passwords = new ArrayList<Password>();
		for (final User user : users) {
			passwords.add(createPasswordFor(user));
		}
		return passwords;
	}

	private static Password createPasswordFor(final User user) {
		return new Password(user.getId(), "password");
	}

}
