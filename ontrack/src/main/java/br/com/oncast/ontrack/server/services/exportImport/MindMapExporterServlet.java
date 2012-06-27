package br.com.oncast.ontrack.server.services.exportImport;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.oncast.ontrack.server.business.BusinessLogic;
import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.server.services.exportImport.freemind.FreeMindExporter;
import br.com.oncast.ontrack.shared.exceptions.business.ProjectNotFoundException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToLoadProjectException;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.url.URLBuilder;

public class MindMapExporterServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final BusinessLogic BUSINESS = ServerServiceProvider.getInstance().getBusinessLogic();

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		try {
			doReply(request, response);
		}
		catch (final Exception e) {
			// TODO +++Display an user-friendly error message.
			throw new ServletException(e);
		}
	}

	private void doReply(final HttpServletRequest request, final HttpServletResponse response) throws UnableToLoadProjectException, IOException,
			ServletException, ProjectNotFoundException {
		final Project project = BUSINESS.loadProject(getProjectId(request));
		configureResponse(response, project);

		FreeMindExporter.export(project, response.getOutputStream());
		response.getOutputStream().flush();
	}

	private UUID getProjectId(final HttpServletRequest request) throws ServletException {
		try {
			return new UUID(request.getParameter(URLBuilder.Parameter.PROJECT_ID.getName()));
		}
		catch (final NumberFormatException e) {
			throw new ServletException("It was not possible to export to Mind Map: the 'projectId' parameter must be a valid Number");
		}
	}

	private void configureResponse(final HttpServletResponse response, final Project project) {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/xml");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + project.getProjectScope().getDescription() + ".mm\"");
	}

}
