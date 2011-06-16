package br.com.oncast.ontrack.client.ui.components.releasepanel;

import br.com.oncast.ontrack.client.ui.components.Component;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ReleasePanelWidget;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ActionExecutionListener;
import br.com.oncast.ontrack.client.ui.components.scopetree.actions.ActionExecutionRequestHandler;
import br.com.oncast.ontrack.shared.project.ProjectContext;
import br.com.oncast.ontrack.shared.release.Release;
import br.com.oncast.ontrack.shared.scope.actions.ScopeAction;
import br.com.oncast.ontrack.shared.scope.actions.ScopeRemoveAction;
import br.com.oncast.ontrack.shared.scope.actions.ScopeUpdateAction;
import br.com.oncast.ontrack.shared.util.deeplyComparable.DeeplyComparable;

import com.google.gwt.user.client.ui.Widget;

public class ReleasePanel implements Component, DeeplyComparable {

	private final ReleasePanelWidget releasePanelWidget;
	private final ActionExecutionListener actionExecutionListener;
	private Release rootRelease;

	public ReleasePanel() {
		releasePanelWidget = new ReleasePanelWidget();

		actionExecutionListener = new ActionExecutionListener() {
			@Override
			public void onActionExecution(final ScopeAction action, final ProjectContext context, final boolean wasRollback) {
				if (action instanceof ScopeUpdateAction || action instanceof ScopeRemoveAction) update();
			}
		};
	}

	protected void update() {
		releasePanelWidget.update();
	}

	public void setRelease(final Release release) {
		this.rootRelease = release;
		releasePanelWidget.setRelease(rootRelease);
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
	public int hashCode() {
		return rootRelease.hashCode();
	}

	@Override
	public boolean deepEquals(final Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (!(obj instanceof ReleasePanel)) return false;
		final ReleasePanel other = (ReleasePanel) obj;
		if (releasePanelWidget == null) {
			if (other.releasePanelWidget != null) return false;
		}
		else if (!releasePanelWidget.deepEquals(other.releasePanelWidget)) return false;
		return true;
	}
}
