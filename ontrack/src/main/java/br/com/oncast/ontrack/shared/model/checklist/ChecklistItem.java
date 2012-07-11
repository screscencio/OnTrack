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
		final ChecklistItem other = (ChecklistItem) obj;
		if (id == null) {
			if (other.id != null) return false;
		}
		else if (!id.equals(other.id)) return false;
		return true;
	}

}
