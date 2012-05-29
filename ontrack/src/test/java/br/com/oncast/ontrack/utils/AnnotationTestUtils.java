package br.com.oncast.ontrack.utils;

import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.mocks.models.UserTestUtils;

public class AnnotationTestUtils {

	public static Annotation create(final UUID id) throws Exception {
		return new Annotation(id, UserTestUtils.createUser(), "Message of annotation '" + id + "'.");
	}

}
