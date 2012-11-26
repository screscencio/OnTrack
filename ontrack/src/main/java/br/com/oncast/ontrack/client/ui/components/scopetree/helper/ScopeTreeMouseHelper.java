package br.com.oncast.ontrack.client.ui.components.scopetree.helper;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.globalEvent.GlobalNativeEventService;
import br.com.oncast.ontrack.client.services.globalEvent.NativeEventListener;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.OneStepInternalAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.internal.TwoStepInternalAction;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeTreeInternalActionHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeItemWidget;
import br.com.oncast.ontrack.client.ui.events.ScopeTreeItemSelectionEvent;
import br.com.oncast.ontrack.client.ui.events.ScopeTreeItemSelectionEventHandler;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootPanel;

public class ScopeTreeMouseHelper {

	private static final int MOUSE_MOVEMENT_DELAY = 5000;
	private boolean mouseHasMoved;
	private final Timer mouseMoveTimer = new Timer() {

		@Override
		public void run() {
			mouseHasMoved = false;
			updateFloatingHelperWidgetVisibility();
		}
	};

	private HandlerRegistration selectionHandlerRegistration;
	private final ScopeTreeItemSelectionEventHandler selectionHandler;
	private final NativeEventListener mouseEventListener;
	private ScopeTreeInternalActionHandler actionHandler;

	private ScopeTreeItemWidget scopeTreeItemWidget;
	private int clientX;
	private int currentClientX;

	private final FloatingActionMenu floatingMenu;
	private ActionExecutionService actionExecutionService;
	private ProjectContext projectContext;

	public ScopeTreeMouseHelper() {
		selectionHandler = new ScopeTreeItemSelectionEventHandler() {

			@Override
			public void onScopeTreeItemSelectionRequest(final ScopeTreeItemSelectionEvent event) {
				scopeTreeItemWidget = event.getScopeWidget();
				if (mouseHasMoved) {
					mouseMoveTimer.cancel();
					mouseMoveTimer.schedule(MOUSE_MOVEMENT_DELAY);
				}
				calculateXCoordinate();
				updateFloatingHelperWidgetVisibility();
			}

			private void calculateXCoordinate() {
				final int i = scopeTreeItemWidget.getOffsetWidth() + scopeTreeItemWidget.getAbsoluteLeft();
				clientX = currentClientX > i ? i - floatingMenu.getOffsetWidth() : currentClientX - (floatingMenu.getOffsetWidth() / 2);
				if (clientX < 30) clientX = 30;
			}
		};
		mouseEventListener = new NativeEventListener() {

			@Override
			public void onNativeEvent(final NativeEvent nativeEvent) {
				currentClientX = nativeEvent.getClientX();
				mouseHasMoved = true;
				mouseMoveTimer.cancel();
				mouseMoveTimer.schedule(MOUSE_MOVEMENT_DELAY);
				if (!floatingMenu.isVisible()) updateFloatingHelperWidgetVisibility();
			}
		};

		floatingMenu = new FloatingActionMenu(new FloatingMenuActionHandler() {

			@Override
			public void onInternalAction(final OneStepInternalAction action) {
				if (actionHandler == null) return;
				floatingMenu.setVisible(false);
				actionHandler.onInternalAction(action);
			}

			@Override
			public void onInternalAction(final TwoStepInternalAction action) {
				if (actionHandler == null) return;
				floatingMenu.setVisible(false);
				actionHandler.onInternalAction(action);
			}

			@Override
			public void onUserActionExecutionRequest(final ModelAction action) {
				if (actionExecutionService == null) return;
				floatingMenu.setVisible(false);
				actionExecutionService.onUserActionExecutionRequest(action);
			}
		});
		resetXCoordinate();
		RootPanel.get().add(floatingMenu);
		floatingMenu.getElement().getStyle().setPosition(Style.Position.ABSOLUTE);
		floatingMenu.setVisible(false);
	}

	private void resetXCoordinate() {
		clientX = Window.getClientWidth() - 450 - floatingMenu.getOffsetWidth();
	}

	protected void updateFloatingHelperWidgetVisibility() {
		if (scopeTreeItemWidget == null || !mouseHasMoved) {
			if (!floatingMenu.isVisible()) return;
			floatingMenu.setVisible(false);
		}
		else {
			floatingMenu.setReferencedScope(scopeTreeItemWidget.getScope(), projectContext);
			final boolean wasInvisible = (!floatingMenu.isVisible());
			floatingMenu.setVisible(true);
			final int clientY = scopeTreeItemWidget.getAbsoluteTop() - floatingMenu.getOffsetHeight();
			if (wasInvisible) resetXCoordinate();
			floatingMenu.getElement().getStyle().setTop(clientY, Unit.PX);
			floatingMenu.getElement().getStyle().setLeft(clientX, Unit.PX);
		}
	}

	public void unregister() {
		if (actionHandler != null) actionHandler = null;
		if (actionExecutionService != null) actionExecutionService = null;
		if (projectContext != null) projectContext = null;
		GlobalNativeEventService.getInstance().removeMouseMoveListener(mouseEventListener);
		if (selectionHandlerRegistration == null) return;
		selectionHandlerRegistration.removeHandler();
		selectionHandlerRegistration = null;
	}

	public void register(final EventBus eventBus, final ActionExecutionService actionExecutionService, final ScopeTreeInternalActionHandler actionHandler,
			final ProjectContext projectContext) {
		this.actionExecutionService = actionExecutionService;
		this.actionHandler = actionHandler;
		this.projectContext = projectContext;
		GlobalNativeEventService.getInstance().addMouseMoveListener(mouseEventListener);
		selectionHandlerRegistration = eventBus.addHandler(ScopeTreeItemSelectionEvent.getType(), selectionHandler);
	}
}
