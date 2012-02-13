package br.com.oncast.ontrack.server.services.email;

import java.io.StringWriter;
import java.io.Writer;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;

public class HtmlMailContent {

	public static String forProjectAuthorization(final String userEmail, final ProjectRepresentation project, final String currentUser) {
		final Template template = getTemplate("emailProjAuth/authMail.html");
		final Writer writer = new StringWriter();
		template.merge(createProjectAuthorizationContext(project, userEmail, currentUser), writer);

		return writer.toString();
	}

	private static VelocityContext createProjectAuthorizationContext(final ProjectRepresentation project, final String userEmail, final String currentUser) {
		final VelocityContext context = new VelocityContext();
		context.put("projectName", project.getName());
		context.put("projectLink", project.getId()); // FIXME BESEN generate link correctly
		context.put("userEmail", userEmail);
		context.put("currentUser", currentUser);

		return context;
	}

	private static Template getTemplate(final String templatePath) {
		final VelocityEngine engine = new VelocityEngine();
		engine.init();
		Template template = null;
		try {
			template = engine.getTemplate(templatePath, "UTF-8");
		}
		catch (final Exception e) {
			throw new RuntimeException("Error loading email template from velocity.", e);
		}
		return template;
	}
}
