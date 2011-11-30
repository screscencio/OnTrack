package br.com.oncast.ontrack.client.services.actionSync;

import java.util.ArrayList;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;
import br.com.drycode.api.web.gwt.dispatchService.client.FailureHandler;
import br.com.drycode.api.web.gwt.dispatchService.shared.DispatchRequest;
import br.com.oncast.ontrack.client.services.context.ProjectRepresentationProviderImpl;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientService;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushEventHandler;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectListRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectListResponse;
import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;

public class ProjectRepresentationProviderMock extends ProjectRepresentationProviderImpl {

	public ProjectRepresentationProviderMock() {
		super(new DispatchService() {

			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public void dispatch(final DispatchRequest request, final DispatchCallback dispatchCallback) {
				if (!(request instanceof ProjectListRequest)) throw new RuntimeException("The test should not try to dispatch '"
						+ request.getClass().getName()
						+ "'.");

				dispatchCallback.onSuccess(new ProjectListResponse(new ArrayList<ProjectRepresentation>()));
			}

			@Override
			public <T extends FailureHandler<R>, R extends Throwable> void addFailureHandler(final Class<R> throwableClass, final T handler) {
				throw new RuntimeException("The test should not use this method.");
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