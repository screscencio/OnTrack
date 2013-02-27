package br.com.oncast.ontrack.client.ui.places.report;

import br.com.oncast.ontrack.client.ui.components.annotations.widgets.ReleaseDetailWidget;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.chart.ReleaseChart;
import br.com.oncast.ontrack.client.ui.components.report.ScopeReportTable;
import br.com.oncast.ontrack.client.ui.places.timesheet.widgets.TimesheetWidget;
import br.com.oncast.ontrack.shared.model.release.Release;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class ReportPanel extends Composite {

	private static ReportPanelUiBinder uiBinder = GWT.create(ReportPanelUiBinder.class);

	interface ReportPanelUiBinder extends UiBinder<Widget, ReportPanel> {}

	@UiField
	HTMLPanel rootPanel;

	@UiField
	Label releaseTitle;

	@UiField
	Panel burnUpPanel;

	@UiField(provided = true)
	ReleaseDetailWidget details;

	@UiField(provided = true)
	ScopeReportTable table;

	@UiField(provided = true)
	TimesheetWidget timesheet;

	private final ReleaseChart chart;

	public ReportPanel(final Release release) {
		chart = new ReleaseChart(release, true);
		details = new ReleaseDetailWidget(release);
		table = new ScopeReportTable(release.getAllScopesIncludingDescendantReleases());
		timesheet = new TimesheetWidget(release, true);

		initWidget(uiBinder.createAndBindUi(this));

		releaseTitle.setText(release.getDescription());

		burnUpPanel.add(chart);
		chart.updateData();
	}

	public Widget getAlertingContainer() {
		return rootPanel;
	}

	public ScopeReportTable getScopeReportTable() {
		return table;
	}
}
