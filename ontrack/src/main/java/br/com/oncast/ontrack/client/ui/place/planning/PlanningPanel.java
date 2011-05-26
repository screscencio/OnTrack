package br.com.oncast.ontrack.client.ui.place.planning;

import java.util.List;

import br.com.oncast.ontrack.client.ui.component.releasepanel.ReleasePanel;
import br.com.oncast.ontrack.client.ui.component.scopetree.ScopeTree;
import br.com.oncast.ontrack.shared.release.Release;
import br.com.oncast.ontrack.shared.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class PlanningPanel implements PlanningView {

	private static PlanningPanelUiBinder uiBinder = GWT.create(PlanningPanelUiBinder.class);

	interface PlanningPanelUiBinder extends UiBinder<Widget, PlanningPanel> {}

	@UiField
	protected SplitLayoutPanel planningPanel;

	@UiField
	protected ReleasePanel releasePanel;

	@UiField
	protected ScopeTree scopeTree;

	public PlanningPanel() {
		uiBinder.createAndBindUi(this);
	}

	@UiFactory
	ReleasePanel createReleasePanel() {
		return new ReleasePanel(Unit.PX);
	}

	@Override
	public Widget asWidget() {
		return planningPanel.asWidget();
	}

	@Override
	public void setScope(final Scope scope) {
		scopeTree.setScope(scope);
		scopeTree.setFocus(true);
	}

	@Override
	public void setReleases(final List<Release> releases) {
		releasePanel.setReleases(releases);
	}
}