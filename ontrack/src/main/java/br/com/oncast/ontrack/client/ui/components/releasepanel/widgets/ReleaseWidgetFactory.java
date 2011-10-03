package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.dnd.DropTargetCreationListener;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;

public class ReleaseWidgetFactory implements ModelWidgetFactory<Release, ReleaseWidget> {

	private final ReleasePanelWidgetInteractionHandler releasePanelInteractionHandler;
	private final ModelWidgetFactory<Scope, ScopeWidget> scopeWidgetFactory;
	private final DropTargetCreationListener dropTargetCreationListener;

	public ReleaseWidgetFactory(final ReleasePanelWidgetInteractionHandler releasePanelInteractionHandler,
			final ModelWidgetFactory<Scope, ScopeWidget> scopeWidgetFactory, final DropTargetCreationListener dropTargetCreationListener) {

		this.releasePanelInteractionHandler = releasePanelInteractionHandler;
		this.scopeWidgetFactory = scopeWidgetFactory;
		this.dropTargetCreationListener = dropTargetCreationListener;
	}

	@Override
	public ReleaseWidget createWidget(final Release release) {
		final ReleaseWidget widget = new ReleaseWidget(release, this, scopeWidgetFactory, releasePanelInteractionHandler);
		widget.setContainerState(true);

		dropTargetCreationListener.onDropTargetCreated(widget.getDroppableArea());
		return widget;
	}
}
