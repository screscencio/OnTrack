package br.com.oncast.ontrack.utils.mocks.models;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.server.services.authentication.DefaultAuthenticationCredentials;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class UserRepresentationTestUtils {

	private static UserRepresentation admin;

	public static UserRepresentation createUser() {
		return createUser(new UUID());
	}

	public static UserRepresentation createUser(final String email) {
		return createUser(new UUID(email));
	}

	public static UserRepresentation createUser(final UUID id, final String email) {
		final UserRepresentation user = new UserRepresentation(id);
		return user;
	}

	public static List<UserRepresentation> createList(final int size) {
		final List<UserRepresentation> users = new ArrayList<UserRepresentation>(size);

		for (int i = 1; i <= size; i++)
			users.add(createUser());

		return users;
	}

	public static UserRepresentation getAdmin() {
		return admin == null ? admin = createUser(DefaultAuthenticationCredentials.USER_ID) : admin;
	}

	public static UserRepresentation createUser(final UUID id) {
		return createUser(id, "user_" + id + "@email.com");
	}

	public static UserRepresentation createUser(final User user) {
		return new UserRepresentation(user.getId());
	}

}
