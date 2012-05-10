package br.com.oncast.ontrack.utils.mocks.models;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.server.services.authentication.DefaultAuthenticationCredentials;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.utils.reflection.ReflectionTestUtils;

public class UserTestUtils {

	private static User admin;

	public static User createUser() {
		return new User("user@email.com");
	}

	public static User createUser(final String email) {
		return new User(email);
	}

	public static User createUser(final long id, final String email) {
		final User user = createUser(email);
		try {
			ReflectionTestUtils.set(user, "id", id);
		}
		catch (final Exception e) {
			throw new RuntimeException("Reflection Failed when tring to create a user");
		}
		return user;
	}

	public static List<User> createList(final int size) throws Exception {
		final List<User> users = new ArrayList<User>(size);

		for (int i = 1; i <= size; i++) {
			users.add(createUser(i, "user" + i + "@email.com"));
		}
		return users;
	}

	public static User createUser(final long id) throws Exception {
		final User user = createUser("user" + id + "@email.com");
		ReflectionTestUtils.set(user, "id", id);
		return user;
	}

	public static User getAdmin() {
		return admin == null ? admin = createUser(1, DefaultAuthenticationCredentials.USER_EMAIL) : admin;
	}

}
