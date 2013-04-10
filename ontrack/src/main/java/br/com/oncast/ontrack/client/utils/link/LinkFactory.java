package br.com.oncast.ontrack.client.utils.link;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class LinkFactory {

	private static final String START_A_TAG = "<a href=\"";
	private static final String CLOSE_HREF = "\"";
	private static final String END_A_TAG = ">";
	private static final String NEW_TAB_LINK = " target=\"_blank\"";
	private static final String CLOSE_A_TAG = "</a>";

	private static final String PROJECT_URL_CONSTANT = "#Planning:";
	private static final String ANNOTATION_URL_CONSTANT = "#Detail:";

	public static SafeHtml getLinkForProject(final ProjectRepresentation project) {
		final String projectLink = START_A_TAG + getProjectHrefLink(project.getId()) + CLOSE_HREF + NEW_TAB_LINK + END_A_TAG + project.getName() + CLOSE_A_TAG;

		return getSafeHTMLFor(projectLink);
	}

	public static SafeHtml getLinkForAnnotation(final UUID projectId, final UUID referencedId, final String text) {
		final StringBuilder builder = new StringBuilder();

		builder.append(START_A_TAG);
		builder.append(getAnnotationHrefLink(projectId, referencedId));
		builder.append(CLOSE_HREF);

		if (shouldAppendNewTabLink(projectId)) builder.append(NEW_TAB_LINK);

		builder.append(END_A_TAG);
		builder.append(text);
		builder.append(CLOSE_A_TAG);

		return getSafeHTMLFor(builder.toString());
	}

	public static SafeHtml getScopeLinkFor(final UUID projectId, final UUID referencedId, final String text) {
		final StringBuilder builder = new StringBuilder();

		builder.append(START_A_TAG);
		builder.append(getScopeHrefLink(projectId, referencedId));
		builder.append(CLOSE_HREF);

		if (shouldAppendNewTabLink(projectId)) builder.append(NEW_TAB_LINK);

		builder.append(END_A_TAG);
		builder.append(text);
		builder.append(CLOSE_A_TAG);

		return getSafeHTMLFor(builder.toString());
	}

	private static boolean shouldAppendNewTabLink(final UUID projectId) {
		try {
			final ProjectRepresentation currentProject = ClientServices.get().projectRepresentationProvider().getCurrent();
			return !currentProject.getId().equals(projectId);
		}
		catch (final RuntimeException e) {
			return false;
		}
	}

	private static String getBaseURL() {
		return GWT.getHostPageBaseURL();
	}

	private static String getProjectHrefLink(final UUID projectId) {
		return getBaseURL() + PROJECT_URL_CONSTANT + projectId;
	}

	private static String getScopeHrefLink(final UUID projectId, final UUID referencedId) {
		return getBaseURL() + PROJECT_URL_CONSTANT + projectId + ":" + referencedId;
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
