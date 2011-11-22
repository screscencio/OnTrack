package br.com.oncast.ontrack.shared.services.url;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;

public class URLBuilder {

	public enum Parameter {
		PROJECT_ID("projectId");

		private final String name;

		private Parameter(final String paramName) {
			this.name = paramName;
		}

		public String getName() {
			return name;
		}
	}

	private static final String EXPORT_TO_MINDMAP_APPLICATION_SERVLET_URL = "application/mindmap/download";
	public static final String SERVER_PUSH_COMET_URL = GWT.getModuleBaseURL() + "comet" + Window.Location.getQueryString();

	public static String buildMindMapExportURL(final long projectId) {
		return Window.Location.createUrlBuilder()
				.setPath(EXPORT_TO_MINDMAP_APPLICATION_SERVLET_URL)
				.setParameter(Parameter.PROJECT_ID.getName(), String.valueOf(projectId))
				.buildString();
	}
}
