package br.com.oncast.ontrack.shared.model.tag;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class Tag {

	UUID id;

	String description;

	public Tag(final UUID id, final String description) {
		this.id = id;
		this.description = description;
	}

	public UUID getId() {
		return id;
	}

	public void setId(final UUID id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

}
