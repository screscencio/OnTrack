package br.com.oncast.ontrack.server.services.authentication.basic;

import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import br.com.oncast.ontrack.server.services.authentication.DefaultAuthenticationCredentials;
import br.com.oncast.ontrack.shared.exceptions.authentication.AuthenticationException;

public class BasicAutheticator {

	public static void authenticate(final HttpServletRequest request) {
		final String auth = request.getHeader("Authorization");
		if (auth == null) throw new AuthenticationException();

		checkCredentials(parseCredentials(auth));
	}

	private static void checkCredentials(final String[] credentials) {
		if (credentials.length < 2) throw new AuthenticationException("Password not informed.");

		if (!verifyUser(credentials[0]) || !verifyPassword(credentials[1])) throw new AuthenticationException();
	}

	private static String[] parseCredentials(final String auth) {
		return StringUtils.split(new String(Base64.decodeBase64(auth.substring(auth.indexOf(' ')).getBytes()), Charset.forName("UTF-8")), ':');
	}

	private static boolean verifyUser(final String user) {
		return user.equals(DefaultAuthenticationCredentials.USER);
	}

	private static boolean verifyPassword(final String password) {
		return password.equals(DefaultAuthenticationCredentials.PASSWORD);
	}
}
