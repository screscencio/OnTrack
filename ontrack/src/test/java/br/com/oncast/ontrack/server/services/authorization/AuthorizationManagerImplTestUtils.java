package br.com.oncast.ontrack.server.services.authorization;

import static org.mockito.Mockito.mock;
import br.com.oncast.ontrack.server.services.authentication.AuthenticationManager;
import br.com.oncast.ontrack.server.services.email.ProjectAuthorizationMailFactory;
import br.com.oncast.ontrack.server.services.multicast.MulticastService;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;

public class AuthorizationManagerImplTestUtils {

	private static MulticastService multicastMock;

	static {
		multicastMock = mock(MulticastService.class);
	}

	public static AuthorizationManagerImpl create(final PersistenceService persistence, final AuthenticationManager authenticationManager,
			final ProjectAuthorizationMailFactory mailFactory) {
		return new AuthorizationManagerImpl(authenticationManager, persistence, multicastMock, mailFactory);
	}

	public static AuthorizationManagerImpl create(final PersistenceService persistence, final AuthenticationManager authenticationManager,
			final ProjectAuthorizationMailFactory mailFactory, final MulticastService multicastService) {
		return new AuthorizationManagerImpl(authenticationManager, persistence, multicastService, mailFactory);
	}

}
