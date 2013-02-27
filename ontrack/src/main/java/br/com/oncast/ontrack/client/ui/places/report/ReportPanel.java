package br.com.oncast.ontrack.client.ui.places.report;

import static br.com.oncast.ontrack.client.services.ClientServiceProvider.getCurrentProjectContext;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import br.com.oncast.ontrack.client.ui.components.annotations.widgets.ReleaseDetailWidget;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.chart.ReleaseChart;
import br.com.oncast.ontrack.client.ui.components.report.ScopeReportTable;
import br.com.oncast.ontrack.client.ui.places.timesheet.widgets.TimesheetWidget;
import br.com.oncast.ontrack.shared.model.description.exceptions.DescriptionNotFoundException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;

import com.google.common.base.Joiner;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;

public class ReportPanel extends Composite {

	private static final DateTimeFormat FORMATTER = DateTimeFormat.getFormat("dd/mm/yyyy - HH:MM");

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

	@UiField
	InlineHTML description;

	@UiField
	DivElement descriptionContainer;

	@UiField
	InlineHTML ancestors;

	@UiField
	Label timestamp;

	private final ReleaseChart chart;

	public ReportPanel(final ProjectContext projectContext, final Release release) {
		chart = new ReleaseChart(release, true);
		details = new ReleaseDetailWidget(release);
		table = new ScopeReportTable(release.getAllScopesIncludingDescendantReleases(), projectContext);
		timesheet = new TimesheetWidget(release, true);

		initWidget(uiBinder.createAndBindUi(this));

		releaseTitle.setText(release.getDescription());
		ancestors.setHTML(getAncestorsBreadcrumb(release));
		timestamp.setText(FORMATTER.format(new Date()));

		try {
			description.setHTML(getCurrentProjectContext().findDescriptionFor(release.getId()).getDescription());
		}
		catch (final DescriptionNotFoundException e) {
			descriptionContainer.removeFromParent();
		}

		burnUpPanel.add(chart);
		chart.updateData();
	}

	private String getAncestorsBreadcrumb(final Release release) {
		final String projectName = "<span style=\"color: #2D4171; font-size: 18px;\">" + release.getRootRelease().getDescription() + "</span>";

		final List<Release> ancestors = release.getAncestors();
		if (ancestors.isEmpty()) return projectName;

		Collections.reverse(ancestors);
		return projectName + " &gt; " + Joiner.on(" &gt; ").join(ancestors);
	}

	public Widget getAlertingContainer() {
		return rootPanel;
	}

	public ScopeReportTable getScopeReportTable() {
		return table;
	}
}
