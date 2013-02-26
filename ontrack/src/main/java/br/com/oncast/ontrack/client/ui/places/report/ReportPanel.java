package br.com.oncast.ontrack.client.ui.places.report;

import java.util.List;

import br.com.oncast.ontrack.client.ui.components.appmenu.ApplicationMenu;
import br.com.oncast.ontrack.client.ui.components.report.ScopeReportTable;
import br.com.oncast.ontrack.client.ui.generalwidgets.layout.ApplicationMenuAndWidgetContainer;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ReportPanel extends Composite {

	private static ReportPanelUiBinder uiBinder = GWT.create(ReportPanelUiBinder.class);

	interface ReportPanelUiBinder extends UiBinder<Widget, ReportPanel> {}

	@UiField
	protected ApplicationMenuAndWidgetContainer rootPanel;

	@UiField(provided = true)
	protected ScopeReportTable table;

	public ReportPanel(final List<Scope> scopeList) {
		table = new ScopeReportTable(scopeList);
		initWidget(uiBinder.createAndBindUi(this));
	}

	public Widget getAlertingContainer() {
		return rootPanel.getContentPanelWidget();
	}

	public ApplicationMenu getApplicationMenu() {
		return rootPanel.getMenu();
	}

	public ScopeReportTable getScopeReportTable() {
		return table;
	}
}
