package br.com.oncast.ontrack.shared.model.checklist;

import java.io.Serializable;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ChecklistItem implements Serializable {

	private static final long serialVersionUID = 1L;

	private String description;
	private UUID id;

	private boolean checked;

	protected ChecklistItem() {}

	public ChecklistItem(final UUID id, final String description) {
		this.id = id;
		this.description = description;
		this.checked = false;
	}

	public String getDescription() {
		return description;
	}

	public UUID getId() {
		return id;
	}

	public void setChecked(final boolean b) {
		checked = b;
	}

	public boolean isChecked() {
		return checked;
	}

}
