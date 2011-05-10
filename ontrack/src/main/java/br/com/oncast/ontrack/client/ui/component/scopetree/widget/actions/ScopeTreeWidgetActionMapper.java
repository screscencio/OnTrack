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
import br.com.oncast.ontrack.client.ui.component.scopetree.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeWidget;

// TODO Change RuntimeException to something more appropriated.
// TODO Refactor this class to decentralize Action to WidgetActionFactory mappings.
// TODO Analize a refactoring to change tree.getSelected() to something that maps a Scope to its ScopeTreeItem.
public class ScopeTreeWidgetActionMapper {

	private final static Map<Class<? extends ScopeAction>, ScopeTreeWidgetActionFactory> actionFactoryMap = new HashMap<Class<? extends ScopeAction>, ScopeTreeWidgetActionFactory>();

	static {
		actionFactoryMap.put(RemoveScopeAction.class, new ScopeTreeWidgetActionFactory() {
			@Override
			public ScopeTreeWidgetAction create(final ScopeTreeWidget tree, final ScopeAction action) {
				return new RemoveScopeTreeWidgetAction(tree.getSelected());
			}
		});

		actionFactoryMap.put(MoveDownScopeAction.class, new ScopeTreeWidgetActionFactory() {
			@Override
			public ScopeTreeWidgetAction create(final ScopeTreeWidget tree, final ScopeAction action) {
				return new MoveDownScopeTreeWidgetAction(tree.getSelected());
			}
		});

		actionFactoryMap.put(MoveUpScopeAction.class, new ScopeTreeWidgetActionFactory() {
			@Override
			public ScopeTreeWidgetAction create(final ScopeTreeWidget tree, final ScopeAction action) {
				return new MoveUpScopeTreeWidgetAction(tree.getSelected());
			}
		});

		actionFactoryMap.put(MoveRightScopeAction.class, new ScopeTreeWidgetActionFactory() {
			@Override
			public ScopeTreeWidgetAction create(final ScopeTreeWidget tree, final ScopeAction action) {
				return new MoveRightScopeTreeWidgetAction(tree.getSelected());
			}
		});

		actionFactoryMap.put(MoveLeftScopeAction.class, new ScopeTreeWidgetActionFactory() {
			@Override
			public ScopeTreeWidgetAction create(final ScopeTreeWidget tree, final ScopeAction action) {
				return new MoveLeftScopeTreeWidgetAction(tree.getSelected());
			}
		});

		actionFactoryMap.put(InsertSiblingUpScopeAction.class, new ScopeTreeWidgetActionFactory() {
			@Override
			public ScopeTreeWidgetAction create(final ScopeTreeWidget tree, final ScopeAction action) {
				return new InsertSiblingUpScopeTreeWidgetAction(tree.getSelected());
			}
		});

		actionFactoryMap.put(InsertSiblingDownScopeAction.class, new ScopeTreeWidgetActionFactory() {
			@Override
			public ScopeTreeWidgetAction create(final ScopeTreeWidget tree, final ScopeAction action) {
				return new InsertSiblingDownScopeTreeWidgetAction(tree.getSelected());
			}
		});

		actionFactoryMap.put(UpdateScopeAction.class, new ScopeTreeWidgetActionFactory() {
			@Override
			public ScopeTreeWidgetAction create(final ScopeTreeWidget tree, final ScopeAction action) {
				return new UpdateScopeTreeWidgetAction(tree.getSelected(), action.getScope());
			}
		});
	}

	public static ScopeTreeWidgetAction getEquivalentActionFor(final ScopeTreeWidget tree, final ScopeAction action) throws UnableToCompleteActionException {
		final Class<? extends ScopeAction> clazz = action.getClass();
		if (!actionFactoryMap.containsKey(clazz)) throw new UnableToCompleteActionException("This action is not supported.");

		return actionFactoryMap.get(clazz).create(tree, action);
	}
}
