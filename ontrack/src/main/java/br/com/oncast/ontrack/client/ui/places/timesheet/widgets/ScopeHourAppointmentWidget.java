package br.com.oncast.ontrack.client.ui.places.timesheet.widgets;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.generalwidgets.EditableLabel;
import br.com.oncast.ontrack.client.ui.generalwidgets.EditableLabelEditionHandler;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.utils.WorkingDay;
import br.com.oncast.ontrack.shared.utils.WorkingDayFactory;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;

public class ScopeHourAppointmentWidget implements IsWidget {

	private static final float DEFAULT_DAILLY_WORKING_HOUR = 8;

	private float appointedHour = 0;

	EditableLabel editableLabel;

	public ScopeHourAppointmentWidget(final Scope scope, final UserRepresentation user) {
		editableLabel = new EditableLabel(new EditableLabelEditionHandler() {
			@Override
			public boolean onEditionRequest(final String text) {
				try {
					final Float newAppointment = Float.valueOf(text);
					if (newAppointment.equals(appointedHour)) return false;

					setAppointedHour(newAppointment);
					return true;
				}
				catch (final NumberFormatException e) {
					// FIXME i18n
					ClientServiceProvider.getInstance().getClientAlertingService().showWarning("should be float");
					return false;
				}
			}
		});
		editableLabel.setWidth("40px");
		editableLabel.getElement().getStyle().setProperty("textAlign", "center");

		setAppointedHour(extractHour(scope));
		editableLabel.setReadOnly(!ClientServiceProvider.getCurrentUser().equals(user.getId()));
	}

	private float extractHour(final Scope scope) {
		final Progress p = scope.getProgress();
		final WorkingDay endDay = p.isDone() ? p.getEndDay() : WorkingDayFactory.create();
		final WorkingDay startDay = p.getStartDay();
		return startDay == null ? 0 : startDay.countTo(endDay) * DEFAULT_DAILLY_WORKING_HOUR;
	}

	private void setAppointedHour(final float appointedHour) {
		this.appointedHour = appointedHour;
		editableLabel.setValue("" + appointedHour);
	}

	public float getAppointedHour() {
		return appointedHour;
	}

	@Override
	public Widget asWidget() {
		return editableLabel;
	}

}
