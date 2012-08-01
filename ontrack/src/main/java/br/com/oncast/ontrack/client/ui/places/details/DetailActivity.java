package br.com.oncast.ontrack.client.ui.places.details;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.places.ApplicationPlaceController;
import br.com.oncast.ontrack.client.ui.components.annotations.AnnotationsPanel;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupCloseListener;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningPlace;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class DetailActivity extends AbstractActivity {

	private AnnotationsPanel detailPanel;
	private final Place previousPlace;

	public DetailActivity(final DetailPlace place) {
		this.previousPlace = place.getDestinationPlace();

		setDetailPanel(place);
	}

	@Override
	public void start(final AcceptsOneWidget panel, final EventBus eventBus) {
		PopupConfig.configPopup().popup(this.detailPanel).onClose(new PopupCloseListener() {
			@Override
			public void onHasClosed() {
				getApplicationPlaceController().goTo(previousPlace);
			}

		}).setModal(true).pop();
	}

	private void setDetailPanel(final DetailPlace place) {
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
				provider.getClientNotificationService().showError("It was not possible to show details: The given id was not found");
				getApplicationPlaceController().goTo(new PlanningPlace(place.getRequestedProjectId()));
			}
		}
	}

	private ApplicationPlaceController getApplicationPlaceController() {
		return ClientServiceProvider.getInstance().getApplicationPlaceController();
	}

}
