package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.ui.components.releasepanel.events.ReleaseContainerStateChangeEvent;
import br.com.oncast.ontrack.client.ui.components.releasepanel.events.ReleaseContainerStateChangeEventHandler;
import br.com.oncast.ontrack.client.ui.components.releasepanel.events.ReleaseDetailUpdateEvent;
import br.com.oncast.ontrack.client.ui.components.releasepanel.events.ReleaseDetailUpdateEventHandler;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.dnd.ItemDroppedListener;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.dnd.ReleaseScopeItemDragHandler;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeDetailUpdateEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeDetailUpdateEventHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.DragAndDropManager;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.DropControllerFactory;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.KanbanAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseDeclareEstimatedVelocityAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseRemoveAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseRemoveRollbackAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseRenameAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseScopeUpdatePriorityAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseUpdatePriorityAction;
import br.com.oncast.ontrack.shared.model.action.ScopeAddAssociatedUserAction;
import br.com.oncast.ontrack.shared.model.action.ScopeBindHumanIdAction;
import br.com.oncast.ontrack.shared.model.action.ScopeBindReleaseAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareEffortAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareProgressAction;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareValueAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertChildRollbackAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertParentRollbackAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertSiblingDownRollbackAction;
import br.com.oncast.ontrack.shared.model.action.ScopeInsertSiblingUpRollbackAction;
import br.com.oncast.ontrack.shared.model.action.ScopeMoveLeftAction;
import br.com.oncast.ontrack.shared.model.action.ScopeMoveRightAction;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveAction;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveAssociatedUserAction;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveRollbackAction;
import br.com.oncast.ontrack.shared.model.action.ScopeUnbindHumanIdAction;
import br.com.oncast.ontrack.shared.model.action.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.deepEquality.IgnoredByDeepEquality;

import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class ReleasePanelWidget extends Composite {

	interface ReleasePanelWidgetUiBinder extends UiBinder<Widget, ReleasePanelWidget> {}

	@IgnoredByDeepEquality
	private static ReleasePanelWidgetUiBinder uiBinder = GWT.create(ReleasePanelWidgetUiBinder.class);

	@UiField
	@IgnoredByDeepEquality
	protected ModelWidgetContainer<Release, ReleaseWidget> releaseContainer;

	@UiField
	protected FlowPanel noReleaseText;

	private Release rootRelease;

	@IgnoredByDeepEquality
	private final ActionExecutionListener actionExecutionListener;

	// IMPORTANT: This field cannot be 'final' because some tests need to set it to a new value through reflection. Do not remove the 'null' attribution.
	@IgnoredByDeepEquality
	private ModelWidgetFactory<Release, ReleaseWidget> releaseWidgetFactory = null;

	private final Set<HandlerRegistration> handlerRegistration;

	private final boolean releaseSpecific;

	private final DragAndDropManager dragAndDropManager;

	public ReleasePanelWidget(final ReleasePanelWidgetInteractionHandler releasePanelInteractionHandler) {
		this(releasePanelInteractionHandler, null, null, false);
	}

	public ReleasePanelWidget(
			final ReleasePanelWidgetInteractionHandler releasePanelInteractionHandler,
			final DragAndDropManager userDragAndDropManager,
			final DropControllerFactory userDropControllerFactory,
			final boolean releaseSpecific) {
		dragAndDropManager = new DragAndDropManager();
		this.releaseSpecific = releaseSpecific;
		handlerRegistration = new HashSet<HandlerRegistration>();
		releaseWidgetFactory = new ReleaseWidgetFactory(releasePanelInteractionHandler, new ScopeWidgetFactory(dragAndDropManager, userDragAndDropManager,
				userDropControllerFactory, releaseSpecific),
				dragAndDropManager,
				releaseSpecific);

		initWidget(uiBinder.createAndBindUi(this));
		dragAndDropManager.configureBoundaryPanel(RootPanel.get());
		dragAndDropManager.addDragHandler(createScopeItemDragHandler(releasePanelInteractionHandler));

		actionExecutionListener = new ActionExecutionListener() {
			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final ActionContext actionContext,
					final Set<UUID> inferenceInfluencedScopeSet, final boolean isUserAction) {

				if (action instanceof ScopeUpdateAction ||
						action instanceof ScopeRemoveAction ||
						action instanceof ScopeInsertAction ||
						action instanceof ScopeInsertParentRollbackAction ||
						action instanceof ScopeInsertChildRollbackAction ||
						action instanceof ScopeInsertSiblingUpRollbackAction ||
						action instanceof ScopeInsertSiblingDownRollbackAction ||
						action instanceof ScopeMoveLeftAction ||
						action instanceof ScopeMoveRightAction ||
						action instanceof ScopeRemoveRollbackAction ||
						action instanceof ScopeDeclareProgressAction ||
						action instanceof ScopeDeclareEffortAction ||
						action instanceof ScopeDeclareValueAction ||
						action instanceof ScopeBindReleaseAction ||
						action instanceof ScopeBindHumanIdAction ||
						action instanceof ScopeUnbindHumanIdAction ||
						action instanceof ScopeRemoveAssociatedUserAction ||
						action instanceof ScopeAddAssociatedUserAction ||
						action instanceof ReleaseRemoveAction ||
						action instanceof ReleaseRemoveRollbackAction ||
						action instanceof ReleaseUpdatePriorityAction ||
						action instanceof ReleaseScopeUpdatePriorityAction ||
						action instanceof ReleaseRenameAction ||
						action instanceof ReleaseDeclareEstimatedVelocityAction ||
						action instanceof KanbanAction) update();

				if (action instanceof ScopeBindReleaseAction || action instanceof ReleaseScopeUpdatePriorityAction
						|| action instanceof ScopeDeclareEffortAction || action instanceof ScopeDeclareValueAction) {
					try {
						final UUID scopeId = (action instanceof ReleaseScopeUpdatePriorityAction) ? ((ReleaseScopeUpdatePriorityAction) (action))
								.getScopeReferenceId() : action.getReferenceId();
						final Release release = ClientServices.getCurrentProjectContext().findScope(scopeId).getRelease();
						ClientServices.get().eventBus().fireEvent(new ReleaseScopeListUpdateEvent(release));
					}
					catch (final ScopeNotFoundException e) {}
				}
			}
		};
	}

	@Override
	protected void onLoad() {
		handlerRegistration.add(ClientServices.get().eventBus()
				.addHandler(ReleaseContainerStateChangeEvent.getType(), new ReleaseContainerStateChangeEventHandler() {
					@Override
					public void onReleaseContainerStateChange(final ReleaseContainerStateChangeEvent event) {
						if (rootRelease == null || event.getSource() instanceof ReleaseWidget) return;
						final ReleaseWidget widget = getWidgetFor(event.getTargetRelease());
						widget.setContainerState(event.getTargetContainerState(), false);
					}
				}));

		handlerRegistration.add(ClientServices.get().eventBus()
				.addHandler(ReleaseDetailUpdateEvent.getType(), new ReleaseDetailUpdateEventHandler() {
					@Override
					public void onReleaseDetailUpdate(final ReleaseDetailUpdateEvent event) {
						final ReleaseWidget widget = releaseContainer.getWidgetFor(event.getTargetRelease());
						if (widget == null) return;
						widget.update();
					}
				}));
		handlerRegistration.add(ClientServices.get().eventBus()
				.addHandler(ScopeDetailUpdateEvent.getType(), new ScopeDetailUpdateEventHandler() {
					@Override
					public void onScopeDetailUpdate(final ScopeDetailUpdateEvent event) {
						final Scope scope = event.getTargetScope();
						final Release release = scope.getRelease();
						if (release == null) return;

						final ReleaseWidget releaseWidget = getWidgetFor(release);

						final ReleaseScopeWidget scopeWidget = releaseWidget.getScopeContainer().getWidgetFor(scope);
						scopeWidget.setHasOpenImpediments(event.hasOpenImpediments());
					}

				}));
	}

	@Override
	protected void onUnload() {
		for (final HandlerRegistration registration : new HashSet<HandlerRegistration>(handlerRegistration)) {
			registration.removeHandler();
			handlerRegistration.remove(registration);
		}
	}

	public void setRelease(final Release rootRelease) {
		this.rootRelease = rootRelease;
		update();
	}

	public Release getRelease() {
		return this.rootRelease;
	}

	public void update() {
		final List<Release> children = new ArrayList<Release>();
		if (releaseSpecific) {
			children.add(rootRelease);
		}
		else {
			children.addAll(rootRelease.getChildren());
		}
		noReleaseText.setVisible(children.isEmpty());
		releaseContainer.update(children);
	}

	public ReleaseWidget getWidgetFor(final Release release) {
		if (releaseSpecific || rootRelease.equals(release.getParent())) {
			final ReleaseWidget widget = releaseContainer.getWidgetFor(release);
			if (widget == null) throw new RuntimeException("Release not found");
			return widget;
		}

		final ReleaseWidget widget = getWidgetFor(release.getParent()).getChildReleasesContainer().getWidgetFor(release);
		if (widget == null) throw new RuntimeException("Release not found");
		return widget;
	}

	@UiFactory
	protected ModelWidgetContainer<Release, ReleaseWidget> createReleaseContainer() {
		return new ModelWidgetContainer<Release, ReleaseWidget>(releaseWidgetFactory);
	}

	private ReleaseScopeItemDragHandler createScopeItemDragHandler(final ReleasePanelWidgetInteractionHandler releasePanelInteractionHandler) {
		return new ReleaseScopeItemDragHandler(createItemDroppedListener(releasePanelInteractionHandler));
	}

	private ItemDroppedListener createItemDroppedListener(final ReleasePanelWidgetInteractionHandler releasePanelInteractionHandler) {
		return new ItemDroppedListener() {
			@Override
			public void onItemDropped(final Scope droppedScope, final Release targetRelease, final int newScopePosition) {
				releasePanelInteractionHandler.onScopeDragAndDropRequest(droppedScope, targetRelease, newScopePosition);
			}

			@Override
			public void onItemDropped(final Scope droppedScope) {
				if (releaseSpecific) releasePanelInteractionHandler.onScopeUnderworkdDropRequest(droppedScope);
			}
		};
	}

	public ActionExecutionListener getActionExecutionListener() {
		return actionExecutionListener;
	}

	public void registerDropController(final DropController controller) {
		dragAndDropManager.registerDropController(controller);
	}
}
