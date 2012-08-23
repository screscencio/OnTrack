package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import java.util.List;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.components.releasepanel.events.ReleaseContainerStateChangeEvent;
import br.com.oncast.ontrack.client.ui.components.releasepanel.events.ReleaseContainerStateChangeEventHandler;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.dnd.ItemDroppedListener;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.dnd.ReleaseScopeItemDragHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetContainerListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.VerticalModelWidgetContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.DragAndDropManager;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.utils.deepEquality.IgnoredByDeepEquality;

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
	protected VerticalModelWidgetContainer<Release, ReleaseWidget> releaseContainer;

	@UiField
	protected FlowPanel noReleaseText;

	private Release rootRelease;

	// IMPORTANT: This field cannot be 'final' because some tests need to set it to a new value through reflection. Do not remove the 'null' attribution.
	@IgnoredByDeepEquality
	private ModelWidgetFactory<Release, ReleaseWidget> releaseWidgetFactory = null;

	private HandlerRegistration handlerRegistration;

	public ReleasePanelWidget(final ReleasePanelWidgetInteractionHandler releasePanelInteractionHandler) {
		final DragAndDropManager dragAndDropManager = new DragAndDropManager();
		releaseWidgetFactory = new ReleaseWidgetFactory(releasePanelInteractionHandler, new ScopeWidgetFactory(dragAndDropManager), dragAndDropManager);

		initWidget(uiBinder.createAndBindUi(this));
		dragAndDropManager.configureBoundaryPanel(RootPanel.get());
		dragAndDropManager.setDragHandler(createScopeItemDragHandler(releasePanelInteractionHandler));
	}

	@Override
	protected void onLoad() {
		handlerRegistration = ClientServiceProvider.getInstance().getEventBus()
				.addHandler(ReleaseContainerStateChangeEvent.getType(), new ReleaseContainerStateChangeEventHandler() {
					@Override
					public void onReleaseContainerStateChange(final ReleaseContainerStateChangeEvent event) {
						if (rootRelease == null || event.getSource() instanceof ReleaseWidget) return;
						final ReleaseWidget widget = getWidgetFor(event.getTargetRelease());
						widget.setContainerState(event.getTargetContainerState(), false);
					}
				});
	}

	@Override
	protected void onUnload() {
		if (handlerRegistration == null) return;

		handlerRegistration.removeHandler();
		handlerRegistration = null;
	}

	public void setRelease(final Release rootRelease) {
		this.rootRelease = rootRelease;
		update();
	}

	public void update() {
		final List<Release> children = rootRelease.getChildren();
		noReleaseText.setVisible(children.isEmpty());
		releaseContainer.update(children);
	}

	public ReleaseWidget getWidgetFor(final Release release) {
		if (rootRelease.equals(release.getParent())) {
			final ReleaseWidget widget = releaseContainer.getWidgetFor(release);
			if (widget == null) throw new RuntimeException("Release not found");
			return widget;
		}
		final ReleaseWidget parentWidget = getWidgetFor(release.getParent());

		final ReleaseWidget widget = parentWidget.getChildReleasesContainer().getWidgetFor(release);
		if (widget == null) throw new RuntimeException("Release not found");
		return widget;
	}

	@UiFactory
	protected VerticalModelWidgetContainer<Release, ReleaseWidget> createReleaseContainer() {
		return new VerticalModelWidgetContainer<Release, ReleaseWidget>(releaseWidgetFactory, new ModelWidgetContainerListener() {
			@Override
			public void onUpdateComplete(final boolean hasChanged) {}
		});
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
		};
	}
}
