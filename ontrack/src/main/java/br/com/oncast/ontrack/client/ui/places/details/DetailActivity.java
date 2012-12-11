package br.com.oncast.ontrack.client.ui.places.details;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.places.ApplicationPlaceController;
import br.com.oncast.ontrack.client.ui.components.annotations.AnnotationsPanel;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupCloseListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupOpenListener;
import br.com.oncast.ontrack.client.ui.keyeventhandler.ShortcutService;
import br.com.oncast.ontrack.client.ui.places.UndoRedoShortCutMapping;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningPlace;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class DetailActivity extends AbstractActivity {

	private AnnotationsPanel detailPanel;
	private HandlerRegistration register;
	private final DetailPlace place;

	public DetailActivity(final DetailPlace place) {
		this.place = place;
		ClientServiceProvider.getInstance().getClientMetricService().onBrowserLoadStart();
	}

	public void start() {
		if (!setDetailPanel(place)) return;
		if (!place.hasLoadedPlace()) register = ShortcutService.register(detailPanel, ClientServiceProvider.getInstance().getActionExecutionService(),
				UndoRedoShortCutMapping.values());

		PopupConfig.configPopup().popup(this.detailPanel).onClose(new PopupCloseListener() {
			@Override
			public void onHasClosed() {
				getApplicationPlaceController().goTo(place.getDestinationPlace());
				detailPanel.unregisterActionExecutionListener();
			}

		}).onOpen(new PopupOpenListener() {
			@Override
			public void onWillOpen() {
				ClientServiceProvider.getInstance().getClientMetricService().onBrowserLoadEnd();
				detailPanel.registerActionExecutionListener();
			}
		}).setModal(true).pop();
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		start();
	}

	@Override
	public void onStop() {
		if (register != null) register.removeHandler();
	}

	private boolean setDetailPanel(final DetailPlace place) {
		final ClientServiceProvider provider = ClientServiceProvider.getInstance();
		final ProjectContext context = provider.getContextProviderService().getProjectContext(place.getRequestedProjectId());

		detailPanel = null;
		try {
			detailPanel = AnnotationsPanel.forScope(context.findScope(place.getSubjectId()));
		}
		catch (final ScopeNotFoundException e) {
			try {
				detailPanel = AnnotationsPanel.forRelease(context.findRelease(place.getSubjectId()));
			}
			catch (final ReleaseNotFoundException e1) {
				provider.getClientAlertingService().showError(ClientServiceProvider.getInstance().getClientErrorMessages().errorShowingDetails());
				getApplicationPlaceController().goTo(new PlanningPlace(place.getRequestedProjectId()));
				return false;
			}
		}
		return true;
	}

	private ApplicationPlaceController getApplicationPlaceController() {
		return ClientServiceProvider.getInstance().getApplicationPlaceController();
	}

}
