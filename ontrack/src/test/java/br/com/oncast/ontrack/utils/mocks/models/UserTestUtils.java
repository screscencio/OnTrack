package br.com.oncast.ontrack.utils.mocks.models;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.utils.reflection.ReflectionTestUtils;

public class UserTestUtils {

	public static User createUser() {
		return new User("user@email.com");
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
			final User user = createUser("user" + i + "@email.com");
			ReflectionTestUtils.set(user, "id", i);
			users.add(user);
		}
		return users;
	}

	public static User createUser(final int id) throws Exception {
		final User user = createUser("user" + id + "@email.com");
		ReflectionTestUtils.set(user, "id", id);
		return user;
	}

}
