package br.com.oncast.ontrack.client.ui.components.scopetree;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionRequestHandler;
import br.com.oncast.ontrack.client.ui.components.Component;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ScopeTreeAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ScopeTreeActionFactory;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.effort.ScopeTreeEffortUpdateEngine;
import br.com.oncast.ontrack.client.ui.components.scopetree.interaction.ScopeTreeInteractionHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeAction;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.util.deeplyComparable.DeeplyComparable;

import com.google.gwt.user.client.ui.Widget;

public class ScopeTree implements Component, DeeplyComparable {

	private final ScopeTreeWidget tree;
	private final ScopeTreeActionFactory treeActionFactory;
	private final ActionExecutionListener actionExecutionListener;
	private final ScopeTreeInteractionHandler treeInteractionHandler;

	public ScopeTree() {
		treeInteractionHandler = new ScopeTreeInteractionHandler();
		tree = new ScopeTreeWidget(treeInteractionHandler);
		actionExecutionListener = new ActionExecutionListener() {

			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context) {
				try {
					final ScopeTreeAction scopeTreeAction = treeActionFactory.createEquivalentActionFor(action);
					scopeTreeAction.execute(context);
					if (action instanceof ScopeAction) {
						if (((ScopeAction) action).changesEffortInference()) ScopeTreeEffortUpdateEngine.process(tree, action.getReferenceId());
					}
				}
				catch (final ScopeNotFoundException e) {
					// TODO Redraw the entire structure to eliminate inconsistencies
					throw new RuntimeException("It was not possible to update the view because an inconsistency with the model was detected.", e);
				}
			}
		};
		treeActionFactory = new ScopeTreeActionFactory(tree);
	}

	@Override
	public ActionExecutionListener getActionExecutionListener() {
		return actionExecutionListener;
	}

	@Override
	public void setActionExecutionRequestHandler(final ActionExecutionRequestHandler actionHandler) {
		treeInteractionHandler.configure(tree, actionHandler);
	}

	public void setScope(final Scope scope) {
		tree.clear();
		final ScopeTreeItem rootItem = new ScopeTreeItem(scope);

		tree.add(rootItem);
		rootItem.setState(true);
		tree.setSelected(rootItem);
	}

	@Override
	public Widget asWidget() {
		return tree;
	}

	public void setFocus(final boolean focus) {
		tree.setFocus(focus);
	}

	@Override
	public boolean deepEquals(final Object other) {
		if (!(other instanceof ScopeTree)) return false;
		return tree.deepEquals(((ScopeTree) other).asWidget());
	}
}
