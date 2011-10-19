package br.com.oncast.ontrack.client.ui.places.planning;

import br.com.oncast.ontrack.client.ui.components.appmenu.ApplicationMenu;
import br.com.oncast.ontrack.client.ui.components.releasepanel.ReleasePanel;
import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTree;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class PlanningPanel extends Composite implements PlanningView {

	private static PlanningPanelUiBinder uiBinder = GWT.create(PlanningPanelUiBinder.class);

	interface PlanningPanelUiBinder extends UiBinder<Widget, PlanningPanel> {}

	@UiField
	protected ReleasePanel releasePanel;

	@UiField
	protected ScopeTree scopeTree;

	@UiField
	protected ApplicationMenu applicationMenu;

	@UiField
	protected Anchor exportMapLink;

	public PlanningPanel() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	public void setExporterPath(final String href) {
		exportMapLink.setHref(href);
	}

	@Override
	public ScopeTree getScopeTree() {
		return scopeTree;
	}

	@Override
	public ReleasePanel getReleasePanel() {
		return releasePanel;
	}

	@Override
	public ApplicationMenu getApplicationMenu() {
		return applicationMenu;
	}

}