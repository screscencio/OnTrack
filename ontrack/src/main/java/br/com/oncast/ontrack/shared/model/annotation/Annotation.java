package br.com.oncast.ontrack.shared.model.annotation;

import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class Annotation {

	private final UUID id;
	private final User author;
	private final String message;

	public Annotation(final UUID id, final User author, final String message) {
		this.id = id;
		this.author = author;
		this.message = message;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final Annotation other = (Annotation) obj;
		if (id == null) {
			if (other.id != null) return false;
		}
		else if (!id.equals(other.id)) return false;
		return true;
	}

	public User getAuthor() {
		return author;
	}

	public String getMessage() {
		return message;
	}

	public UUID getId() {
		return id;
	}

}
