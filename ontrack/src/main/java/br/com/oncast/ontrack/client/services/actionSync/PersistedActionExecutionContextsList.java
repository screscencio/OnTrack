package br.com.oncast.ontrack.client.services.actionSync;

import br.com.oncast.ontrack.client.services.storage.ClientStorageService;
import br.com.oncast.ontrack.client.ui.events.PendingActionsCountChangeEvent;
import br.com.oncast.ontrack.shared.model.action.UserAction;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecutionContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.web.bindery.event.shared.EventBus;

public class PersistedActionExecutionContextsList implements Iterable<ActionExecutionContext> {

	private final ClientStorageService storage;

	private List<ActionExecutionContext> entries;

	private final EventBus eventBus;

	private final UUID projectId;

	public PersistedActionExecutionContextsList(final UUID projectId, final ClientStorageService storage, final EventBus eventBus) {
		this.projectId = projectId;
		this.storage = storage;
		this.eventBus = eventBus;
		this.entries = new ArrayList<ActionExecutionContext>();
		loadEntries();
	}

	public void add(final ActionExecutionContext executionContext) {
		if (loadEntries().contains(executionContext)) return;
		entries.add(executionContext);
		saveEntries();
	}

	public ActionExecutionContext remove(final UserAction action) {
		for (final ActionExecutionContext entry : backup()) {
			if (!entry.getUserAction().equals(action)) continue;

			if (loadEntries().remove(entry)) saveEntries();
			return entry;
		}
		return null;
	}

	public void clear() {
		final ArrayList<ActionExecutionContext> backup = backup();
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
	public Iterator<ActionExecutionContext> iterator() {
		return entries().iterator();
	}

	public List<ActionExecutionContext> entries() {
		return new ArrayList<ActionExecutionContext>(loadEntries());
	}

	/**
	 * Returns a copy of the entries list and reverse it. <br/>
	 * This does not modifies the original list.<br/>
	 * This is a helper method because reverse actions should be applied backwards.
	 * 
	 * @return reversed list of entries
	 */
	public List<ActionExecutionContext> reverse() {
		final List<ActionExecutionContext> copy = entries();
		Collections.reverse(copy);
		return copy;
	}

	private ArrayList<ActionExecutionContext> backup() {
		return new ArrayList<ActionExecutionContext>(entries);
	}

	private List<ActionExecutionContext> loadEntries() {
		final ArrayList<ActionExecutionContext> backup = backup();
		entries = new ArrayList<ActionExecutionContext>(storage.loadPendingActionExecutionContexts(projectId));
		if (backup.size() != entries.size()) eventBus.fireEvent(new PendingActionsCountChangeEvent(entries.size()));
		return entries;
	}

	private void saveEntries() {
		eventBus.fireEvent(new PendingActionsCountChangeEvent(entries.size()));
		storage.storePendingActionExecutionContexts(projectId, entries);
	}

}
