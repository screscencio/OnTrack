package br.com.oncast.ontrack.server.services.authentication.basic;

import br.com.oncast.ontrack.server.services.authentication.BasicRequestAuthenticator;
import br.com.oncast.ontrack.server.services.authentication.DefaultAuthenticationCredentials;
import br.com.oncast.ontrack.shared.exceptions.authentication.AuthenticationException;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.anyString;

import static org.mockito.Mockito.when;

public class BasicAuthenticatorTest {

	@Mock
	private HttpServletRequest request;

	private final String USER = DefaultAuthenticationCredentials.USER_EMAIL;
	private final String PASSWORD = DefaultAuthenticationCredentials.USER_PASSWORD;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test(expected = AuthenticationException.class)
	public void requestWithoutAuthenticationInformationShouldThrowException() {
		when(request.getHeader(anyString())).thenReturn(null);
		BasicRequestAuthenticator.authenticate(request);
	}

	@Test(expected = AuthenticationException.class)
	public void requestWithoutPasswordShouldThrowException() {
		doRequestWithCredentials("user");
		BasicRequestAuthenticator.authenticate(request);
	}

	@Test(expected = AuthenticationException.class)
	public void shouldFailAuthenticationWhenInformedUserIsNotRegistered() {
		doRequestWithCredentials("unregistered" + ":" + PASSWORD);
		BasicRequestAuthenticator.authenticate(request);
	}

	@Test(expected = AuthenticationException.class)
	public void shouldFailAuthenticationWhenInformedPasswordIsNotCorrect() {
		when(request.getHeader(anyString())).thenReturn(mountBasicCredentials(USER + ":" + "incorrect"));
		BasicRequestAuthenticator.authenticate(request);
	}

	@Test
	public void authenticationShouldPassForValidCredentials() {
		doRequestWithCredentials(USER + ":" + PASSWORD);
		BasicRequestAuthenticator.authenticate(request);
	}

	private void doRequestWithCredentials(final String credentials) {
		when(request.getHeader(anyString())).thenReturn(mountBasicCredentials(credentials));
	}

	private String mountBasicCredentials(final String credentials) {
		return "Basic " + new String(Base64.encodeBase64(credentials.getBytes()));
	}

}
