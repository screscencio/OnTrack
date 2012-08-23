package br.com.oncast.ontrack.shared.model.checklist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class Checklist implements Serializable {

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

	public UUID getId() {
		return this.id;
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
		final Checklist other = (Checklist) obj;
		if (id == null) {
			if (other.id != null) return false;
		}
		else if (!id.equals(other.id)) return false;
		return true;
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

}
