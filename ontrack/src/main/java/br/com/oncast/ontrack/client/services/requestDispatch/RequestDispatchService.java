package br.com.oncast.ontrack.client.services.requestDispatch;

import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.services.requestDispatch.AuthenticationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.PasswordChangeRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextRequest;

public interface RequestDispatchService {

	public void dispatch(final ProjectContextRequest projectContextRequest, final DispatchCallback<ProjectContext> dispatchCallback);

	public void dispatch(final ModelActionSyncRequest modelActionSyncRequest, final DispatchCallback<Void> dispatchCallback);

	public void dispatch(final AuthenticationRequest authenticationRequest, final DispatchCallback<User> dispatchCallback);

	public void dispatch(final PasswordChangeRequest passwordChangeRequest, final DispatchCallback<Void> dispatchCallback);

	public void dispatch(final DispatchCallback<Void> dispatchCallback);

	public void isCurrentUserAuthenticated(final DispatchCallback<Boolean> dispatchCallback);

}