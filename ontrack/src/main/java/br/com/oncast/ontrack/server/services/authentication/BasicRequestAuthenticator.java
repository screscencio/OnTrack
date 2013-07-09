package br.com.oncast.ontrack.server.services.authentication;

import br.com.oncast.ontrack.shared.exceptions.authentication.AuthenticationException;

import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

public class BasicRequestAuthenticator {

	public static void authenticate(final HttpServletRequest request) {
		authenticate(request, DefaultAuthenticationCredentials.USER_EMAIL, DefaultAuthenticationCredentials.USER_PASSWORD);
	}

	public static void authenticate(final HttpServletRequest request, final String user, final String password) {
		checkCredentials(extractCredentials(request), user, password);
	}

	public static String[] extractCredentials(final HttpServletRequest request) {
		final String auth = request.getHeader("Authorization");
		if (auth == null) throw new AuthenticationException();

		return parseCredentials(auth);
	}

	private static void checkCredentials(final String[] credentials, final String user, final String password) {
		assert credentials.length == 2;
		if (!verify(user, credentials[0]) || !verify(password, credentials[1])) throw new AuthenticationException();
	}

	private static String[] parseCredentials(final String auth) {
		final String[] credentials = StringUtils.split(new String(Base64.decodeBase64(auth.substring(auth.indexOf(' ')).getBytes()), Charset.forName("UTF-8")), ':');
		if (credentials.length < 2) throw new AuthenticationException("Password not informed.");
		return credentials;
	}

	private static boolean verify(final String expected, final String obtained) {
		return obtained.equals(expected);
	}

}
