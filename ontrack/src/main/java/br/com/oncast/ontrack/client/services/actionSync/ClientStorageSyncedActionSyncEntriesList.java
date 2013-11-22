package br.com.oncast.ontrack.client.services.actionSync;

import br.com.oncast.ontrack.client.services.storage.ClientStorageService;
import br.com.oncast.ontrack.client.ui.events.PendingActionsCountChangeEvent;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.web.bindery.event.shared.EventBus;

public class ClientStorageSyncedActionSyncEntriesList implements Iterable<ActionSyncEntry> {

	private final ClientStorageService storage;

	private final List<ActionSyncEntry> entries;

	private final EventBus eventBus;

	public ClientStorageSyncedActionSyncEntriesList(final ClientStorageService storage, final EventBus eventBus) {
		this.storage = storage;
		this.eventBus = eventBus;
		this.entries = new ArrayList<ActionSyncEntry>(storage.loadActionSyncEntries());
	}

	public void add(final ModelAction action, final ActionContext actionContext, final ModelAction reverseAction) {
		entries.add(new ActionSyncEntry(action, reverseAction, actionContext));
		save();
	}

	public ActionSyncEntry remove(final ModelAction action) {
		for (final ActionSyncEntry entry : entries()) {
			if (!entry.getAction().equals(action)) continue;

			entries.remove(entry);
			save();
			return entry;
		}
		return null;
	}

	public void clear() {
		entries.clear();
		save();
	}

	public int size() {
		return entries.size();
	}

	public boolean isEmpty() {
		return entries.isEmpty();
	}

	@Override
	public Iterator<ActionSyncEntry> iterator() {
		return entries().iterator();
	}

	public List<ActionSyncEntry> entries() {
		return new ArrayList<ActionSyncEntry>(entries);
	}

	/**
	 * Returns a copy of the entries list and reverse it. <br/>
	 * This does not modifies the original list.<br/>
	 * This is a helper method because reverse actions should be applied backwards.
	 * 
	 * @return reversed list of entries
	 */
	public List<ActionSyncEntry> reverse() {
		final List<ActionSyncEntry> copy = entries();
		Collections.reverse(copy);
		return copy;
	}

	private void save() {
		eventBus.fireEvent(new PendingActionsCountChangeEvent(entries.size()));
		storage.storeActionSyncEntries(entries);
	}

}
