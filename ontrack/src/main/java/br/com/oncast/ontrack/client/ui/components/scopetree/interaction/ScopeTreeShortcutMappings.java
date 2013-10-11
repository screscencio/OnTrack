package br.com.oncast.ontrack.client.ui.components.scopetree.interaction;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.services.alerting.ClientAlertingService;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.AddAnnotationInternalAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.AddTagInternalAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.BindReleaseInternalAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.DeclareEffortInternalAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.DeclareImpedimentInternalAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.DeclareProgressInternalAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.DeclareValueInternalAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.InsertChildInternalAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.InsertFatherInternalAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.InsertSiblingDownInternalAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.InsertSiblingUpInternalAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.NodeEditionInternalAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeWidgetInteractionHandler;
import br.com.oncast.ontrack.client.ui.events.ScopeSelectionEvent;
import br.com.oncast.ontrack.client.ui.keyeventhandler.EventProcessor;
import br.com.oncast.ontrack.client.ui.keyeventhandler.Shortcut;
import br.com.oncast.ontrack.client.ui.keyeventhandler.ShortcutMapping;
import br.com.oncast.ontrack.client.ui.keyeventhandler.modifier.AltModifier;
import br.com.oncast.ontrack.client.ui.keyeventhandler.modifier.ControlModifier;
import br.com.oncast.ontrack.client.ui.keyeventhandler.modifier.ShiftModifier;
import br.com.oncast.ontrack.client.ui.keyeventhandlers.ShortcutsSet;
import br.com.oncast.ontrack.client.ui.settings.ViewSettings.ScopeTreeColumn;
import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;
import br.com.oncast.ontrack.shared.model.action.ScopeMoveToAction;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveAction;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.core.client.GWT;

import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_1;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_2;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_3;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_4;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_5;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_DELETE;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_DOWN;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_E;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_ENTER;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_F2;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_LEFT;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_RIGHT;
import static br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes.KEY_UP;

public enum ScopeTreeShortcutMappings implements ShortcutMapping<ScopeTreeWidgetInteractionHandler> {
	UPDATE(new Shortcut(KEY_F2), new Shortcut(KEY_E).with(ShiftModifier.BOTH)) {
		@Override
		public void customExecution(final ScopeTreeWidgetInteractionHandler interactionHandler, final Scope scope) {
			interactionHandler.onInternalAction(new NodeEditionInternalAction(scope));
		}

		@Override
		public String getDescription() {
			return messages.updateScope();
		}

	},

	INSERT_SIBLING_SCOPE_DOWN(new Shortcut(KEY_ENTER)) {

		@Override
		protected void customExecution(final ScopeTreeWidgetInteractionHandler interactionHandler, final Scope scope) {
			interactionHandler.onInternalAction(new InsertSiblingDownInternalAction(scope));
		}

		@Override
		public String getDescription() {
			return messages.insertSiblingDown();
		}

	},

	INSERT_SIBLING_SCOPE_UP(new Shortcut(KEY_ENTER).with(ShiftModifier.PRESSED)) {
		@Override
		protected void customExecution(final ScopeTreeWidgetInteractionHandler interactionHandler, final Scope scope) {
			interactionHandler.onInternalAction(new InsertSiblingUpInternalAction(scope));
		}

		@Override
		public String getDescription() {
			return messages.insertSiblingUp();
		}

	},

	INSERT_SCOPE_AS_CHILD(new Shortcut(KEY_ENTER).with(ControlModifier.PRESSED)) {
		@Override
		protected void customExecution(final ScopeTreeWidgetInteractionHandler interactionHandler, final Scope scope) {
			interactionHandler.onInternalAction(new InsertChildInternalAction(scope));
		}

		@Override
		public String getDescription() {
			return messages.insertChild();
		}

	},

	INSERT_SCOPE_AS_PARENT(new Shortcut(KEY_ENTER).with(ControlModifier.PRESSED).with(ShiftModifier.PRESSED)) {
		@Override
		protected void customExecution(final ScopeTreeWidgetInteractionHandler interactionHandler, final Scope scope) {
			interactionHandler.onInternalAction(new InsertFatherInternalAction(scope));
		}

		@Override
		public String getDescription() {
			return messages.insertParent();
		}

	},

	MOVE_SCOPE_UP(new Shortcut(KEY_UP).with(ControlModifier.PRESSED)) {
		@Override
		protected void customExecution(final ScopeTreeWidgetInteractionHandler interactionHandler, final Scope scope) {
			if (scope.isRoot()) return;

			final Scope target = interactionHandler.getVisibleScopeAbove(scope);

			final Scope parent = scope.getParent();
			final int targetIndex = target == null ? parent.getChildCount() : parent.getChildIndex(target);
			interactionHandler.onUserActionExecutionRequest(new ScopeMoveToAction(scope.getId(), parent.getId(), targetIndex));
		}

		@Override
		public String getDescription() {
			return messages.moveUp();
		}

	},

	MOVE_SCOPE_DOWN(new Shortcut(KEY_DOWN).with(ControlModifier.PRESSED)) {
		@Override
		protected void customExecution(final ScopeTreeWidgetInteractionHandler interactionHandler, final Scope scope) {
			if (scope.isRoot()) return;

			final Scope target = interactionHandler.getVisibleScopeBelow(scope);

			final Scope parent = scope.getParent();
			final int targetIndex = target == null ? 0 : parent.getChildIndex(target) + 1;
			interactionHandler.onUserActionExecutionRequest(new ScopeMoveToAction(scope.getId(), parent.getId(), targetIndex));
		}

		@Override
		public String getDescription() {
			return messages.moveScopeDown();
		}

	},
	MOVE_SCOPE_RIGHT(new Shortcut(KEY_RIGHT).with(ControlModifier.PRESSED)) {
		@Override
		protected void customExecution(final ScopeTreeWidgetInteractionHandler interactionHandler, final Scope scope) {
			if (scope.isRoot()) return;

			final Scope target = interactionHandler.getVisibleScopeAbove(scope);
			if (target == null) return;

			if (target.getRelease() != null && scope.getRelease() != null) {
				ClientServices.get().alerting().showWarning(messages.cantMoveBecauseCantCascadeScopesWithReleases(), ClientAlertingService.DURATION_LONG);
				return;
			}

			interactionHandler.onUserActionExecutionRequest(new ScopeMoveToAction(scope.getId(), target.getId(), target.getChildCount()));
		}

		@Override
		public String getDescription() {
			return messages.moveRight();
		}

	},

	MOVE_SCOPE_LEFT(new Shortcut(KEY_LEFT).with(ControlModifier.PRESSED)) {
		@Override
		protected void customExecution(final ScopeTreeWidgetInteractionHandler interactionHandler, final Scope scope) {
			if (scope.isRoot() || scope.getParent().isRoot()) return;

			final Scope parent = scope.getParent();
			final Scope grandParent = parent.getParent();
			interactionHandler.onUserActionExecutionRequest(new ScopeMoveToAction(scope.getId(), grandParent.getId(), grandParent.getChildIndex(parent) + 1));
		}

		@Override
		public String getDescription() {
			return messages.moveLeft();
		}

	},

	DELETE_SCOPE(new Shortcut(KEY_DELETE), new Shortcut(BrowserKeyCodes.KEY_BACKSPACE)) {
		@Override
		protected void customExecution(final ScopeTreeWidgetInteractionHandler interactionHandler, final Scope scope) {
			interactionHandler.onUserActionExecutionRequest(new ScopeRemoveAction(scope.getId()));
		}

		@Override
		public String getDescription() {
			return messages.deleteScope();
		}

	},

	DECLARE_IMPEDIMENT(new Shortcut(KEY_1).with(ShiftModifier.BOTH)) {
		@Override
		protected void customExecution(final ScopeTreeWidgetInteractionHandler interactionHandler, final Scope scope) {
			interactionHandler.onInternalAction(new DeclareImpedimentInternalAction(scope));
		}

		@Override
		public String getDescription() {
			return messages.declareImpediment();
		}

	},

	BIND_RELEASE(new Shortcut(KEY_2).with(ShiftModifier.BOTH)) {
		@Override
		protected void customExecution(final ScopeTreeWidgetInteractionHandler interactionHandler, final Scope scope) {
			interactionHandler.onInternalAction(new BindReleaseInternalAction(scope, interactionHandler.getProjectContext()));
		}

		@Override
		public String getDescription() {
			return messages.bindRelease();
		}

	},

	BIND_EFFORT(new Shortcut(KEY_3).with(ShiftModifier.BOTH)) {
		@Override
		protected void customExecution(final ScopeTreeWidgetInteractionHandler interactionHandler, final Scope scope) {
			interactionHandler.onInternalAction(new DeclareEffortInternalAction(scope, interactionHandler.getProjectContext()));
		}

		@Override
		public String getDescription() {
			return messages.declareEffort();
		}

	},

	BIND_VALUE(new Shortcut(KEY_4).with(ShiftModifier.BOTH)) {
		@Override
		protected void customExecution(final ScopeTreeWidgetInteractionHandler interactionHandler, final Scope scope) {
			interactionHandler.onInternalAction(new DeclareValueInternalAction(scope, interactionHandler.getProjectContext()));
		}

		@Override
		public String getDescription() {
			return messages.declareValue();
		}

	},

	BIND_PROGRESS(new Shortcut(KEY_5).with(ShiftModifier.BOTH)) {
		@Override
		protected void customExecution(final ScopeTreeWidgetInteractionHandler interactionHandler, final Scope scope) {
			interactionHandler.onInternalAction(new DeclareProgressInternalAction(scope, interactionHandler.getProjectContext()));
		}

		@Override
		public String getDescription() {
			return messages.declareProgress();
		}

	},

	ADD_TAG(new Shortcut(BrowserKeyCodes.KEY_7).with(ShiftModifier.BOTH)) {
		@Override
		protected void customExecution(final ScopeTreeWidgetInteractionHandler interactionHandler, final Scope scope) {
			interactionHandler.onInternalAction(new AddTagInternalAction(scope, interactionHandler.getProjectContext()));
		}

		@Override
		public String getDescription() {
			return messages.addOrRemoveTagToScope();
		}

	},

	ADD_ANNOTATION(new Shortcut(BrowserKeyCodes.KEY_C).with(ShiftModifier.BOTH)) {
		@Override
		protected void customExecution(final ScopeTreeWidgetInteractionHandler interactionHandler, final Scope scope) {
			interactionHandler.onInternalAction(new AddAnnotationInternalAction(scope, interactionHandler.getProjectContext()));
		}

		@Override
		public String getDescription() {
			return messages.quickAddAnnotationToScope();
		}

	},

	OPEN_DETAILS(new Shortcut(BrowserKeyCodes.KEY_A).with(ShiftModifier.BOTH)) {
		@Override
		protected void customExecution(final ScopeTreeWidgetInteractionHandler interactionHandler, final Scope scope) {
			ClientServices.get().details().showDetailsFor(scope.getId());
		}

		@Override
		public String getDescription() {
			return messages.openDetailsPanel();
		}

	},

	TOGGLE_VALUE_COLUMN(new Shortcut(BrowserKeyCodes.KEY_4).with(AltModifier.PRESSED).with(ShiftModifier.BOTH)) {
		@Override
		protected void customExecution(final ScopeTreeWidgetInteractionHandler interactionHandler, final Scope scope) {
			ScopeTreeColumn.VALUE.toggle();
		}

		@Override
		public String getDescription() {
			return messages.toggleValueColumn();
		}

	},

	TOGGLE_EFFORT_COLUMN(new Shortcut(BrowserKeyCodes.KEY_3).with(AltModifier.PRESSED).with(ShiftModifier.BOTH)) {
		@Override
		protected void customExecution(final ScopeTreeWidgetInteractionHandler interactionHandler, final Scope scope) {
			ScopeTreeColumn.EFFORT.toggle();
		}

		@Override
		public String getDescription() {
			return messages.toggleEffortColumn();
		}

	},

	TOGGLE_RELEASE_COLUMN(new Shortcut(BrowserKeyCodes.KEY_2).with(AltModifier.PRESSED).with(ShiftModifier.BOTH)) {
		@Override
		protected void customExecution(final ScopeTreeWidgetInteractionHandler interactionHandler, final Scope scope) {
			ScopeTreeColumn.RELEASE.toggle();
		}

		@Override
		public String getDescription() {
			return messages.toggleReleaseColumn();
		}

	},

	TOGGLE_PROGRESS_COLUMN(new Shortcut(BrowserKeyCodes.KEY_5).with(AltModifier.PRESSED).with(ShiftModifier.BOTH)) {
		@Override
		protected void customExecution(final ScopeTreeWidgetInteractionHandler interactionHandler, final Scope scope) {
			ScopeTreeColumn.PROGRESS.toggle();
		}

		@Override
		public String getDescription() {
			return messages.toggleProgressColumn();
		}

	},

	FIND_SCOPE_AT_RELEASE_WIDGET(new Shortcut(BrowserKeyCodes.KEY_SPACE), new Shortcut(BrowserKeyCodes.KEY_F).with(AltModifier.PRESSED)) {
		@Override
		protected void customExecution(final ScopeTreeWidgetInteractionHandler interactionHandler, final Scope scope) {
			ClientServices.get().eventBus().fireEventFromSource(new ScopeSelectionEvent(scope, true), this);
		}

		@Override
		public String getDescription() {
			return messages.findScopeAtReleaseWidget();
		}

	},

	SELECT_PREVIOUS_SELECTED_SCOPE(new Shortcut(BrowserKeyCodes.KEY_LEFT).with(AltModifier.PRESSED)) {
		@Override
		protected void customExecution(final ScopeTreeWidgetInteractionHandler interactionHandler, final Scope scope) {
			ClientServices.get().applicationState().jumpToPreviousSelection();
		}

		@Override
		public String getDescription() {
			return messages.selectPreviousSelectedScope();
		}

	},

	SELECT_NEXT_SELECTED_SCOPE(new Shortcut(BrowserKeyCodes.KEY_RIGHT).with(AltModifier.PRESSED)) {
		@Override
		protected void customExecution(final ScopeTreeWidgetInteractionHandler interactionHandler, final Scope scope) {
			ClientServices.get().applicationState().jumpToNextSelection();
		}

		@Override
		public String getDescription() {
			return messages.selectNextSelectedScope();
		}

	};

	private final ShortcutsSet shortcuts;
	private static final ScopeTreeShortcutMappingsMessage messages = GWT.create(ScopeTreeShortcutMappingsMessage.class);

	ScopeTreeShortcutMappings(final Shortcut... shortcuts) {
		this.shortcuts = new ShortcutsSet(shortcuts);
	}

	@Override
	public void execute(final ScopeTreeWidgetInteractionHandler interactionHandler) {
		interactionHandler.assureConfigured();
		final Scope selectedScope = interactionHandler.getSelectedScope();
		if (selectedScope == null) return;
		customExecution(interactionHandler, selectedScope);
	}

	protected abstract void customExecution(ScopeTreeWidgetInteractionHandler interactionHandler, Scope scope);

	@Override
	public ShortcutsSet getShortcuts() {
		return this.shortcuts;
	}

	@Override
	public EventProcessor getEventPostExecutionProcessor() {
		return EventProcessor.CONSUME;
	}

}
