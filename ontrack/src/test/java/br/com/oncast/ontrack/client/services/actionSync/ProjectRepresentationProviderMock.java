package br.com.oncast.ontrack.client.services.actionSync;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.client.services.context.ProjectRepresentationProviderImpl;
import br.com.oncast.ontrack.client.services.requestDispatch.DispatchCallback;
import br.com.oncast.ontrack.client.services.requestDispatch.RequestDispatchService;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientService;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushEventHandler;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectCreationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectListRequest;
import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;

public class ProjectRepresentationProviderMock extends ProjectRepresentationProviderImpl {

	public ProjectRepresentationProviderMock() {
		super(new RequestDispatchService() {

			@Override
			public void dispatch(final ProjectListRequest projectListRequest, final DispatchCallback<List<ProjectRepresentation>> dispatchCallback) {
				dispatchCallback.onRequestCompletition(new ArrayList<ProjectRepresentation>());
			}

			@Override
			public void dispatch(final ProjectCreationRequest projectCreationRequest, final DispatchCallback<ProjectRepresentation> dispatchCallback) {
				throw new RuntimeException("This method should not be called.");
			}

			@Override
			public void dispatch(final ModelActionSyncRequest modelActionSyncRequest, final DispatchCallback<Void> dispatchCallback) {
				throw new RuntimeException("This method should not be called.");
			}

			@Override
			public void dispatch(final ProjectContextRequest projectContextRequest, final DispatchCallback<ProjectContext> dispatchCallback) {
				throw new RuntimeException("This method should not be called.");
			}
		},
				new ServerPushClientService() {

					@Override
					public <T extends ServerPushEvent> void registerServerEventHandler(final Class<T> eventClass,
							final ServerPushEventHandler<T> serverPushEventHandler) {}
				});
	}

	@Override
	public void setProjectRepresentation(final ProjectRepresentation projectRepresentation) {
		super.setProjectRepresentation(projectRepresentation);
	}

}