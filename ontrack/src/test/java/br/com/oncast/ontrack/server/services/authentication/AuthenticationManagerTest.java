package br.com.oncast.ontrack.server.services.authentication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.com.oncast.ontrack.server.model.Password;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.server.services.session.SessionManager;
import br.com.oncast.ontrack.shared.exceptions.authentication.AuthenticationException;
import br.com.oncast.ontrack.shared.exceptions.authentication.IncorrectPasswordException;
import br.com.oncast.ontrack.shared.exceptions.authentication.UserNotFoundException;
import br.com.oncast.ontrack.shared.model.user.User;

public class AuthenticationManagerTest {

	@Mock
	private PersistenceService persistenceServiceMock;

	@Mock
	private Password passwordMock;

	private AuthenticationManager authenticationManager;

	private User user;

	@Before
	public void before() throws NoResultFoundException, PersistenceException {
		MockitoAnnotations.initMocks(this);

		SessionManager.setCurrentHttpSession(mock(HttpSession.class));
		authenticationManager = new AuthenticationManager(persistenceServiceMock);

		configureUser();
		setDefaultMockBehavior();
	}

	private void configureUser() {
		user = new User();
		user.setId(123456);
		user.setEmail("user@mail.com");
	}

	private void setDefaultMockBehavior() throws NoResultFoundException, PersistenceException {
		when(persistenceServiceMock.findUserByEmail(anyString())).thenReturn(user);
		when(persistenceServiceMock.findPasswordForUser(anyInt())).thenReturn(passwordMock);
		when(passwordMock.authenticate(anyString())).thenReturn(true);
	}

	@Test
	public void authenticationShouldReturnTheCorrectUser() throws Exception {
		assertEquals(user, authenticateUser());
	}

	@Test
	public void userShouldBeLoggedInAfterAuthentication() throws Exception {
		assertUserIsNotLoggedIn();
		authenticateUser();
		assertUserIsLoggedIn();
	}

	@Test
	public void userShouldNotBeLoggedInIfAuthenticationWasNotCalled() throws Exception {
		assertUserIsNotLoggedIn();
	}

	@Test(expected = UserNotFoundException.class)
	public void shouldNotAuthenticateUserIfHisEmailIsNotRegistered() throws Exception {
		assertUserIsNotLoggedIn();
		forcePersistenceToDoNotFindUser();
		authenticateUser();
	}

	@Test(expected = IncorrectPasswordException.class)
	public void shouldNotAuthenticateUserIfPassedPasswordIsIncorrect() throws Exception {
		assertUserIsNotLoggedIn();
		forcePasswordToBeIncorrect();
		authenticateUser();
	}

	@Test(expected = AuthenticationException.class)
	public void shouldNotAuthenticateUserIfSomeProblemOccurAtPersistenceLayer() throws Exception {
		assertUserIsNotLoggedIn();
		forcePersistenceToFail();
		authenticateUser();
	}

	@Test
	public void userShouldNotBeLoggedInAfterLoggindOut() throws Exception {
		authenticateUser();
		assertUserIsLoggedIn();

		logoutUser();
		assertUserIsNotLoggedIn();
	}

	@Test
	public void logoutShouldDoNothingIfThereIsNoUserLoggedIn() throws Exception {
		assertUserIsNotLoggedIn();
		logoutUser();
		assertUserIsNotLoggedIn();
	}

	@Test
	public void shouldChangeUserPassword() throws Exception {
		authenticateUser();
		changePassword();
		assertPasswordWasChanged();
	}

	@Test(expected = IncorrectPasswordException.class)
	public void shouldNotChangeUserPasswordIfOldPasswordIsIncorrect() throws Exception {
		authenticateUser();
		forcePasswordToBeIncorrect();
		changePassword();
	}

	private void forcePersistenceToDoNotFindUser() throws NoResultFoundException, PersistenceException {
		when(persistenceServiceMock.findUserByEmail(anyString())).thenThrow(new NoResultFoundException(null, null));
	}

	private void forcePersistenceToFail() throws Exception {
		when(persistenceServiceMock.findUserByEmail(anyString())).thenThrow(new AuthenticationException());
		when(persistenceServiceMock.findPasswordForUser(anyInt())).thenThrow(new AuthenticationException());
	}

	private void forcePasswordToBeIncorrect() {
		when(passwordMock.authenticate(anyString())).thenReturn(false);
	}

	private User authenticateUser() throws UserNotFoundException, IncorrectPasswordException {
		return authenticationManager.authenticate(user.getEmail(), "password");
	}

	private void logoutUser() {
		authenticationManager.logout();
	}

	private void changePassword() throws UserNotFoundException, IncorrectPasswordException {
		authenticationManager.changePasswordForUser(user.getEmail(), "password", "new password");
	}

	private void assertUserIsNotLoggedIn() {
		assertFalse(authenticationManager.isCurrentUserLoggedIn());
	}

	private void assertUserIsLoggedIn() {
		assertTrue(authenticationManager.isCurrentUserLoggedIn());
	}

	private void assertPasswordWasChanged() {
		Mockito.verify(passwordMock).setPassword("new password");
	}

}