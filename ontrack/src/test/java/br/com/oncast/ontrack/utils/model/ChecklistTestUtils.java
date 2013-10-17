package br.com.oncast.ontrack.utils.model;

import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.checklist.ChecklistItem;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ChecklistTestUtils {

	public static Checklist create() {
		final UUID uuid = new UUID();
		return new Checklist(uuid, "title " + uuid.toString());
	}

	public static ChecklistItem createItem(final UUID itemId, final String itemDescription, final boolean isChecked) {
		final ChecklistItem item = new ChecklistItem(itemId, itemDescription);
		item.setChecked(isChecked);
		return item;
	}

	public static ChecklistItem createItem() {
		final UUID itemId = new UUID();
		return createItem(itemId, "checklist item description " + itemId.toString(), true);
	}

	public static Checklist createWithItems(final int itemsCount) {
		final Checklist checklist = create();
		for (int i = 0; i < itemsCount; i++) {
			checklist.addItem(createItem());
		}
		return checklist;
	}

	public static Checklist createWithItems(final boolean... checklistItemsValues) {
		final Checklist checklist = create();
		for (final boolean value : checklistItemsValues) {
			final ChecklistItem item = createItem();
			item.setChecked(value);
			checklist.addItem(item);
		}
		return checklist;
	}

}
