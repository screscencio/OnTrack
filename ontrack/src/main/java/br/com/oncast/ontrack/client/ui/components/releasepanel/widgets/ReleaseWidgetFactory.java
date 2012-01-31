package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.DragAndDropManager;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class ReleaseWidgetFactory implements ModelWidgetFactory<Release, ReleaseWidget> {

	private final ReleasePanelWidgetInteractionHandler releasePanelInteractionHandler;
	private final ModelWidgetFactory<Scope, ScopeWidget> scopeWidgetFactory;
	private ReleaseWidget widget;
	private final DragAndDropManager dragAndDropManager;

	public ReleaseWidgetFactory(final ReleasePanelWidgetInteractionHandler releasePanelInteractionHandler,
			final ModelWidgetFactory<Scope, ScopeWidget> scopeWidgetFactory, final DragAndDropManager dragAndDropManager) {

		this.releasePanelInteractionHandler = releasePanelInteractionHandler;
		this.scopeWidgetFactory = scopeWidgetFactory;
		this.dragAndDropManager = dragAndDropManager;
	}

	@Override
	public ReleaseWidget createWidget(final Release release) {
		widget = new ReleaseWidget(release, this, scopeWidgetFactory, releasePanelInteractionHandler);
		dragAndDropManager.monitorDropTarget(widget.getScopeContainer().getVerticalContainer());
		widget.setContainerState(true);

		return widget;
	}
}
