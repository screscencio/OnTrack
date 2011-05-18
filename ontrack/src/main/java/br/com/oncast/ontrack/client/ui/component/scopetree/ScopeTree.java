package br.com.oncast.ontrack.client.ui.component.scopetree;

import static br.com.oncast.ontrack.client.util.keyboard.BrowserKeyCodes.KEY_DELETE;
import static br.com.oncast.ontrack.client.util.keyboard.BrowserKeyCodes.KEY_DOWN;
import static br.com.oncast.ontrack.client.util.keyboard.BrowserKeyCodes.KEY_ENTER;
import static br.com.oncast.ontrack.client.util.keyboard.BrowserKeyCodes.KEY_F2;
import static br.com.oncast.ontrack.client.util.keyboard.BrowserKeyCodes.KEY_INSERT;
import static br.com.oncast.ontrack.client.util.keyboard.BrowserKeyCodes.KEY_LEFT;
import static br.com.oncast.ontrack.client.util.keyboard.BrowserKeyCodes.KEY_RIGHT;
import static br.com.oncast.ontrack.client.util.keyboard.BrowserKeyCodes.KEY_UP;
import static br.com.oncast.ontrack.client.util.keyboard.BrowserKeyCodes.KEY_Y;
import static br.com.oncast.ontrack.client.util.keyboard.BrowserKeyCodes.KEY_Z;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.InsertChildScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.InsertFatherScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.InsertSiblingDownScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.InsertSiblingUpScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.MoveDownScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.MoveLeftScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.MoveRightScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.MoveUpScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.RemoveScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.UpdateScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeWidget;
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

				// TODO Refactor this code to organize mappings from inputs to actions
				if (event.isControlKeyDown()) {
					switch (event.getNativeKeyCode()) {
					case KEY_UP:
						actionManager.execute(new MoveUpScopeAction(selected.getReferencedScope()));
						break;
					case KEY_DOWN:
						actionManager.execute(new MoveDownScopeAction(selected.getReferencedScope()));
						break;
					case KEY_RIGHT:
						actionManager.execute(new MoveRightScopeAction(selected.getReferencedScope()));
						break;
					case KEY_LEFT:
						actionManager.execute(new MoveLeftScopeAction(selected.getReferencedScope()));
						break;
					case KEY_Z:
						actionManager.undo();
						break;
					case KEY_Y:
						actionManager.redo();
						break;
					}
				} else {
					if (event.getNativeKeyCode() == KEY_DELETE) actionManager.execute(new RemoveScopeAction(selected.getReferencedScope()));
					else if (event.getNativeKeyCode() == KEY_ENTER) {
						if (event.isShiftKeyDown()) {
							actionManager.execute(new InsertSiblingUpScopeAction(selected.getReferencedScope()));
						} else {
							actionManager.execute(new InsertSiblingDownScopeAction(selected.getReferencedScope()));
						}
					} else if (event.getNativeKeyCode() == KEY_INSERT && !event.isShiftKeyDown()) {
						actionManager.execute(new InsertChildScopeAction(selected.getReferencedScope()));
					} else if (event.getNativeKeyCode() == KEY_INSERT && event.isShiftKeyDown()) {
						actionManager.execute(new InsertFatherScopeAction(selected.getReferencedScope()));
					} else if (event.getNativeKeyCode() == KEY_F2 || event.getNativeKeyCode() == 69) {
						tree.getSelected().enterEditMode();
					}
				}
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
