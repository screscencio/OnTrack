package br.com.oncast.ontrack.shared.model;

import java.io.Serializable;
import java.util.Date;

import br.com.oncast.ontrack.shared.model.user.UserRepresentation;

public class ModelState<T> implements Serializable {

	private static final long serialVersionUID = 1L;

	private T value;
	private Date timestamp;
	private UserRepresentation author;

	protected ModelState() {}

	public ModelState(final T stateValue, final UserRepresentation author, final Date timestamp) {
		this.value = stateValue;
		this.author = author;
		this.timestamp = timestamp;
	}

	public T getValue() {
		return value;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public UserRepresentation getAuthor() {
		return author;
	}

	public static <T> ModelState<T> create(final T stateValue, final UserRepresentation author, final Date timestamp) {
		return new ModelState<T>(stateValue, author, timestamp);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((author == null) ? 0 : author.hashCode());
		result = prime * result + ((timestamp == null) ? 0 : timestamp.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final ModelState other = (ModelState) obj;
		if (author == null) {
			if (other.author != null) return false;
		}
		else if (!author.equals(other.author)) return false;
		if (timestamp == null) {
			if (other.timestamp != null) return false;
		}
		else if (!timestamp.equals(other.timestamp)) return false;
		if (value == null) {
			if (other.value != null) return false;
		}
		else if (!value.equals(other.value)) return false;
		return true;
	}

}