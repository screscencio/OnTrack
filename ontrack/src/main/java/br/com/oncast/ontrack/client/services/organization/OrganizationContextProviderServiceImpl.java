package br.com.oncast.ontrack.client.services.organization;

import java.util.HashSet;
import java.util.Set;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;
import br.com.oncast.ontrack.client.services.context.ProjectListChangeListener;
import br.com.oncast.ontrack.client.services.context.ProjectRepresentationProvider;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.services.requestDispatch.MultipleProjectContextRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.MultipleProjectContextRequestResponse;

import com.google.gwt.event.shared.HandlerRegistration;

public class OrganizationContextProviderServiceImpl implements OrganizationContextProviderService {

	private final DispatchService dispatchService;
	private final ProjectRepresentationProvider projectRepresentationProvider;

	private HandlerRegistration handlerRegistration;
	private final Set<ProjectContext> availableContexts;
	private final Set<ProjectRepresentation> currentProjectRepresentations;
	private final Set<AvailableContextsListChangeListener> contextListChangeListeners;

	public OrganizationContextProviderServiceImpl(final DispatchService dispatchService, final ProjectRepresentationProvider projectRepresentationProvider) {
		this.dispatchService = dispatchService;
		this.projectRepresentationProvider = projectRepresentationProvider;

		availableContexts = new HashSet<ProjectContext>();
		currentProjectRepresentations = new HashSet<ProjectRepresentation>();
		contextListChangeListeners = new HashSet<AvailableContextsListChangeListener>();
	}

	@Override
	public void registerContextsChangeListener(final AvailableContextsListChangeListener listener) {
		if (hasAvailableContexts()) listener.onContextListChange(getAvailableProjects());

		contextListChangeListeners.add(listener);

		if (handlerRegistration != null) return;

		handlerRegistration = projectRepresentationProvider.registerProjectListChangeListener(new ProjectListChangeListener() {

			@Override
			public void onProjectListChanged(final Set<ProjectRepresentation> projectRepresentations) {
				projectRepresentations.removeAll(currentProjectRepresentations);

				if (projectRepresentations.isEmpty()) return;

				dispatchService.dispatch(new MultipleProjectContextRequest(projectRepresentations),
						new DispatchCallback<MultipleProjectContextRequestResponse>() {

							@Override
							public void onSuccess(final MultipleProjectContextRequestResponse result) {
								availableContexts.addAll(result.getProjects());
								currentProjectRepresentations.addAll(projectRepresentations);

								notifyAvailableContextListChangeListers();
							}

							@Override
							public void onTreatedFailure(final Throwable caught) {}

							@Override
							public void onUntreatedFailure(final Throwable caught) {
								caught.printStackTrace();
							}
						});
			}

			@Override
			public void onProjectListAvailabilityChange(final boolean availability) {
				availableContexts.clear();
			}
		});
	}

	private boolean hasAvailableContexts() {
		return !availableContexts.isEmpty();
	}

	private Set<ProjectContext> getAvailableProjects() {
		return availableContexts;
	}

	private void notifyAvailableContextListChangeListers() {
		for (final AvailableContextsListChangeListener listener : contextListChangeListeners) {
			listener.onContextListChange(getAvailableProjects());
		}
	}

}
