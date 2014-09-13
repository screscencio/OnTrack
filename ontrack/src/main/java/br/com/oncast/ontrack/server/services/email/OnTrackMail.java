package br.com.oncast.ontrack.server.services.email;

public interface OnTrackMail {

	String getSubject();

	String getTemplatePath();

	MailVariableValuesMap getParameters();

	String getSendTo();

}
