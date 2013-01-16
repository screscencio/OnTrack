package br.com.oncast.ontrack.client.ui.components.scopetree.events;

import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.OneStepInternalAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.TwoStepInternalAction;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public interface ScopeTreeInternalActionHandler {

	public abstract void onInternalAction(OneStepInternalAction action);

	public abstract void onInternalAction(TwoStepInternalAction action);

	public abstract boolean hasPendingInternalAction();

	public abstract void filterByTag(UUID filteredTagId);

	void clearTagFilter();
}