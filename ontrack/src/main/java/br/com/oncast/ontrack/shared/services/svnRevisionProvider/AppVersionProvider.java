package br.com.oncast.ontrack.shared.services.svnRevisionProvider;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Constants;

public interface AppVersionProvider extends Constants {

	final static AppVersionProvider INSTANCE = GWT.create(AppVersionProvider.class);

	@DefaultStringValue("NotBuilded")
	String version();

}
