package br.com.oncast.ontrack.client.utils.link;

import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.Window;

public class LinkFactory {

	private static final String START_A_TAG = "<a href=\"";
	private static final String END_A_TAG = "\" target=\"_blank\" >";
	private static final String CLOSE_A_TAG = "</a>";

	private static final String PROJECT_URL_CONSTANT = "#Planning:";
	private static final String ANNOTATION_URL_CONSTANT = "#Detail:";

	public static SafeHtml getLinkForProject(final ProjectRepresentation project) {
		final String projectLink = START_A_TAG + getProjectHrefLink(project.getId()) + END_A_TAG + project.getName() + CLOSE_A_TAG;

		return getSafeHTMLFor(projectLink);
	}

	public static SafeHtml getLinkForAnnotation(final UUID projectId, final UUID referencedId, final String text) {
		final String annotationLink = START_A_TAG + getAnnotationHrefLink(projectId, referencedId) + END_A_TAG + text + CLOSE_A_TAG;

		return getSafeHTMLFor(annotationLink);
	}

	private static String getBaseURL() {
		return Window.Location.getHref().substring(0, Window.Location.getHref().indexOf('#'));
	}

	private static String getProjectHrefLink(final UUID projectId) {
		return getBaseURL() + PROJECT_URL_CONSTANT + projectId;
	}

	private static String getAnnotationHrefLink(final UUID projectId, final UUID referencedId) {
		return getBaseURL() + ANNOTATION_URL_CONSTANT + projectId + ":" + referencedId;
	}

	private static SafeHtml getSafeHTMLFor(final String html) {
		final SafeHtmlBuilder safeHtmlBuilder = new SafeHtmlBuilder();
		safeHtmlBuilder.appendHtmlConstant(html);
		return safeHtmlBuilder.toSafeHtml();
	}
}
