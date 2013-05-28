package br.com.oncast.ontrack.shared.model.kanban;

import java.io.Serializable;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class KanbanColumn implements Serializable {

	private static final long serialVersionUID = 1L;

	private String description;

	private boolean isStaticColumn;

	private UUID id;

	// IMPORTANT serialization
	protected KanbanColumn() {}

	public KanbanColumn(final String description) {
		this(description, false);
	}

	public KanbanColumn(final String description, final boolean isStatic) {
		this.description = description.trim();
		this.isStaticColumn = isStatic;
	}

	public KanbanColumn(final String description, final UUID id) {
		this(description);
		setId(id);
	}

	public boolean isStaticColumn() {
		return isStaticColumn;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public UUID getId() {
		return id;
	}

	public void setId(final UUID id) {
		this.id = id;
	}
}
