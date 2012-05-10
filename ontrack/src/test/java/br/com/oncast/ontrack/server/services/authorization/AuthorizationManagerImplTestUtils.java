package br.com.oncast.ontrack.server.services.authorization;

import static org.mockito.Mockito.mock;
import br.com.oncast.ontrack.server.services.authentication.AuthenticationManager;
import br.com.oncast.ontrack.server.services.email.ProjectAuthorizationMailFactory;
import br.com.oncast.ontrack.server.services.notification.NotificationService;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;

public class AuthorizationManagerImplTestUtils {

	private static NotificationService notificationMock;

	static {
		notificationMock = mock(NotificationService.class);
	}

	public static AuthorizationManagerImpl create(final PersistenceService persistence, final AuthenticationManager authenticationManager,
			final ProjectAuthorizationMailFactory mailFactory) {
		return new AuthorizationManagerImpl(authenticationManager, persistence, notificationMock, mailFactory);
	}

	public static AuthorizationManagerImpl create(final PersistenceService persistence, final AuthenticationManager authenticationManager,
			final ProjectAuthorizationMailFactory mailFactory, final NotificationService notificationService) {
		return new AuthorizationManagerImpl(authenticationManager, persistence, notificationService, mailFactory);
	}

}
