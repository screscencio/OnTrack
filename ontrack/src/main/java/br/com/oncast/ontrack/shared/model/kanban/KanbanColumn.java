package br.com.oncast.ontrack.shared.model.kanban;

import java.io.Serializable;

public class KanbanColumn implements Serializable {

	private static final long serialVersionUID = 1L;

	private String description;

	private boolean isStaticColumn = false;

	// IMPORTANT The default constructor is used by GWT and by Mind map converter to construct new scopes. Do not remove this.
	protected KanbanColumn() {}

	public KanbanColumn(final String description) {
		this.description = description;
	}

	public KanbanColumn(final String title, final boolean isStatic) {
		this.description = title;
		this.isStaticColumn = isStatic;
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
}
