package br.com.oncast.ontrack.client.services.requestDispatch;

import br.com.oncast.ontrack.shared.exceptions.authentication.IncorrectPasswordException;
import br.com.oncast.ontrack.shared.exceptions.authentication.UserNotFoundException;
import br.com.oncast.ontrack.shared.exceptions.business.BusinessException;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.services.requestDispatch.AuthenticationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.PasswordChangeRequest;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("requestDispatchService")
public interface CommunicationRpcService extends RemoteService {

	void transmitAction(ModelActionSyncRequest modelActionSyncRequest) throws BusinessException;

	public Project loadProject() throws BusinessException;

	User autheticateUser(AuthenticationRequest authenticationRequest) throws UserNotFoundException, IncorrectPasswordException;

	Boolean isCurrentUserAuthenticated();

	void passwordChangeForUser(PasswordChangeRequest passwordChangeRequest) throws UserNotFoundException, IncorrectPasswordException;

	void logoutUser();
}
