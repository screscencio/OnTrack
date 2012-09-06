package br.com.oncast.ontrack.shared.services.url;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

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
	public static final String ATMOSPHERE_URL = GWT.getModuleBaseURL() + "atmosphere" + Window.Location.getQueryString();

	public static String buildMindMapExportURL(final UUID uuid) {
		return Window.Location.createUrlBuilder()
				.setPath(EXPORT_TO_MINDMAP_APPLICATION_SERVLET_URL)
				.setParameter(Parameter.PROJECT_ID.getName(), uuid.toStringRepresentation())
				.buildString();
	}
}
