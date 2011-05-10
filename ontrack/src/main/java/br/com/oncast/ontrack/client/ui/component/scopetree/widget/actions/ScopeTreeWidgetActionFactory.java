package br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions;

import java.util.HashMap;
import java.util.Map;

import br.com.oncast.ontrack.client.ui.component.scopetree.actions.MoveDownScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.MoveRightScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.MoveUpScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.RemoveScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.ScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.UnableToCompleteActionException;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeWidget;

// TODO Change RuntimeException to something more appropriated.
// TODO Refactor this class to decentralize Action to WidgetActionFactory mappings.
public class ScopeTreeWidgetActionFactory {

	private final static Map<Class<? extends ScopeAction>, ActionFactory> actionFactoryMap = new HashMap<Class<? extends ScopeAction>, ActionFactory>();

	static {
		actionFactoryMap.put(RemoveScopeAction.class, new ActionFactory() {
			@Override
			public ScopeTreeWidgetAction create(final ScopeTreeWidget tree, final ScopeAction action) {
				return new RemoveScopeTreeWidgetAction(tree.getSelected());
			}
		});

		actionFactoryMap.put(MoveDownScopeAction.class, new ActionFactory() {
			@Override
			public ScopeTreeWidgetAction create(final ScopeTreeWidget tree, final ScopeAction action) {
				return new MoveDownScopeTreeWidgetAction(tree.getSelected());
			}
		});

		actionFactoryMap.put(MoveUpScopeAction.class, new ActionFactory() {
			@Override
			public ScopeTreeWidgetAction create(final ScopeTreeWidget tree, final ScopeAction action) {
				return new MoveUpScopeTreeWidgetAction(tree.getSelected());
			}
		});

		actionFactoryMap.put(MoveRightScopeAction.class, new ActionFactory() {
			@Override
			public ScopeTreeWidgetAction create(final ScopeTreeWidget tree, final ScopeAction action) {
				return new MoveRightScopeTreeWidgetAction(tree.getSelected());
			}
		});
	}

	public static ScopeTreeWidgetAction getEquivalentActionFor(final ScopeTreeWidget tree, final ScopeAction action) throws UnableToCompleteActionException {
		final Class<? extends ScopeAction> clazz = action.getClass();
		if (!actionFactoryMap.containsKey(clazz)) throw new UnableToCompleteActionException("This action is not supported.");

		return actionFactoryMap.get(clazz).create(tree, action);
	}
}
