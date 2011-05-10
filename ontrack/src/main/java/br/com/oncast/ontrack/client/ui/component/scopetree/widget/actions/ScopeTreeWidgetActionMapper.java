package br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions;

import java.util.HashMap;
import java.util.Map;

import br.com.oncast.ontrack.client.ui.component.scopetree.actions.InsertSiblingDownScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.InsertSiblingUpScopeAction;
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

// TODO Change RuntimeException to something more appropriated.
// TODO Refactor this class to decentralize Action to WidgetActionFactory mappings.
// TODO Analize a refactoring to change tree.getSelected() to something that maps a Scope to its ScopeTreeItem.
public class ScopeTreeWidgetActionMapper {

	private final static Map<Class<? extends ScopeAction>, ScopeTreeWidgetActionFactory> actionFactoryMap = new HashMap<Class<? extends ScopeAction>, ScopeTreeWidgetActionFactory>();

	static {
		actionFactoryMap.put(RemoveScopeAction.class, new ScopeTreeWidgetActionFactory() {
			@Override
			public ScopeTreeWidgetAction create(final ScopeTreeItem referencedScopeTreeItem, final Scope referencedScope) {
				return new RemoveScopeTreeWidgetAction(referencedScopeTreeItem);
			}
		});

		actionFactoryMap.put(MoveDownScopeAction.class, new ScopeTreeWidgetActionFactory() {
			@Override
			public ScopeTreeWidgetAction create(final ScopeTreeItem referencedScopeTreeItem, final Scope referencedScope) {
				return new MoveDownScopeTreeWidgetAction(referencedScopeTreeItem);
			}
		});

		actionFactoryMap.put(MoveUpScopeAction.class, new ScopeTreeWidgetActionFactory() {
			@Override
			public ScopeTreeWidgetAction create(final ScopeTreeItem referencedScopeTreeItem, final Scope referencedScope) {
				return new MoveUpScopeTreeWidgetAction(referencedScopeTreeItem);
			}
		});

		actionFactoryMap.put(MoveRightScopeAction.class, new ScopeTreeWidgetActionFactory() {
			@Override
			public ScopeTreeWidgetAction create(final ScopeTreeItem referencedScopeTreeItem, final Scope referencedScope) {
				return new MoveRightScopeTreeWidgetAction(referencedScopeTreeItem);
			}
		});

		actionFactoryMap.put(MoveLeftScopeAction.class, new ScopeTreeWidgetActionFactory() {
			@Override
			public ScopeTreeWidgetAction create(final ScopeTreeItem referencedScopeTreeItem, final Scope referencedScope) {
				return new MoveLeftScopeTreeWidgetAction(referencedScopeTreeItem);
			}
		});

		actionFactoryMap.put(InsertSiblingUpScopeAction.class, new ScopeTreeWidgetActionFactory() {
			@Override
			public ScopeTreeWidgetAction create(final ScopeTreeItem referencedScopeTreeItem, final Scope referencedScope) {
				return new InsertSiblingUpScopeTreeWidgetAction(referencedScopeTreeItem);
			}
		});

		actionFactoryMap.put(InsertSiblingDownScopeAction.class, new ScopeTreeWidgetActionFactory() {
			@Override
			public ScopeTreeWidgetAction create(final ScopeTreeItem referencedScopeTreeItem, final Scope referencedScope) {
				return new InsertSiblingDownScopeTreeWidgetAction(referencedScopeTreeItem);
			}
		});

		actionFactoryMap.put(UpdateScopeAction.class, new ScopeTreeWidgetActionFactory() {
			@Override
			public ScopeTreeWidgetAction create(final ScopeTreeItem referencedScopeTreeItem, final Scope referencedScope) {
				return new UpdateScopeTreeWidgetAction(referencedScopeTreeItem, referencedScope);
			}
		});
	}

	public static ScopeTreeWidgetAction getEquivalentActionFor(final ScopeTreeWidget tree, final ScopeAction action) throws UnableToCompleteActionException {
		final Class<? extends ScopeAction> clazz = action.getClass();
		if (!actionFactoryMap.containsKey(clazz)) throw new UnableToCompleteActionException("This action is not supported.");

		try {
			final Scope referencedScope = action.getScope();
			return actionFactoryMap.get(clazz).create(tree.getScopeTreeItemFor(referencedScope), referencedScope);
		} catch (final NotFoundException e) {
			throw new UnableToCompleteActionException("It was not possible to find the tree item in which the action should be performed on.");
		}
	}
}
