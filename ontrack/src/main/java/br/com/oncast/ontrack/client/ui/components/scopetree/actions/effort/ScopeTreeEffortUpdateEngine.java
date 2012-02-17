package br.com.oncast.ontrack.client.ui.components.scopetree.actions.effort;

import java.util.Set;

import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;

public class ScopeTreeEffortUpdateEngine {

	public static void process(final ScopeTreeWidget tree, final Set<Scope> inferenceInfluencedScopes) throws ScopeNotFoundException {
		for (final Scope scope : inferenceInfluencedScopes)
			tree.findScopeTreeItem(scope).getScopeTreeItemWidget().updateDisplay();
	}
}