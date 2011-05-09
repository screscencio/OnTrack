package br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions;

import java.util.Map;

import br.com.oncast.ontrack.client.ui.component.scopetree.actions.RemoveScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.ScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeWidget;

import com.google.gwt.dev.util.collect.HashMap;

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
	}

	public static ScopeTreeWidgetAction getEquivalentActionFor(final ScopeTreeWidget tree, final ScopeAction action) {
		final Class<? extends ScopeAction> clazz = action.getClass();
		if (!actionFactoryMap.containsKey(clazz)) throw new RuntimeException();

		return actionFactoryMap.get(clazz).create(tree, action);
	}
}
