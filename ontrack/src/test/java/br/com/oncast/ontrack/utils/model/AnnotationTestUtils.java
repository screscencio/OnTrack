package br.com.oncast.ontrack.utils.model;

import java.util.Date;

import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.annotation.AnnotationType;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.mocks.models.UserRepresentationTestUtils;

public class AnnotationTestUtils {

	public static Annotation create() throws Exception {
		return new AnnotationBuilder().generate();
	}

	public static Annotation create(final UUID id) throws Exception {
		return new AnnotationBuilder().setId(id).generate();
	}

	public static Annotation create(final UserRepresentation author) throws Exception {
		return new AnnotationBuilder().setAuthor(author).generate();
	}

	public static Annotation create(final Date date) throws Exception {
		return new AnnotationBuilder().setDate(date).generate();
	}

	private static class AnnotationBuilder {
		private UUID id;
		private UserRepresentation author;
		private Date date;
		private String message;

		public AnnotationBuilder() throws Exception {
			this.id = new UUID();
			this.author = UserRepresentationTestUtils.createUser();
			this.date = new Date();
			this.message = "Message of annotation '" + id + "'.";
		}

		public Annotation generate() {
			return new Annotation(id, author, date, message, AnnotationType.SIMPLE);
		}

		public AnnotationBuilder setId(final UUID id) {
			this.id = id;
			return this;
		}

		public AnnotationBuilder setAuthor(final UserRepresentation author) {
			this.author = author;
			return this;
		}

		public AnnotationBuilder setDate(final Date date) {
			this.date = date;
			return this;
		}

		@SuppressWarnings("unused")
		public AnnotationBuilder setMessage(final String message) {
			this.message = message;
			return this;
		}

	}

}
