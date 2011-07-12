package br.com.oncast.ontrack.client.ui.places.planning;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionRequestHandler;
import br.com.oncast.ontrack.client.ui.components.releasepanel.ReleasePanel;
import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTree;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

// TODO Refactor this XMLs file so that "sizes" are proportional (and not defined by pixels in a hardcoded manner).
public class PlanningPanel extends Composite implements PlanningView {

	private static PlanningPanelUiBinder uiBinder = GWT.create(PlanningPanelUiBinder.class);

	interface PlanningPanelUiBinder extends UiBinder<Widget, PlanningPanel> {}

	@UiField
	protected ReleasePanel releasePanel;

	@UiField
	protected ScopeTree scopeTree;

	public PlanningPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setScope(final Scope scope) {
		scopeTree.setScope(scope);
		scopeTree.setFocus(true);
	}

	@Override
	public void setRelease(final Release release) {
		releasePanel.setRelease(release);
	}

	@Override
	public void setActionExecutionRequestHandler(final ActionExecutionRequestHandler actionHandler) {
		scopeTree.setActionExecutionRequestHandler(actionHandler);
		releasePanel.setActionExecutionRequestHandler(actionHandler);
	}

	@Override
	public List<ActionExecutionListener> getActionExecutionSuccessListeners() {
		final List<ActionExecutionListener> list = new ArrayList<ActionExecutionListener>();
		list.add(scopeTree.getActionExecutionListener());
		list.add(releasePanel.getActionExecutionListener());
		return list;
	}
}