package br.com.oncast.ontrack.client.ui.components.releasepanel;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionRequestHandler;
import br.com.oncast.ontrack.client.ui.components.Component;
import br.com.oncast.ontrack.client.ui.components.releasepanel.interaction.ReleasePanelInteractionHandler;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ReleasePanelWidget;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ReleaseWidget;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.utils.deepEquality.IgnoredByDeepEquality;

import com.google.gwt.user.client.ui.Widget;

public class ReleasePanel implements Component {

	private final ReleasePanelWidget releasePanelWidget;

	// TODO Review if this should be tested by deepEquality
	@IgnoredByDeepEquality
	private Release rootRelease;

	@IgnoredByDeepEquality
	private ReleasePanelInteractionHandler releasePanelInteractionHandler;

	public ReleasePanel() {
		releasePanelWidget = new ReleasePanelWidget(releasePanelInteractionHandler = new ReleasePanelInteractionHandler());
	}

	public void setRelease(final Release release) {
		this.rootRelease = release;
		releasePanelWidget.setRelease(rootRelease);
	}

	@Override
	public ActionExecutionListener getActionExecutionListener() {
		return releasePanelWidget.getActionExecutionListener();
	}

	@Override
	public void setActionExecutionRequestHandler(final ActionExecutionRequestHandler actionHandler) {
		releasePanelInteractionHandler.configureActionExecutionRequestHandler(actionHandler);
	}

	@Override
	public Widget asWidget() {
		return releasePanelWidget;
	}

	@Override
	public int hashCode() {
		return rootRelease.hashCode();
	}

	public ReleaseWidget getWidgetFor(final Release release) {
		return releasePanelWidget.getWidgetFor(release);
	}
}
