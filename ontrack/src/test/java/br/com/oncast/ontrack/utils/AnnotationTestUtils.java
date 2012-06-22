package br.com.oncast.ontrack.utils;

import java.util.Date;

import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.mocks.models.UserTestUtils;

public class AnnotationTestUtils {

	public static Annotation create(final UUID id) throws Exception {
		return new Annotation(id, UserTestUtils.createUser(), new Date(), "Message of annotation '" + id + "'.", FileRepresentationTestUtils.create());
	}

	public static Annotation create() throws Exception {
		return create(new UUID());
	}

}
