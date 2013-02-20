package br.com.oncast.ontrack.client.ui.places.timesheet;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class TimesheetActivity extends AbstractActivity {

	private final TimesheetPlace place;

	public TimesheetActivity(final TimesheetPlace place) {
		this.place = place;

	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		try {
			final Release release = ClientServiceProvider.getCurrentProjectContext().findRelease(place.getReleaseId());
			panel.setWidget(new TimesheetPanel(release));
		}
		catch (final ReleaseNotFoundException e) {
			// FIXME i18n
			ClientServiceProvider.getInstance().getClientAlertingService().showError("FIXME");
		}
	}

}
