package br.com.oncast.ontrack.client.ui.components.releasepanel;

import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ReleasePanelWidget;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ActionExecutionListener;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ActionExecutionRequestHandler;
import br.com.oncast.ontrack.shared.release.Release;
import br.com.oncast.ontrack.shared.scope.actions.ScopeAction;
import br.com.oncast.ontrack.shared.scope.actions.ScopeRemoveAction;
import br.com.oncast.ontrack.shared.scope.actions.ScopeUpdateAction;

import com.google.gwt.user.client.ui.Widget;

public class ReleasePanel implements Component {

	private final ReleasePanelWidget releasePanelWidget;
	private final ActionExecutionListener actionExecutionListener;
	private Release rootRelease;

	public ReleasePanel() {
		releasePanelWidget = new ReleasePanelWidget();

		actionExecutionListener = new ActionExecutionListener() {
			@Override
			public void onActionExecution(final ScopeAction action, final boolean wasRollback) {
				if (action instanceof ScopeUpdateAction || action instanceof ScopeRemoveAction) refresh();
			}
		};
	}

	// TODO Is really necessary to send the release list?
	protected void refresh() {
		releasePanelWidget.updateReleases(rootRelease.getChildReleases());
	}

	public void setRelease(final Release release) {
		this.rootRelease = release;
		releasePanelWidget.init(rootRelease.getChildReleases());
	}

	@Override
	public ActionExecutionListener getActionExecutionListener() {
		return actionExecutionListener;
	}

	@Override
	public void setActionExecutionRequestHandler(final ActionExecutionRequestHandler actionHandler) {}

	@Override
	public Widget asWidget() {
		return releasePanelWidget;
	}

	@Override
	public boolean equals(final Object other) {
		if (!(other instanceof ReleasePanel)) return false;
		final ReleasePanel otherReleasePanel = (ReleasePanel) other;

		return releasePanelWidget.equals(otherReleasePanel.asWidget());
	}
}
