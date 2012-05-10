package br.com.oncast.ontrack.client.services.validation;

import com.google.gwt.regexp.shared.RegExp;

public class EmailValidator {

	private static final String FLAG_IGNORE_CASE = "i";
	private static final String PATTERN = "^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?";

	/**
	 * Verifies if the given e-mail address is a valid e-mail using practical implementation of RFC 2822 that omits double quotes and square brackets.<br>
	 * It will match 99.99% of all email addresses.
	 * <b>Note:</b>
	 * <ul>
	 * <li>Case insensitive.</li>
	 * <li>This method does not trim nor modifies the parameter.</li>
	 * </ul>
	 * @param email to be validated.
	 * @return true if the given e-mail is valid, false otherwise.
	 */
	public static boolean isValid(final String email) {
		return RegExp.compile(PATTERN, FLAG_IGNORE_CASE).test(email);
	}

}
