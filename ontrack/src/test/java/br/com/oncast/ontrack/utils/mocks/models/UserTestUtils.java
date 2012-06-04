package br.com.oncast.ontrack.utils.mocks.models;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.server.services.authentication.DefaultAuthenticationCredentials;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.utils.reflection.ReflectionTestUtils;

public class UserTestUtils {

	private static User admin;
	private static int userCount = 0;

	public static User createUser() throws Exception {
		return createUser(++userCount);
	}

	public static User createUser(final String email) {
		return new User(email);
	}

	public static User createUser(final long id, final String email) throws Exception {
		final User user = createUser(email);
		ReflectionTestUtils.set(user, "id", id);
		return user;
	}

	public static List<User> createList(final int size) throws Exception {
		final List<User> users = new ArrayList<User>(size);

		for (int i = 1; i <= size; i++) {
			users.add(createUser());
		}
		return users;
	}

	public static User createUser(final long id) throws Exception {
		final User user = createUser("user" + id + "@email.com");
		ReflectionTestUtils.set(user, "id", id);
		return user;
	}

	public static User getAdmin() throws Exception {
		return admin == null ? admin = createUser(1, DefaultAuthenticationCredentials.USER_EMAIL) : admin;
	}

}
