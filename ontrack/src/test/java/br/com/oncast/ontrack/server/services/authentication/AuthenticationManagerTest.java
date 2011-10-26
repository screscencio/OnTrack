package br.com.oncast.ontrack.server.services.authentication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.oncast.ontrack.server.model.Password;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.server.services.session.SessionManager;
import br.com.oncast.ontrack.shared.model.user.User;

public class AuthenticationManagerTest {

	@Mock
	private PersistenceService persistenceServiceMock;

	@Mock
	private Password passwordMock;

	private AuthenticationManager authenticationManager;

	private final User user = new User();

	@Before
	public void before() throws NoResultFoundException, PersistenceException {
		MockitoAnnotations.initMocks(this);

		SessionManager.setCurrentHttpSession(mock(HttpSession.class));
		authenticationManager = new AuthenticationManager(persistenceServiceMock);
		configureMocksResponses();
	}

	private void configureMocksResponses() throws NoResultFoundException, PersistenceException {
		when(persistenceServiceMock.findUserByEmail(anyString())).thenReturn(user);
		when(persistenceServiceMock.findPasswordForUserId(anyInt())).thenReturn(passwordMock);
		when(passwordMock.authenticate(anyString())).thenReturn(true);
	}

	@Test
	public void authenticationShouldReturnTheCorrectUser() throws Exception {
		assertEquals(user, authenticationManager.authenticate(user.getEmail(), "password"));
	}

	@Test
	public void userShouldBeLoggedInAfterAuthentication() throws Exception {
		authenticationManager.authenticate(user.getEmail(), "password");

		assertTrue(authenticationManager.isCurrentUserLoggedIn());
	}

}
