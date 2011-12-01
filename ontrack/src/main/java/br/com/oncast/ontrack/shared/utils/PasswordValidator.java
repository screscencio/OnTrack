package br.com.oncast.ontrack.shared.utils;

public class PasswordValidator {

	// TODO Review password strength roles.
	public static boolean isValid(final String password) {
		return password.length() >= 6;
	}
}