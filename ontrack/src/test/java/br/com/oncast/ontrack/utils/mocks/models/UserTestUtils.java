package br.com.oncast.ontrack.utils.mocks.models;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.server.services.authentication.DefaultAuthenticationCredentials;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class UserTestUtils {

	private static User admin;
	private static int userCount = 0;

	public static User createUser() {
		return createUser(new UUID(), "user" + ++userCount + "@email.com");
	}

	public static User createUser(final String email) {
		return createUser(new UUID(), email);
	}

	public static User createUser(final UUID id, final String email) {
		final User user = new User(id, email);
		return user;
	}

	public static List<User> createList(final int size) {
		final List<User> users = new ArrayList<User>(size);

		for (int i = 1; i <= size; i++)
			users.add(createUser());

		return users;
	}

	public static User getAdmin() {
		return admin == null ? admin = createUser(new UUID(), DefaultAuthenticationCredentials.USER_EMAIL) : admin;
	}

	public static User createUser(final UUID id) {
		return createUser(id, "user_" + id + "@email.com");
	}

}
