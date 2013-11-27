package br.com.oncast.ontrack.shared.model.checklist;

import br.com.oncast.ontrack.shared.model.uuid.HasUUID;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.UUIDUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Checklist implements Serializable, HasUUID {

	private static final long serialVersionUID = 1L;

	private String title;
	private UUID id;

	private List<ChecklistItem> items;

	protected Checklist() {}

	public Checklist(final UUID id, final String title) {
		this.title = title;
		this.id = id;
		items = new ArrayList<ChecklistItem>();
	}

	public String getTitle() {
		return title;
	}

	@Override
	public UUID getId() {
		return this.id;
	}

	@Override
	public int hashCode() {
		return UUIDUtils.hashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return UUIDUtils.equals(this, obj);
	}

	public void addItem(final ChecklistItem item) {
		if (items.contains(item)) return;

		items.add(item);
	}

	public ChecklistItem removeItem(final UUID itemId) {
		final ChecklistItem item = getItem(itemId);
		items.remove(item);
		return item;
	}

	public List<ChecklistItem> getItems() {
		return new ArrayList<ChecklistItem>(items);
	}

	public ChecklistItem getItem(final UUID itemId) {
		for (final ChecklistItem item : items) {
			if (item.getId().equals(itemId)) return item;
		}
		return null;
	}

	public void setTitle(final String newTitle) {
		this.title = newTitle;
	}

	public boolean isComplete() {
		for (final ChecklistItem item : items) {
			if (!item.isChecked()) return false;
		}
		return true;
	}

}
