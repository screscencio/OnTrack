package br.com.oncast.ontrack.shared.model.description;

import java.io.Serializable;
import java.util.Date;

import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class Description implements Serializable {

	private static final long serialVersionUID = 1L;

	private UUID id;

	private UserRepresentation author;

	private Date timestamp;

	private String description;

	protected Description() {}

	public Description(final UUID id, final UserRepresentation author, final Date date, final String description) {
		this.id = id;
		this.setAuthor(author);
		this.setTimestamp(date);
		this.setDescription(description);
	}

	public UUID getId() {
		return id;
	}

	public void setId(final UUID id) {
		this.id = id;
	}

	public UserRepresentation getAuthor() {
		return author;
	}

	public void setAuthor(final UserRepresentation author) {
		this.author = author;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(final Date timestamp) {
		this.timestamp = timestamp;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
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
		final Description other = (Description) obj;
		if (id == null) {
			if (other.id != null) return false;
		}
		else if (!id.equals(other.id)) return false;
		return true;
	}

}
