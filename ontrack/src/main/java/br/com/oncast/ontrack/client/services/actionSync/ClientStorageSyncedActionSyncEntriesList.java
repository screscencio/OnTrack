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

	private List<ActionSyncEntry> entries;

	private final EventBus eventBus;

	public ClientStorageSyncedActionSyncEntriesList(final ClientStorageService storage, final EventBus eventBus) {
		this.storage = storage;
		this.eventBus = eventBus;
		this.entries = new ArrayList<ActionSyncEntry>();
		loadEntries();
	}

	public void add(final ModelAction action, final ActionContext actionContext, final ModelAction reverseAction) {
		loadEntries().add(new ActionSyncEntry(action, reverseAction, actionContext));
		saveEntries();
	}

	public ActionSyncEntry remove(final ModelAction action) {
		for (final ActionSyncEntry entry : backup()) {
			if (!entry.getAction().equals(action)) continue;

			if (loadEntries().remove(entry)) saveEntries();
			return entry;
		}
		return null;
	}

	public void clear() {
		final ArrayList<ActionSyncEntry> backup = backup();
		loadEntries().removeAll(backup);
		saveEntries();
	}

	public int size() {
		return loadEntries().size();
	}

	public boolean isEmpty() {
		return loadEntries().isEmpty();
	}

	@Override
	public Iterator<ActionSyncEntry> iterator() {
		return entries().iterator();
	}

	public List<ActionSyncEntry> entries() {
		return new ArrayList<ActionSyncEntry>(loadEntries());
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

	private ArrayList<ActionSyncEntry> backup() {
		return new ArrayList<ActionSyncEntry>(entries);
	}

	private List<ActionSyncEntry> loadEntries() {
		final ArrayList<ActionSyncEntry> backup = backup();
		entries = new ArrayList<ActionSyncEntry>(storage.loadActionSyncEntries());
		if (backup.size() != entries.size()) eventBus.fireEvent(new PendingActionsCountChangeEvent(entries.size()));
		return entries;
	}

	private void saveEntries() {
		eventBus.fireEvent(new PendingActionsCountChangeEvent(entries.size()));
		storage.storeActionSyncEntries(entries);
	}

}
