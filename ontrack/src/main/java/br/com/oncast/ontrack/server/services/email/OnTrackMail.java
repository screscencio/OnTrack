package br.com.oncast.ontrack.server.services.email;

import java.util.List;

public interface OnTrackMail {

	String getSubject();

	String getTemplatePath();

	MailVariableValuesMap getParameters();

	List<String> getRecipients();

}
