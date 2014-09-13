package br.com.oncast.ontrack.server.services.email;

import br.com.oncast.ontrack.server.services.CustomUrlGenerator;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;

import java.io.StringWriter;
import java.io.Writer;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

// TODO+ refactor this removing duplication and modularizing better
public class HtmlMailContent {

	public static String forPasswordReset(final String userEmail, final String generatedPassword) {
		final Template template = getTemplate("/br/com/oncast/ontrack/server/services/email/authMailPassReset.html");
		return writeMailContent(createPasswordResetContext(userEmail, generatedPassword), template);
	}

	public static String forProjectAuthorization(final String userEmail, final ProjectRepresentation project, final String currentUser) {
		final Template template = getTemplate("/br/com/oncast/ontrack/server/services/email/authMail.html");
		return writeMailContent(createProjectAuthorizationContext(project, userEmail, currentUser), template);
	}

	public static String forNewUserProjectAuthorization(final String userEmail, final String generatedPassword, final ProjectRepresentation project, final String currentUser) {
		final Template template = getTemplate("/br/com/oncast/ontrack/server/services/email/authMailNewUser.html");
		return writeMailContent(createProjectAuthorizationContextWithGeneratedPassword(project, userEmail, generatedPassword, currentUser), template);
	}

	public static String forNewUserWelcome(final String userEmail, final String generatedPassword, final String from) {
		final Template template = getTemplate("/br/com/oncast/ontrack/server/services/email/welcomeToNewUser.html");
		return writeMailContent(createNewUserWelcomeContext(userEmail, generatedPassword, from), template);
	}

	public static String forTrialUserWelcome(final String userEmail, final String accessToken, final String from) {
		final Template template = getTemplate("/br/com/oncast/ontrack/server/services/email/welcomeToTrialUser.html");
		return writeMailContent(createTrialUserWelcomeContext(userEmail, accessToken, from), template);
	}

	public static String forProjectCreationQuotaRequest(final String currentUser) {
		final Template template = getTemplate("/br/com/oncast/ontrack/server/services/email/projectCreationQuotaRequest.html");
		return writeMailContent(createProjectCreationQuotaRequestContext(currentUser), template);
	}

	public static String forSendFeedback(final String currentUser, final String feedbackMessage) {
		final Template template = getTemplate("/br/com/oncast/ontrack/server/services/email/sendFeedback.html");
		return writeMailContent(createSendFeedbackContext(currentUser, feedbackMessage), template);
	}

	private static String writeMailContent(final VelocityContext context, final Template template) {
		final Writer writer = new StringWriter();
		template.merge(context, writer);
		return writer.toString();
	}

	private static VelocityContext createNewUserWelcomeContext(final String userEmail, final String generatedPassword, final String inviter) {
		final VelocityContext context = new VelocityContext();
		context.put("projectLink", CustomUrlGenerator.getApplicationUrl());
		context.put("userEmail", userEmail);
		context.put("currentUser", inviter);
		context.put("generatedPassword", generatedPassword);
		return context;
	}

	private static VelocityContext createTrialUserWelcomeContext(final String userEmail, final String accessToken, final String inviter) {
		final VelocityContext context = new VelocityContext();
		context.put("projectLink", CustomUrlGenerator.getApplicationUrl() + "onboarding/access/" + accessToken);
		context.put("userEmail", userEmail);
		context.put("currentUser", inviter);
		return context;
	}

	private static VelocityContext createProjectAuthorizationContext(final ProjectRepresentation project, final String userEmail, final String currentUser) {
		final VelocityContext context = new VelocityContext();
		context.put("projectName", project.getName());
		context.put("projectLink", CustomUrlGenerator.forProject(project));
		context.put("userEmail", userEmail);
		context.put("currentUser", currentUser);
		return context;
	}

	private static VelocityContext createPasswordResetContext(final String userEmail, final String generatedPassword) {
		final VelocityContext context = new VelocityContext();
		context.put("userEmail", userEmail);
		context.put("generatedPassword", generatedPassword);
		return context;
	}

	private static VelocityContext createProjectAuthorizationContextWithGeneratedPassword(final ProjectRepresentation project, final String userEmail, final String generatedPassword,
			final String currentUser) {
		final VelocityContext context = createProjectAuthorizationContext(project, userEmail, currentUser);
		context.put("generatedPassword", generatedPassword);
		return context;
	}

	private static VelocityContext createSendFeedbackContext(final String currentUser, final String feedbackMessage) {
		final VelocityContext context = new VelocityContext();
		context.put("currentUser", currentUser);
		context.put("feedbackMessage", feedbackMessage);
		return context;
	}

	private static VelocityContext createProjectCreationQuotaRequestContext(final String currentUser) {
		final VelocityContext context = new VelocityContext();
		context.put("currentUser", currentUser);
		return context;
	}

	private static Template getTemplate(final String templatePath) {
		final VelocityEngine engine = createAndSetupVelocityEngine();
		Template template = null;
		try {
			template = engine.getTemplate(templatePath, "UTF-8");
		} catch (final Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error loading email template from velocity.");
		}
		return template;
	}

	private static VelocityEngine createAndSetupVelocityEngine() {
		final VelocityEngine engine = new VelocityEngine();
		engine.setProperty("resource.loader", "class");
		engine.setProperty("class.resource.loader.description", "Velocity Classpath Resource Loader");
		engine.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");

		try {
			final java.util.Properties p = new java.util.Properties();
			p.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");

			engine.init(p);
		} catch (final Exception e) {
			e.printStackTrace();
			throw new RuntimeException("Error initializing velocity.");
		}
		return engine;
	}

	public static String getContent(final String templatePath, final MailVariableValuesMap parameters) {
		final Template template = getTemplate(templatePath);
		return writeMailContent(parameters, template);
	}

}
