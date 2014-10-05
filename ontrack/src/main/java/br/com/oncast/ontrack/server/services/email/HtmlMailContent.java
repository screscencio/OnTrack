package br.com.oncast.ontrack.server.services.email;

import java.io.StringWriter;
import java.io.Writer;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

class HtmlMailContent {

	public static String getContent(final String templatePath, final MailVariableValuesMap parameters) {
		final Template template = getTemplate(templatePath);
		return writeMailContent(parameters, template);
	}

	private static String writeMailContent(final VelocityContext context, final Template template) {
		final Writer writer = new StringWriter();
		template.merge(context, writer);
		return writer.toString();
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

}
