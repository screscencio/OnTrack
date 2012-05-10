package br.com.oncast.ontrack.client.ui.components.scopetree;

import java.util.HashSet;
import java.util.Set;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionRequestHandler;
import br.com.oncast.ontrack.client.ui.components.Component;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ScopeTreeActionFactory;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.effort.ScopeTreeEffortUpdateEngine;
import br.com.oncast.ontrack.client.ui.components.scopetree.interaction.ScopeTreeInstructionGuide;
import br.com.oncast.ontrack.client.ui.components.scopetree.interaction.ScopeTreeInteractionHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.interaction.ScopeTreeShortcutMappings;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.client.ui.keyeventhandler.ShortcutService;
import br.com.oncast.ontrack.shared.model.ModelBeanNotFoundException;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.user.client.ui.Widget;

public class ScopeTree implements Component {

	private final ScopeTreeWidget tree;

	private final ScopeTreeActionFactory treeActionFactory;

	private final ActionExecutionListener actionExecutionListener;

	private final ScopeTreeInteractionHandler treeInteractionHandler;

	private final ScopeTreeInstructionGuide instructionGuide;

	public ScopeTree() {
		treeInteractionHandler = new ScopeTreeInteractionHandler();
		tree = new ScopeTreeWidget(treeInteractionHandler);

		ShortcutService.register(tree, treeInteractionHandler, ScopeTreeShortcutMappings.values());

		instructionGuide = new ScopeTreeInstructionGuide(tree);

		actionExecutionListener = new ActionExecutionListener() {

			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final Set<UUID> inferenceInfluencedScopeSet,
					final boolean isUserAction) {
				try {
					treeActionFactory.createEquivalentActionFor(action).execute(context, isUserAction);
					final HashSet<Scope> inferenceInfluencedScopes = new HashSet<Scope>();
					for (final UUID id : inferenceInfluencedScopeSet)
						inferenceInfluencedScopes.add(context.findScope(id));

					ScopeTreeEffortUpdateEngine.process(tree, inferenceInfluencedScopes);

					instructionGuide.onActionExecution(action);
				}
				catch (final RuntimeException e) {
					// This happens when the incoming action is not important for ScopeTree
				}
				catch (final ModelBeanNotFoundException e) {
					// TODO ++Resync and Redraw the entire structure to eliminate inconsistencies
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

	public void setContext(final ProjectContext context) {
		treeInteractionHandler.setContext(context);
		tree.clear();
		final ScopeTreeItem rootItem = new ScopeTreeItem(context.getProjectScope());

		tree.add(rootItem);
		rootItem.mountTwoLevels();
		rootItem.setState(true);
		tree.setSelectedItem(rootItem);

		instructionGuide.onSetContext(context);
	}

	@Override
	public Widget asWidget() {
		return tree;
	}

	public void setFocus(final boolean focus) {
		tree.setFocus(focus);
	}

	public void setSelectedScope(final Scope scope) throws ScopeNotFoundException {
		final ScopeTreeItem scopeTreeItem = tree.findScopeTreeItem(scope);
		scopeTreeItem.setHierarchicalState(true);
		tree.setSelectedItem(scopeTreeItem);
	}
}
