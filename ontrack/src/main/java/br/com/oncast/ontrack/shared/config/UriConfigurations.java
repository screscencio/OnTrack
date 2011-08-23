package br.com.oncast.ontrack.shared.config;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

public class UriConfigurations {

	public static final String EXPORT_TO_MINDMAP_APPLICATION_SERVLET_URL = "application/mindmap/download" + Window.Location.getQueryString();

	public static final String SERVER_PUSH_COMET_URL = GWT.getModuleBaseURL() + "comet" + Window.Location.getQueryString();
}
