package br.com.oncast.ontrack.utils;

import java.util.Date;

import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.mocks.models.UserTestUtils;

public class AnnotationTestUtils {

	public static Annotation create() throws Exception {
		return create(new UUID());
	}

	public static Annotation create(final UUID id) throws Exception {
		return create(id, UserTestUtils.createUser());
	}

	public static Annotation create(final User author) throws Exception {
		return create(new UUID(), author);
	}

	private static Annotation create(final UUID id, final User author) {
		return new Annotation(id, author, new Date(), "Message of annotation '" + id + "'.");
	}

}
