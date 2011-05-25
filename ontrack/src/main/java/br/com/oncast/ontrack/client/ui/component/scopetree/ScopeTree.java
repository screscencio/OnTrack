package br.com.oncast.ontrack.client.ui.component.scopetree;

import static br.com.oncast.ontrack.client.util.keyboard.BrowserKeyCodes.KEY_F2;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.ScopeTreeActionFactoryImpl;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.ScopeTreeActionManager;
import br.com.oncast.ontrack.client.ui.component.scopetree.events.ScopeTreeWidgetInteractionHandler;
import br.com.oncast.ontrack.client.ui.component.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.scope.actions.ScopeUpdateAction;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class ScopeTree implements IsWidget {

	private final ScopeTreeWidget tree;
	private final ScopeTreeActionManager actionManager;

	public ScopeTree() {
		tree = new ScopeTreeWidget(new ScopeTreeWidgetInteractionHandler() {
			@Override
			public void onKeyUp(final KeyUpEvent event) {

				final ScopeTreeItem selected = tree.getSelected();
				if (selected == null) return;

				ScopeTreeShortcutMappings.interpretKeyboardCommand(event.getNativeKeyCode(), event.isControlKeyDown(), event.isShiftKeyDown(), actionManager,
						selected.getReferencedScope());

				if (event.getNativeKeyCode() == KEY_F2) {
					tree.getSelected().enterEditMode();
					tree.setSelected(null);
				}
			}

			@Override
			public void onItemUpdate(final ScopeTreeItem item, final String newContent) {
				actionManager.execute(new ScopeUpdateAction(item.getReferencedScope(), newContent));
			}
		});
		actionManager = new ScopeTreeActionManager(new ScopeTreeActionFactoryImpl(tree));
	}

	public void setScope(final Scope scope) {
		tree.clear();
		final ScopeTreeItem rootItem = new ScopeTreeItem(scope);

		tree.add(rootItem);
		tree.setSelected(rootItem);
	}

	@Override
	public Widget asWidget() {
		return tree;
	}

	public void setFocus(final boolean focus) {
		tree.setFocus(focus);
	}
}
