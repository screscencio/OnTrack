package br.com.oncast.ontrack.client.ui.places.timesheet;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.places.ApplicationPlaceController;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupCloseListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupOpenListener;
import br.com.oncast.ontrack.client.ui.keyeventhandler.ShortcutService;
import br.com.oncast.ontrack.client.ui.places.UndoRedoShortCutMapping;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class TimesheetActivity extends AbstractActivity {

	private static final TimesheetMessages MESSAGES = GWT.create(TimesheetMessages.class);

	private static TimesheetPanel timesheetPanel;
	private static PopupConfig popupConfig;

	private final TimesheetPlace place;
	private HandlerRegistration register;

	public TimesheetActivity(final TimesheetPlace place) {
		this.place = place;
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		start();
	}

	public void start() {
		try {
			final ProjectContext projectContext = ClientServiceProvider.getCurrentProjectContext();
			final Release release = projectContext.findRelease(place.getReleaseId());
			getTimesheetPanel().setRelease(release, projectContext);

			if (!place.hasLoadedPlace()) register = ShortcutService.register(timesheetPanel, ClientServiceProvider.getInstance().getActionExecutionService(),
					UndoRedoShortCutMapping.values());

			getPopupConfig().pop();
		}
		catch (final ReleaseNotFoundException e) {
			ClientServiceProvider.getInstance().getClientAlertingService().showError(MESSAGES.theRequestingReleaseWasNotFound());
		}
	}

	private TimesheetPanel getTimesheetPanel() {
		if (timesheetPanel != null) return timesheetPanel;
		return timesheetPanel = new TimesheetPanel();
	}

	private PopupConfig getPopupConfig() {
		if (popupConfig != null) return popupConfig;

		return popupConfig = PopupConfig.configPopup().popup(timesheetPanel).onClose(new PopupCloseListener() {
			@Override
			public void onHasClosed() {
				getApplicationPlaceController().goTo(place.getDestinationPlace());
				timesheetPanel.unregisterActionExecutionListener();
			}

		}).onOpen(new PopupOpenListener() {
			@Override
			public void onWillOpen() {
				timesheetPanel.registerActionExecutionListener();
			}
		}).setModal(true);
	}

	@Override
	public void onStop() {
		if (register != null) register.removeHandler();
	}

	private ApplicationPlaceController getApplicationPlaceController() {
		return ClientServiceProvider.getInstance().getApplicationPlaceController();
	}

}
