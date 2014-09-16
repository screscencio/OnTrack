package br.com.oncast.ontrack.server.services.authorization;

import br.com.oncast.ontrack.server.services.authentication.AuthenticationManager;
import br.com.oncast.ontrack.server.services.email.MailService;
import br.com.oncast.ontrack.server.services.integration.IntegrationService;
import br.com.oncast.ontrack.server.services.multicast.ClientManager;
import br.com.oncast.ontrack.server.services.multicast.MulticastService;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;

import static org.mockito.Mockito.mock;

public class AuthorizationManagerImplTestUtils {

	private static MulticastService multicastMock;
	private static ClientManager clientManager;
	private static IntegrationService integrationMock;

	static {
		multicastMock = mock(MulticastService.class);
		clientManager = mock(ClientManager.class);
		integrationMock = mock(IntegrationService.class);
	}

	public static AuthorizationManagerImpl create(final PersistenceService persistence, final AuthenticationManager authenticationManager, final MailService mailService) {
		return new AuthorizationManagerImpl(authenticationManager, persistence, multicastMock, mailService, clientManager, integrationMock);
	}

	public static AuthorizationManagerImpl create(final PersistenceService persistence, final AuthenticationManager authenticationManager, final MailService mailService,
			final MulticastService multicastService) {
		return new AuthorizationManagerImpl(authenticationManager, persistence, multicastService, mailService, clientManager, integrationMock);
	}

	public static AuthorizationManagerImpl create(final PersistenceService persistence, final AuthenticationManager authenticationManager, final MailService mailService,
			final IntegrationService integrationService) {
		return new AuthorizationManagerImpl(authenticationManager, persistence, multicastMock, mailService, clientManager, integrationService);
	}

}
