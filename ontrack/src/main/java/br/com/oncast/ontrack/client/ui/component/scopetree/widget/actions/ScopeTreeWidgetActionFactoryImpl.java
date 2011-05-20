package br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions;

import br.com.oncast.ontrack.client.ui.component.scopetree.actions.InsertChildScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.InsertFatherScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.InsertSiblingDownScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.InsertSiblingUpScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.MoveDownScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.MoveLeftScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.MoveRightScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.MoveUpScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.RemoveScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.ScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.UpdateScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeWidget;

// TODO Refactor this class to decentralize Action to WidgetActionFactory mappings.
// TODO Analize refactoring so that every widgetAction receives the tree and the action in its constructor.
public class ScopeTreeWidgetActionFactoryImpl implements ScopeTreeWidgetActionFactory {

	private final ScopeTreeWidget tree;

	public ScopeTreeWidgetActionFactoryImpl(final ScopeTreeWidget tree) {
		this.tree = tree;
	}

	/**
	 * @see br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions.ScopeTreeWidgetActionFactory#getEquivalentActionFor(br.com.oncast.ontrack.client.ui.component.scopetree.actions.ScopeAction)
	 */
	@Override
	public ScopeTreeWidgetAction getEquivalentActionFor(final ScopeAction action) throws UnableToCompleteActionException {
		final Class<? extends ScopeAction> clazz = action.getClass();

		if (clazz.equals(RemoveScopeAction.class)) return new RemoveScopeTreeWidgetAction(tree, action);
		if (clazz.equals(MoveDownScopeAction.class)) return new MoveDownScopeTreeWidgetAction(tree, action);
		if (clazz.equals(MoveUpScopeAction.class)) return new MoveUpScopeTreeWidgetAction(tree, action);
		if (clazz.equals(MoveRightScopeAction.class)) return new MoveRightScopeTreeWidgetAction(tree, action);
		if (clazz.equals(MoveLeftScopeAction.class)) return new MoveLeftScopeTreeWidgetAction(tree, action);
		if (clazz.equals(UpdateScopeAction.class)) return new UpdateScopeTreeWidgetAction(tree, action);
		if (clazz.equals(InsertSiblingUpScopeAction.class)) return new InsertSiblingUpScopeTreeWidgetAction(tree, action);
		if (clazz.equals(InsertSiblingDownScopeAction.class)) return new InsertSiblingDownScopeTreeWidgetAction(tree, action);
		if (clazz.equals(InsertChildScopeAction.class)) return new InsertChildScopeTreeWidgetAction(tree, action);
		if (clazz.equals(InsertFatherScopeAction.class)) return new InsertFatherScopeTreeWidgetAction(tree, action);

		throw new UnableToCompleteActionException("It was not possible to find the desired action.");
	}
}
