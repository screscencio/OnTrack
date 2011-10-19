package br.com.oncast.ontrack.server.services.requestDispatch;

import br.com.oncast.ontrack.client.services.requestDispatch.CommunicationRpcService;
import br.com.oncast.ontrack.server.business.BusinessLogic;
import br.com.oncast.ontrack.server.business.ServerBusinessLogicLocator;
import br.com.oncast.ontrack.server.services.ServerServiceProvider;
import br.com.oncast.ontrack.server.services.authentication.AuthenticationManager;
import br.com.oncast.ontrack.shared.exceptions.authentication.IncorrectPasswordException;
import br.com.oncast.ontrack.shared.exceptions.authentication.UserNotFoundException;
import br.com.oncast.ontrack.shared.exceptions.business.BusinessException;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.services.requestDispatch.AuthenticationRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.PasswordChangeRequest;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class CommunicationRpcServiceImpl extends RemoteServiceServlet implements CommunicationRpcService {

	private static final long serialVersionUID = 1L;

	private static final BusinessLogic BUSINESS = ServerBusinessLogicLocator.getInstance().getBusinessLogic();
	private static final AuthenticationManager AUTHENTICATION_MANAGER = ServerServiceProvider.getInstance().getAuthenticationManager();

	@Override
	public void transmitAction(final ModelActionSyncRequest modelActionSyncRequest) throws BusinessException {
		BUSINESS.handleIncomingActionSyncRequest(modelActionSyncRequest);
	}

	@Override
	public Project loadProject() throws BusinessException {
		return BUSINESS.loadProject();
	}

	@Override
	public User autheticateUser(final AuthenticationRequest authenticationRequest) throws UserNotFoundException, IncorrectPasswordException {
		return AUTHENTICATION_MANAGER.authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());
	}

	@Override
	public Boolean isCurrentUserAuthenticated() {
		return AUTHENTICATION_MANAGER.isCurrentUserLoggedIn();
	}

	@Override
	public void passwordChangeForUser(final PasswordChangeRequest passwordChangeRequest) throws UserNotFoundException, IncorrectPasswordException {
		AUTHENTICATION_MANAGER.changePasswordForUser(passwordChangeRequest.getEmail(), passwordChangeRequest.getOldPassword(),
				passwordChangeRequest.getNewPassword());
	}

	@Override
	public void logoutUser() {
		AUTHENTICATION_MANAGER.logout();
	}
}
