package br.com.oncast.ontrack.client.services.requestDispatch;

import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.services.requestDispatch.AuthenticationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.PasswordChangeRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextRequest;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

// TODO +++Implement/Refactor dispatch method to receive generic "Requests" and asynchronously return "Responses".
// TODO Provide a centralized exception handling mechanism in which you can register exception handlers.
public class RequestDispatchServiceImpl implements RequestDispatchService {

	final CommunicationRpcServiceAsync rpcServiceAsync = GWT.create(CommunicationRpcService.class);

	@Override
	public void dispatch(final ProjectContextRequest projectContextRequest, final DispatchCallback<ProjectContext> dispatchCallback) {
		rpcServiceAsync.loadProject(new AsyncCallback<Project>() {

			@Override
			public void onSuccess(final Project project) {
				dispatchCallback.onRequestCompletition(new ProjectContext(project));
			}

			@Override
			public void onFailure(final Throwable caught) {
				dispatchCallback.onFailure(caught);
				caught.printStackTrace();
			}
		});
	}

	@Override
	public void dispatch(final ModelActionSyncRequest modelActionSyncRequest, final DispatchCallback<Void> dispatchCallback) {
		rpcServiceAsync.transmitAction(modelActionSyncRequest, new AsyncCallback<Void>() {

			@Override
			public void onSuccess(final Void result) {
				dispatchCallback.onRequestCompletition(result);
			}

			@Override
			public void onFailure(final Throwable caught) {
				dispatchCallback.onFailure(caught);
				caught.printStackTrace();
			}
		});
	}

	@Override
	public void dispatch(final AuthenticationRequest authenticationRequest, final DispatchCallback<User> dispatchCallback) {
		rpcServiceAsync.autheticateUser(authenticationRequest, new AsyncCallback<User>() {
			@Override
			public void onSuccess(final User result) {
				dispatchCallback.onRequestCompletition(result);
			}

			@Override
			public void onFailure(final Throwable caught) {
				dispatchCallback.onFailure(caught);
			}
		});
	}

	@Override
	public void isCurrentUserAuthenticated(final DispatchCallback<Boolean> dispatchCallback) {
		rpcServiceAsync.isCurrentUserAuthenticated(new AsyncCallback<Boolean>() {
			@Override
			public void onSuccess(final Boolean result) {
				dispatchCallback.onRequestCompletition(result);
			}

			@Override
			public void onFailure(final Throwable caught) {
				dispatchCallback.onFailure(caught);
			}
		});
	}

	@Override
	public void dispatch(final PasswordChangeRequest passwordChangeRequest, final DispatchCallback<Void> dispatchCallback) {
		rpcServiceAsync.passwordChangeForUser(passwordChangeRequest, new AsyncCallback<Void>() {
			@Override
			public void onSuccess(final Void result) {
				dispatchCallback.onRequestCompletition(result);
			}

			@Override
			public void onFailure(final Throwable caught) {
				dispatchCallback.onFailure(caught);
			}
		});
	}

	@Override
	public void dispatch(final DispatchCallback<Void> dispatchCallback) {
		rpcServiceAsync.logoutUser(new AsyncCallback<Void>() {
			@Override
			public void onSuccess(final Void result) {
				dispatchCallback.onRequestCompletition(result);
			}

			@Override
			public void onFailure(final Throwable caught) {
				dispatchCallback.onFailure(caught);
				caught.printStackTrace();
			}
		});
	}
}
