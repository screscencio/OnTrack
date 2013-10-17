package br.com.oncast.ontrack.shared.model.checklist;

import br.com.oncast.ontrack.shared.model.uuid.HasUUID;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.UUIDUtils;

import java.io.Serializable;

public class ChecklistItem implements Serializable, HasUUID {

	private static final long serialVersionUID = 1L;

	private UUID id;

	private String description;

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

	@Override
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
		return UUIDUtils.hashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return UUIDUtils.equals(this, obj);
	}

	public void setDescription(final String newDescription) {
		this.description = newDescription;
	}

}
