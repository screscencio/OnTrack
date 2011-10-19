package br.com.oncast.ontrack.client.services.requestDispatch;

import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.services.requestDispatch.AuthenticationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.PasswordChangeRequest;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface CommunicationRpcServiceAsync {

	void transmitAction(ModelActionSyncRequest modelActionSyncRequest, AsyncCallback<Void> callback);

	void loadProject(AsyncCallback<Project> callback);

	void autheticateUser(AuthenticationRequest authenticationRequest, AsyncCallback<User> asyncCallback);

	void isCurrentUserAuthenticated(AsyncCallback<Boolean> callback);

	void passwordChangeForUser(PasswordChangeRequest passwordChangeRequest, AsyncCallback<Void> callback);

	void logoutUser(AsyncCallback<Void> asyncCallback);
}
