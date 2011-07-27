package br.com.oncast.ontrack.server.business;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.oncast.ontrack.server.services.persistence.jpa.PersistenceServiceJpaImpl;
import br.com.oncast.ontrack.server.util.mmConverter.FreeMindExporter;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToLoadProjectException;
import br.com.oncast.ontrack.shared.model.project.Project;

public class MindMapExporterServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		try {
			doReply(request, response);
		}
		catch (final Exception e) {
			doHandleError(request, response, e);
		}
	}

	private void doReply(final HttpServletRequest request, final HttpServletResponse response) throws UnableToLoadProjectException, IOException {
		final BusinessLogic business = new BusinessLogic(new PersistenceServiceJpaImpl());
		final Project project = business.loadProject();

		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/xml");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + project.getProjectScope().getDescription() + ".mm\"");

		FreeMindExporter.export(project, response.getOutputStream());
		response.getOutputStream().flush();
	}

	// TODO +++Display an user-friendly error message.
	private void doHandleError(final HttpServletRequest request, final HttpServletResponse response, final Exception e) throws ServletException {
		throw new ServletException(e);
	}
}
