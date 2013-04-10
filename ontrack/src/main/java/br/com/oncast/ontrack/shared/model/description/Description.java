package br.com.oncast.ontrack.shared.model.description;

import java.io.Serializable;
import java.util.Date;

import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.HasUUID;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.UUIDUtils;

public class Description implements Serializable, HasUUID {

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

	@Override
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
		return UUIDUtils.hashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return UUIDUtils.equals(this, obj);
	}

	public boolean isEmpty() {
		return description.isEmpty();
	}

}
