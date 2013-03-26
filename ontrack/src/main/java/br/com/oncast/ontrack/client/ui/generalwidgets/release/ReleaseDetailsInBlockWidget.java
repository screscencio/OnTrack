package br.com.oncast.ontrack.client.ui.generalwidgets.release;

import java.util.Date;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.generalwidgets.InformationBlockWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.client.utils.date.HumanDateFormatter;
import br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.ReleaseEstimator;
import br.com.oncast.ontrack.shared.utils.WorkingDay;
import br.com.oncast.ontrack.shared.utils.WorkingDayFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ReleaseDetailsInBlockWidget extends Composite implements ModelWidget<Release> {

	private static ReleaseDetailsInBlockWidgetUiBinder uiBinder = GWT.create(ReleaseDetailsInBlockWidgetUiBinder.class);

	interface ReleaseDetailsInBlockWidgetUiBinder extends UiBinder<Widget, ReleaseDetailsInBlockWidget> {}

	@UiField
	InformationBlockWidget effort;

	@UiField
	InformationBlockWidget value;

	@UiField
	InformationBlockWidget estimatedSpeed;

	@UiField
	InformationBlockWidget actualSpeed;

	@UiField
	InformationBlockWidget startDay;

	@UiField
	InformationBlockWidget endDay;

	@UiField
	InformationBlockWidget duration;

	@UiField
	InformationBlockWidget cycletime;

	@UiField
	InformationBlockWidget leadtime;

	private Release release;

	public ReleaseDetailsInBlockWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public ReleaseDetailsInBlockWidget(final Release release) {
		this();
		setRelease(release);
	}

	@Override
	public boolean update() {
		effort.setValue(round(release.getEffortSum()));
		value.setValue(round(release.getValueSum()));
		estimatedSpeed.setValue(round(getEstimator().getEstimatedVelocity(release)));
		actualSpeed.setValue(release.getActualVelocity());

		startDay.setValue(getEstimator().getEstimatedStartDayFor(release));
		endDay.setValue(getEstimator().getEstimatedEndDayFor(release));

		formatTimeDifference(cycletime, release.getAverageCycleTime());
		formatTimeDifference(leadtime, release.getAverageLeadTime());
		updateDuration();

		return false;
	}

	private void updateDuration() {
		final float effortSum = release.getEffortSum();
		final float estimatedVelocity = getEstimator().getEstimatedVelocity(release);
		if (effortSum < 1) {
			duration.setAsNull();
			return;
		}

		final int days = Math.round(effortSum / estimatedVelocity);
		final Date date = new Date();
		final WorkingDay workingDay = WorkingDayFactory.create(date);
		workingDay.add(days);
		formatTimeDifference(duration, workingDay.getJavaDate().getTime() - date.getTime());
	}

	private void formatTimeDifference(final InformationBlockWidget widget, final Long difference) {
		if (difference == null) {
			widget.setAsNull();
			return;
		}

		// FIXME LOBO REmove the split workaround.
		final String differenceText[] = HumanDateFormatter.getDifferenceText(difference, 1).split(" ");
		widget.setValue(differenceText[0]);
		widget.setPosfix(differenceText[1]);
	}

	@Override
	public Release getModelObject() {
		return release;
	}

	public ReleaseDetailsInBlockWidget setRelease(final Release release) {
		if (this.release != null && this.release.equals(release)) return this;

		this.release = release;
		update();
		return this;
	}

	private ReleaseEstimator getEstimator() {
		return ClientServiceProvider.getInstance().getReleaseEstimatorProvider().get();
	}

	private String round(final float number) {
		return ClientDecimalFormat.roundFloat(number, 1);
	}

}
