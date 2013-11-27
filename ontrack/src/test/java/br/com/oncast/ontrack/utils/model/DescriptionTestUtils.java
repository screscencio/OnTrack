package br.com.oncast.ontrack.utils.model;

import br.com.oncast.ontrack.shared.model.description.Description;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.mocks.models.UserRepresentationTestUtils;

import java.util.Date;

public class DescriptionTestUtils {

	public static Description create() throws Exception {
		return new DescriptionBuilder().generate();
	}

	public static Description create(final UUID id) throws Exception {
		return new DescriptionBuilder().setId(id).generate();
	}

	public static Description create(final UserRepresentation author) throws Exception {
		return new DescriptionBuilder().setAuthor(author).generate();
	}

	public static Description create(final Date date) throws Exception {
		return new DescriptionBuilder().setDate(date).generate();
	}

	private static class DescriptionBuilder {
		private UUID id;
		private UserRepresentation author;
		private Date date;
		private String message;

		public DescriptionBuilder() throws Exception {
			this.id = new UUID();
			this.author = UserRepresentationTestUtils.createUser();
			this.date = new Date();
			this.message = "Message of annotation '" + id + "'.";
		}

		public Description generate() {
			return new Description(id, author, date, message);
		}

		public DescriptionBuilder setId(final UUID id) {
			this.id = id;
			return this;
		}

		public DescriptionBuilder setAuthor(final UserRepresentation author) {
			this.author = author;
			return this;
		}

		public DescriptionBuilder setDate(final Date date) {
			this.date = date;
			return this;
		}

		@SuppressWarnings("unused")
		public DescriptionBuilder setMessage(final String message) {
			this.message = message;
			return this;
		}

	}
}
