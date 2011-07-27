package br.com.oncast.ontrack.client.ui.components.scopetree.actions.effort;

import java.util.Set;

import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ScopeTreeEffortUpdateEngine {

	public static void process(final ScopeTreeWidget tree, final Set<UUID> inferenceInfluencedScopeSet) throws ScopeNotFoundException {
		for (final UUID scopeId : inferenceInfluencedScopeSet)
			tree.findScopeTreeItem(scopeId).getScopeTreeItemWidget().updateDisplay();
	}
}