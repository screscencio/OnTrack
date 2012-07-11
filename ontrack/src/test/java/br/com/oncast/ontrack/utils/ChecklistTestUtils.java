package br.com.oncast.ontrack.utils;

import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.checklist.ChecklistItem;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ChecklistTestUtils {

	public static Checklist create() {
		final UUID uuid = new UUID();
		return new Checklist(uuid, "title " + uuid.toStringRepresentation());
	}

	public static ChecklistItem createItem(final UUID itemId, final String itemDescription, final boolean isChecked) {
		final ChecklistItem item = new ChecklistItem(itemId, itemDescription);
		item.setChecked(isChecked);
		return item;
	}

	public static ChecklistItem createItem() {
		final UUID itemId = new UUID();
		return createItem(itemId, "checklist item description " + itemId.toStringRepresentation(), true);
	}

}
