package br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions;

import br.com.oncast.ontrack.client.ui.component.scopetree.actions.InsertFatherScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.InsertSiblingDownScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.InsertSiblingUpScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.InsertionScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.MoveDownScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.MoveLeftScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.MoveRightScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.MoveUpScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.RemoveScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.ScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.UpdateScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.NotFoundException;
import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.beans.Scope;

// TODO Refactor this class to decentralize Action to WidgetActionFactory mappings.
public class ScopeTreeWidgetActionFactory {

	public static ScopeTreeWidgetAction getEquivalentActionFor(final ScopeTreeWidget tree, final ScopeAction action) throws UnableToCompleteActionException {
		final Class<? extends ScopeAction> clazz = action.getClass();

		try {
			final Scope referencedScope = action.getScope();
			final ScopeTreeItem referencedScopeTreeItem = tree.getScopeTreeItemFor(referencedScope);

			if (clazz.equals(RemoveScopeAction.class)) return new RemoveScopeTreeWidgetAction(referencedScopeTreeItem);
			if (clazz.equals(MoveDownScopeAction.class)) return new MoveDownScopeTreeWidgetAction(referencedScopeTreeItem);
			if (clazz.equals(MoveUpScopeAction.class)) return new MoveUpScopeTreeWidgetAction(referencedScopeTreeItem);
			if (clazz.equals(MoveRightScopeAction.class)) return new MoveRightScopeTreeWidgetAction(referencedScopeTreeItem);
			if (clazz.equals(MoveLeftScopeAction.class)) return new MoveLeftScopeTreeWidgetAction(referencedScopeTreeItem);
			if (clazz.equals(UpdateScopeAction.class)) return new UpdateScopeTreeWidgetAction(referencedScopeTreeItem, referencedScope);
			if (clazz.equals(InsertSiblingUpScopeAction.class)) return new InsertSiblingUpScopeTreeWidgetAction(referencedScopeTreeItem,
					((InsertionScopeAction) action).getNewScope());
			if (clazz.equals(InsertSiblingDownScopeAction.class)) return new InsertSiblingDownScopeTreeWidgetAction(referencedScopeTreeItem,
					((InsertionScopeAction) action).getNewScope());
			if (clazz.equals(InsertFatherScopeAction.class)) return new InsertFatherScopeTreeWidgetAction(referencedScopeTreeItem,
					((InsertionScopeAction) action).getNewScope());

			throw new UnableToCompleteActionException("It was not possible to find the desired action.");

		} catch (final NotFoundException e) {
			throw new UnableToCompleteActionException("It was not possible to find the tree item in which the action should be performed on.");
		}
	}
}
