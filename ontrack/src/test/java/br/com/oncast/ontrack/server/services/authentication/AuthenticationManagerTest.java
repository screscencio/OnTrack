package br.com.oncast.ontrack.server.services.authentication;

import br.com.oncast.ontrack.server.services.email.MailFactory;
import br.com.oncast.ontrack.server.services.metrics.ServerAnalytics;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.exceptions.NoResultFoundException;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.server.services.session.SessionManager;
import br.com.oncast.ontrack.shared.config.RequestConfigurations;
import br.com.oncast.ontrack.shared.exceptions.authentication.AuthenticationException;
import br.com.oncast.ontrack.shared.exceptions.authentication.InvalidAuthenticationCredentialsException;
import br.com.oncast.ontrack.shared.exceptions.authentication.UserNotFoundException;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.ibm.icu.util.Calendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthenticationManagerTest {

	@Mock
	private PersistenceService persistenceServiceMock;

	@Mock
	private Password passwordMock;

	@Mock
	private MailFactory mailFactory;

	@Mock
	private ServerAnalytics serverAnalytics;

	private List<Password> userPasswords;

	private AuthenticationManager authenticationManager;

	private User user;

	private SessionManager sessionManager;

	@Before
	public void before() throws NoResultFoundException, PersistenceException {
		MockitoAnnotations.initMocks(this);

		sessionManager = new SessionManager();

		final HttpSession httpSessionMock = mock(HttpSession.class);
		final HttpServletRequest requestMock = mock(HttpServletRequest.class);
		when(requestMock.getSession()).thenReturn(httpSessionMock);
		when(requestMock.getHeader(RequestConfigurations.CLIENT_IDENTIFICATION_PARAMETER_NAME)).thenReturn("fakeClientId");

		sessionManager.configureCurrentHttpSession(requestMock);
		authenticationManager = new AuthenticationManager(persistenceServiceMock, sessionManager, mailFactory, serverAnalytics);

		userPasswords = new ArrayList<Password>();
		userPasswords.add(passwordMock);

		configureUser();
		setDefaultMockBehavior();

	}

	private void configureUser() {
		user = new User(new UUID(), "user@mail.com");
	}

	private void setDefaultMockBehavior() throws NoResultFoundException, PersistenceException {
		when(persistenceServiceMock.retrieveUserByEmail(anyString())).thenReturn(user);
		when(persistenceServiceMock.retrievePasswordsForUser(any(UUID.class))).thenReturn(userPasswords);
		// when(persistenceServicethen.new Answer<List<Password>>() {}ID.class))).then(new Answer<List<Password>>() {
		//
		// @Override
		// public List<Password> answer(final InvocationOnMock arg0) throws Throwable {
		// // FIXME Auto-generated catch block
		// return null;
		// }});
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

	@Test(expected = InvalidAuthenticationCredentialsException.class)
	public void shouldNotAuthenticateUserIfHisEmailIsNotRegistered() throws Exception {
		assertUserIsNotLoggedIn();
		forcePersistenceToDoNotFindUser();
		authenticateUser();
	}

	@Test(expected = InvalidAuthenticationCredentialsException.class)
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

	@Test(expected = InvalidAuthenticationCredentialsException.class)
	public void shouldNotChangeUserPasswordIfOldPasswordIsIncorrect() throws Exception {
		authenticateUser();
		forcePasswordToBeIncorrect();
		changePassword();
	}

	@Test
	public void notifyListenersAfterLogin() throws Exception {
		final AuthenticationListener listener = mock(AuthenticationListener.class);
		authenticationManager.register(listener);
		authenticationManager.authenticate("email", "password");

		verify(listener).onUserLoggedIn(Mockito.same(user), (String) Mockito.any());
	}

	@Test
	public void notifyListenersAfterLogout() throws Exception {
		authenticationManager.authenticate("email", "password");

		final AuthenticationListener listener = mock(AuthenticationListener.class);
		authenticationManager.register(listener);
		authenticationManager.logout();

		verify(listener).onUserLoggedOut(Mockito.same(user), (String) Mockito.any());
	}

	@Test
	public void unregisteredListenersShouldNotBeNotifiedAfterLogin() throws Exception {
		final AuthenticationListener listener = mock(AuthenticationListener.class);

		authenticationManager.register(listener);
		authenticationManager.authenticate("email", "password");

		authenticationManager.unregister(listener);
		authenticationManager.authenticate("email", "password");

		verify(listener, times(1)).onUserLoggedIn(Mockito.same(user), (String) Mockito.any());
	}

	@Test
	public void unregisteredListenersShouldNotBeNotifiedAfterLogout() throws Exception {
		authenticationManager.authenticate("email", "password");

		final AuthenticationListener listener = mock(AuthenticationListener.class);
		authenticationManager.register(listener);
		authenticationManager.logout();

		authenticationManager.unregister(listener);
		authenticationManager.logout();

		verify(listener, times(1)).onUserLoggedOut(Mockito.same(user), (String) Mockito.any());
	}

	@Test(expected = AuthenticationException.class)
	public void invalidAthenticationTokenShouldThrowAuthenticationError() throws Exception {
		authenticationManager.authenticateByToken("invalid");
	}

	@Test(expected = AuthenticationException.class)
	public void usersOlderThanAWeekWihtoutPasswordShouldNotBeAuthenticated() throws Exception {
		final User olderUser = userWithAge(-8);
		userPasswords.clear();
		authenticationManager.authenticateByToken(olderUser.getId().toString());
	}

	@Test(expected = AuthenticationException.class)
	public void usersOlderThanAWeekShouldNotBeAuthenticatedEvenWithPassword() throws Exception {
		final User olderUser = userWithAge(-8);
		authenticationManager.authenticateByToken(olderUser.getId().toString());
	}

	@Test(expected = AuthenticationException.class)
	public void usersWithPasswordShouldNotBeAuthenticatedWithToken() throws Exception {
		authenticationManager.authenticateByToken(user.getId().toString());
	}

	@Test
	public void usersWithoutPasswordAndYoungerThanAWeekShouldBeAuthenticatedWithToken() throws Exception {
		final User youngerUser = userWithAge(-6);
		userPasswords.clear();
		authenticationManager.authenticateByToken(youngerUser.getId().toString());
		assertTrue(authenticationManager.isUserAuthenticated());
		assertEquals(youngerUser, authenticationManager.getAuthenticatedUser());
	}

	@Test
	public void usersWithoutPasswordAndCreatedTodayShouldBeAuthenticatedWithToken() throws Exception {
		final User youngerUser = userWithAge(0);
		userPasswords.clear();
		authenticationManager.authenticateByToken(youngerUser.getId().toString());
		assertTrue(authenticationManager.isUserAuthenticated());
		assertEquals(youngerUser, authenticationManager.getAuthenticatedUser());
		userPasswords.add(passwordMock);
		try {
			authenticationManager.authenticateByToken(youngerUser.getId().toString());
			fail("User should not be authenticated after setting a password");
		} catch (final AuthenticationException e) {}
	}

	private User userWithAge(final int ageInDays) throws Exception {
		final User olderUser = user;
		final Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_YEAR, ageInDays);
		olderUser.setCreationTimestamp(cal.getTime());
		when(persistenceServiceMock.retrieveUserById(olderUser.getId())).thenReturn(olderUser);
		return olderUser;
	}

	private void forcePersistenceToDoNotFindUser() throws NoResultFoundException, PersistenceException {
		when(persistenceServiceMock.retrieveUserByEmail(anyString())).thenThrow(new NoResultFoundException(null, null));
	}

	private void forcePersistenceToFail() throws Exception {
		when(persistenceServiceMock.retrieveUserByEmail(anyString())).thenThrow(new AuthenticationException());
		when(persistenceServiceMock.retrievePasswordsForUser(any(UUID.class))).thenThrow(new AuthenticationException());
	}

	private void forcePasswordToBeIncorrect() {
		when(passwordMock.authenticate(anyString())).thenReturn(false);
	}

	private User authenticateUser() throws UserNotFoundException, InvalidAuthenticationCredentialsException {
		return authenticationManager.authenticate(user.getEmail(), "password");
	}

	private void logoutUser() {
		authenticationManager.logout();
	}

	private void changePassword() throws UserNotFoundException, InvalidAuthenticationCredentialsException {
		authenticationManager.updateCurrentUserPassword("password", "new password");
	}

	private void assertUserIsNotLoggedIn() {
		assertFalse(authenticationManager.isUserAuthenticated());
	}

	private void assertUserIsLoggedIn() {
		assertTrue(authenticationManager.isUserAuthenticated());
	}

	private void assertPasswordWasChanged() {
		Mockito.verify(passwordMock).setPassword("new password");
	}

}
