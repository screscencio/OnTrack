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

import java.util.EmptyStackException;
import java.util.Stack;

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
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeItem;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.ScopeTreeWidget;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.actions.ScopeTreeWidgetActionFactory;
import br.com.oncast.ontrack.client.ui.component.scopetree.widget.event.ScopeTreeWidgetInteractionHandler;
import br.com.oncast.ontrack.shared.beans.Scope;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class ScopeTree implements IsWidget {

	private final ScopeTreeWidget tree;
	private final Stack<ScopeAction> undoStack;
	private final Stack<ScopeAction> redoStack;

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
						execute(new MoveUpScopeAction(selected.getReferencedScope()));
						break;
					case KEY_DOWN:
						execute(new MoveDownScopeAction(selected.getReferencedScope()));
						break;
					case KEY_RIGHT:
						execute(new MoveRightScopeAction(selected.getReferencedScope()));
						break;
					case KEY_LEFT:
						execute(new MoveLeftScopeAction(selected.getReferencedScope()));
						break;
					case KEY_Z:
						undo();
						break;
					case KEY_Y:
						redo();
						break;
					}
				} else {
					if (event.getNativeKeyCode() == KEY_DELETE) execute(new RemoveScopeAction(selected.getReferencedScope()));
					else if (event.getNativeKeyCode() == KEY_ENTER) {
						if (event.isShiftKeyDown()) {
							execute(new InsertSiblingUpScopeAction(selected.getReferencedScope()));
						} else {
							execute(new InsertSiblingDownScopeAction(selected.getReferencedScope()));
						}
					} else if (event.getNativeKeyCode() == KEY_INSERT && !event.isShiftKeyDown()) {
						execute(new InsertChildScopeAction(selected.getReferencedScope()));
					} else if (event.getNativeKeyCode() == KEY_INSERT && event.isShiftKeyDown()) {
						execute(new InsertFatherScopeAction(selected.getReferencedScope()));
					} else if (event.getNativeKeyCode() == KEY_F2 || event.getNativeKeyCode() == 69) {
						tree.getSelected().enterEditMode();
					}
				}
			}

			@Override
			public void onItemUpdate(final ScopeTreeItem item, final String newContent) {
				execute(new UpdateScopeAction(item.getReferencedScope(), newContent));
			}
		});
		undoStack = new Stack<ScopeAction>();
		redoStack = new Stack<ScopeAction>();
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
				undoStack.push(action);
				redoStack.clear();
			} catch (final UnableToCompleteActionException e) {
				action.rollback();
				throw e;
			}
		} catch (final UnableToCompleteActionException e) {
			// TODO Implement an adequate exception treatment.
			// TODO Display error to the user
			// TODO Maybe create a type of exception when we don't want to display any messages.
			throw new RuntimeException(e);
		}
	}

	protected void undo() {
		try {
			final ScopeAction action = undoStack.pop();
			action.rollback();
			try {
				ScopeTreeWidgetActionFactory.getEquivalentActionFor(tree, action).rollback();
				redoStack.push(action);
			} catch (final UnableToCompleteActionException e) {
				action.execute();
				throw e;
			}
		} catch (final UnableToCompleteActionException e) {
			undoStack.clear();
			// TODO Implement an adequate exception treatment.
			// TODO Display error to the user
			// TODO Maybe create a type of exception when we don't want to display any messages.
			throw new RuntimeException(e);
		} catch (final EmptyStackException e) {
			// Purposefully ignoring exception
		}
	}

	protected void redo() {
		try {
			final ScopeAction action = redoStack.pop();
			action.execute();
			try {
				ScopeTreeWidgetActionFactory.getEquivalentActionFor(tree, action).execute();
				undoStack.push(action);
			} catch (final UnableToCompleteActionException e) {
				action.rollback();
				throw e;
			}
		} catch (final UnableToCompleteActionException e) {
			redoStack.clear();
			// TODO Implement an adequate exception treatment.
			// TODO Display error to the user
			// TODO Maybe create a type of exception when we don't want to display any messages.
			throw new RuntimeException(e);
		} catch (final EmptyStackException e) {
			// Purposefully ignoring exception
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
