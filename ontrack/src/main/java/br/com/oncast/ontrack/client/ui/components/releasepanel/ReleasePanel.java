package br.com.oncast.ontrack.client.ui.components.releasepanel;

import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ReleasePanelWidget;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ActionExecutionListener;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ActionExecutionRequestHandler;
import br.com.oncast.ontrack.shared.release.Release;
import br.com.oncast.ontrack.shared.scope.actions.ScopeAction;
import br.com.oncast.ontrack.shared.scope.actions.ScopeUpdateAction;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class ReleasePanel implements IsWidget {

	private final ReleasePanelWidget releasePanelWidget;
	private final ActionExecutionListener actionExecutionListener;
	private ActionExecutionRequestHandler actionHandler;
	private Release rootRelease;

	public ReleasePanel() {
		releasePanelWidget = new ReleasePanelWidget();

		actionExecutionListener = new ActionExecutionListener() {
			@Override
			public void onActionExecution(final ScopeAction action, final boolean wasRollback) {
				if (action instanceof ScopeUpdateAction) refresh();
			}
		};
	}

	// TODO Refactor to a more performatic approach
	protected void refresh() {
		releasePanelWidget.setReleases(rootRelease.getChildReleases());
	}

	public void setRelease(final Release release) {
		this.rootRelease = release;
		releasePanelWidget.setReleases(rootRelease.getChildReleases());
	}

	public ActionExecutionListener getActionExecutionListener() {
		return actionExecutionListener;
	}

	public void setActionExecutionRequestHandler(final ActionExecutionRequestHandler actionHandler) {
		this.actionHandler = actionHandler;
	}

	@Override
	public Widget asWidget() {
		return releasePanelWidget;
	}
}
