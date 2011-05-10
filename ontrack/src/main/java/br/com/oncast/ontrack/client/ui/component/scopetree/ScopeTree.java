package br.com.oncast.ontrack.client.ui.component.scopetree;

import br.com.oncast.ontrack.client.ui.component.scopetree.actions.InsertSiblingDownScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.InsertSiblingUpScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.MoveDownScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.MoveLeftScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.MoveRightScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.MoveUpScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.RemoveScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.ScopeAction;
import br.com.oncast.ontrack.client.ui.component.scopetree.actions.UnableToCompleteActionException;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeWidget;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeWidgetInteractionHandler;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions.ScopeTreeWidgetActionFactory;
import br.com.oncast.ontrack.shared.beans.Scope;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class ScopeTree implements IsWidget {

	private final ScopeTreeWidget tree;

	public ScopeTree() {
		tree = new ScopeTreeWidget(new ScopeTreeWidgetInteractionHandler() {
			@Override
			public void onKeyUp(final KeyUpEvent event) {

				final TreeItem selected = tree.getSelected();
				if (selected == null) return;

				if (event.isControlKeyDown()) {
					switch (event.getNativeKeyCode()) {
					case KeyCodes.KEY_UP:
						execute(new MoveUpScopeAction((Scope) selected.getUserObject()));
						break;
					case KeyCodes.KEY_DOWN:
						execute(new MoveDownScopeAction((Scope) selected.getUserObject()));
						break;
					case KeyCodes.KEY_RIGHT:
						execute(new MoveRightScopeAction((Scope) selected.getUserObject()));
						break;
					case KeyCodes.KEY_LEFT:
						execute(new MoveLeftScopeAction((Scope) selected.getUserObject()));
						break;
					default:
						break;
					}
				} else if (event.getNativeKeyCode() == KeyCodes.KEY_DELETE) {
					execute(new RemoveScopeAction((Scope) selected.getUserObject()));
				} else if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					if (event.isShiftKeyDown()) execute(new InsertSiblingUpScopeAction((Scope) selected.getUserObject()));
					else execute(new InsertSiblingDownScopeAction((Scope) selected.getUserObject()));
				}
			}
		});
	}

	public void setScope(final Scope scope) {
		tree.clear();
		final ScopeTreeItem rootItem = new ScopeTreeItem(scope);

		tree.add(rootItem);
		tree.setSelected(rootItem);
	}

	protected void execute(final ScopeAction action) {
		try {
			action.execute();
			try {
				ScopeTreeWidgetActionFactory.getEquivalentActionFor(tree, action).execute();
				// TODO Push ScopeAction into "Undo Stack"
				// TODO Push ScopeAction into "Server Changes Stack"
			} catch (final UnableToCompleteActionException e) {
				// TODO Rollback ScopeAction.
				throw e;
			}
		} catch (final UnableToCompleteActionException e) {
			// TODO Implement an adequate exception treatment.
			// TODO Show error message.
			throw new RuntimeException(e);
		} catch (final Exception e) {
			// TODO Implement an adequate exception treatment.
			// Maybe create a type of exception when we don't want to display any messages.
			throw new RuntimeException(e);
		}
	}

	@Override
	public Widget asWidget() {
		return tree;
	}

	public void setFocus(final boolean focus) {
		tree.setFocus(focus);
	}
}
