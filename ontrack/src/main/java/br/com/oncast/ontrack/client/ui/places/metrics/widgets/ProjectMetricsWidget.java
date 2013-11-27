package br.com.oncast.ontrack.client.ui.places.metrics.widgets;

import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.shared.services.metrics.ProjectMetrics;

import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ParagraphElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ProjectMetricsWidget extends Composite implements ModelWidget<ProjectMetrics> {

	private static ProjectMetricsWidgetUiBinder uiBinder = GWT.create(ProjectMetricsWidgetUiBinder.class);

	interface ProjectMetricsWidgetUiBinder extends UiBinder<Widget, ProjectMetricsWidget> {}

	@UiField
	ParagraphElement projectName;

	@UiField
	SpanElement usersCount;

	@UiField
	SpanElement scopesCount;

	@UiField
	SpanElement scopesDepth;

	@UiField
	SpanElement releasesCount;

	@UiField
	SpanElement releasesDepth;

	@UiField
	SpanElement releasesDuration;

	@UiField
	SpanElement storiesPerRelease;

	private final ProjectMetrics metrics;

	public ProjectMetricsWidget(final ProjectMetrics metrics) {
		this.metrics = metrics;
		initWidget(uiBinder.createAndBindUi(this));
		update();
	}

	@Override
	public boolean update() {
		projectName.setInnerText(metrics.getProjectName());
		usersCount.setInnerText("" + metrics.getUsersCount());
		scopesCount.setInnerText("" + metrics.getScopesCount());
		releasesCount.setInnerText("" + metrics.getReleasesCount());
		scopesDepth.setInnerText(getAverageSlashMaxString(metrics.getScopesDepth()));
		releasesDepth.setInnerText(getAverageSlashMaxString(metrics.getReleasesDepth()));
		releasesDuration.setInnerText(getAverageSlashMaxString(metrics.getReleasesDuration()));
		storiesPerRelease.setInnerText(getAverageSlashMaxString(metrics.getStoriesPerRelease()));

		return false;
	}

	private String getAverageSlashMaxString(final List<Integer> elements) {
		if (elements.isEmpty()) return "None";

		int sum = 0;
		int max = Integer.MIN_VALUE;
		for (final Integer i : elements) {
			sum += i;
			max = Math.max(max, i);
		}
		final int average = sum / elements.size();

		return (average == max) ? "" + average : (average + " / " + max);
	}

	@Override
	public ProjectMetrics getModelObject() {
		return metrics;
	}

}
