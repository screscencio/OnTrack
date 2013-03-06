package br.com.oncast.ontrack.server.services.email;

public class PasswordResetMailFactory {
	public PasswordResetMail createMail() {
		return PasswordResetMail.createInstance();
	}
}
