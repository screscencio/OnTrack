package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import static br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat.roundFloat;

import java.util.Set;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.chart.ReleaseChartDataProvider;
import br.com.oncast.ontrack.client.utils.date.HumanDateFormatter;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseRenameAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.ReleaseEstimator;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.WorkingDay;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.Widget;

public class ReleaseDetailWidget extends Composite implements SubjectDetailWidget {

	private static ReleaseDetailWidgetUiBinder uiBinder = GWT.create(ReleaseDetailWidgetUiBinder.class);

	private static final ReleaseDetailWidgetMessages messages = GWT.create(ReleaseDetailWidgetMessages.class);

	interface ReleaseDetailWidgetUiBinder extends UiBinder<Widget, ReleaseDetailWidget> {}

	public ReleaseDetailWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	HasText parent;

	@UiField
	HasText effort;

	@UiField
	HasText value;

	@UiField
	HasText velocity;

	@UiField
	HasText period;

	@UiField
	HasText duration;

	private Release release;

	private ActionExecutionListener actionExecutionListener;

	private ReleaseChartDataProvider dataProvider;

	public ReleaseDetailWidget(final Release release) {
		this();
		setSubject(release);
	}

	public void setSubject(final Release release) {
		this.release = release;
		this.dataProvider = new ReleaseChartDataProvider(release, new ReleaseEstimator(getRootRelease()), ClientServiceProvider.getInstance()
				.getActionExecutionService());
		update();
	}

	private Release getRootRelease() {
		Release root = release;
		while (!root.isRoot())
			root = root.getParent();
		return root;
	}

	@Override
	protected void onLoad() {
		getActionExecutionService().addActionExecutionListener(getActionExecutionListener());
	}

	@Override
	protected void onUnload() {
		getActionExecutionService().removeActionExecutionListener(getActionExecutionListener());
	}

	private ActionExecutionService getActionExecutionService() {
		return ClientServiceProvider.getInstance().getActionExecutionService();
	}

	private ActionExecutionListener getActionExecutionListener() {
		if (actionExecutionListener == null) actionExecutionListener = new ActionExecutionListener() {
			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final ActionContext actionContext,
					final Set<UUID> inferenceInfluencedScopeSet, final boolean isUserAction) {
				if (action instanceof ReleaseRenameAction && action.getReferenceId().equals(release.getId())) update();
			}
		};
		return actionExecutionListener;
	}

	private void update() {
		this.parent.setText(release.isRoot() || (release.getParent().isRoot()) ? messages.none() : release.getParent().getDescription());
		this.effort.setText(formatProgressText(release.getAccomplishedEffortSum(), release.getEffortSum(), " ep"));
		this.value.setText(formatProgressText(release.getAccomplishedValueSum(), release.getValueSum(), " vp"));
		final float vel = dataProvider.hasStarted() ? dataProvider.getActualVelocity() : dataProvider.getEstimatedVelocity();
		final String estimatedIndicator = " ( " + (dataProvider.hasStarted() ? roundFloat(dataProvider.getEstimatedVelocity(), 1) + " " : "")
				+ messages.planned() + " )";
		this.velocity.setText(roundFloat(vel, 1) + " ep / " + messages.day() + estimatedIndicator);
		final WorkingDay startDay = dataProvider.getEstimatedStartDay();
		final WorkingDay endDay = dataProvider.getEstimatedEndDay();
		this.period.setText(format(startDay) + " - " + format(endDay));
		this.duration.setText(HumanDateFormatter.getDifferenceText(endDay.getJavaDate().getTime() - startDay.getJavaDate().getTime(), 1));
	}

	private String format(final WorkingDay day) {
		return day.getDayMonthShortYearString();
	}

	private String formatProgressText(final float accomplished, final float total, final String unit) {
		if (total == 0) return format(total) + unit;
		final String percentage = accomplished == 0 ? "" : (" ( " + format(accomplished * 100 / total) + "% )");
		return format(accomplished) + " / " + format(total) + unit + percentage;
	}

	private String format(final float floatValue) {
		return roundFloat(floatValue, 0);
	}

}
