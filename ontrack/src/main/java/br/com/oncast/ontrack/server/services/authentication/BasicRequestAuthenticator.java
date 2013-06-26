package br.com.oncast.ontrack.server.services.authentication;

import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import br.com.oncast.ontrack.shared.exceptions.authentication.AuthenticationException;

public class BasicRequestAuthenticator {

	public static void authenticate(final HttpServletRequest request) {
		authenticate(request, DefaultAuthenticationCredentials.USER_EMAIL, DefaultAuthenticationCredentials.USER_PASSWORD);
	}

	public static void authenticate(final HttpServletRequest request, final String user, final String password) {
		final String auth = request.getHeader("Authorization");
		if (auth == null) throw new AuthenticationException();

		checkCredentials(parseCredentials(auth), user, password);
	}

	private static void checkCredentials(final String[] credentials, final String user, final String password) {
		if (credentials.length < 2) throw new AuthenticationException("Password not informed.");

		if (!verify(user, credentials[0]) || !verify(password, credentials[1])) throw new AuthenticationException();
	}

	private static String[] parseCredentials(final String auth) {
		return StringUtils.split(new String(Base64.decodeBase64(auth.substring(auth.indexOf(' ')).getBytes()), Charset.forName("UTF-8")), ':');
	}

	private static boolean verify(final String expected, final String obtained) {
		return obtained.equals(expected);
	}

}
