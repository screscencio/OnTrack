package br.com.oncast.ontrack.client.ui.component.scopetree;

import static br.com.oncast.ontrack.client.util.keyboard.BrowserKeyCodes.KEY_F2;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.UpdateScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeWidget;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions.ScopeTreeWidgetActionFactory;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions.ScopeTreeWidgetActionManager;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions.ScopeTreeWidgetActionFactoryImpl;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.event.ScopeTreeWidgetInteractionHandler;
import br.com.oncast.ontrack.shared.beans.Scope;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class ScopeTree implements IsWidget {

	private final ScopeTreeWidget tree;
	private final ScopeTreeWidgetActionManager actionManager;

	public ScopeTree() {
		tree = new ScopeTreeWidget(new ScopeTreeWidgetInteractionHandler() {
			@Override
			public void onKeyUp(final KeyUpEvent event) {

				final ScopeTreeItem selected = tree.getSelected();
				if (selected == null) return;

				ScopeTreeShortcutMappings.interpretKeyboardCommand(event.getNativeKeyCode(), event.isControlKeyDown(), event.isShiftKeyDown(), actionManager,
						selected.getReferencedScope());

				if (event.getNativeKeyCode() == KEY_F2) tree.getSelected().enterEditMode();
			}

			@Override
			public void onItemUpdate(final ScopeTreeItem item, final String newContent) {
				actionManager.execute(new UpdateScopeAction(item.getReferencedScope(), newContent));
			}
		});
		actionManager = new ScopeTreeWidgetActionManager(new ScopeTreeWidgetActionFactoryImpl(tree));
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
