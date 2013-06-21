package br.com.oncast.ontrack.server.services.exportImport;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.server.services.authentication.BasicRequestAuthenticator;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToLoadProjectException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class XMLExporterServlet extends HttpServlet {

	private static final String PARAMETER_PROJECT_ID = "projectId";
	private static final String PARAMETER_LIST_PROJECTS = "list-projects";
	private static final ServerServiceProvider SERVICE_PROVIDER = ServerServiceProvider.getInstance();
	private static final long serialVersionUID = 1L;
	private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy_MM_dd");

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		try {
			BasicRequestAuthenticator.authenticate(request);
			if (request.getParameter(PARAMETER_LIST_PROJECTS) != null) doReplyProjectList(response);
			else doReply(request, response);
		}
		catch (final Exception e) {
			// TODO Display an user-friendly error message.
			throw new ServletException(e);
		}
	}

	private void doReplyProjectList(final HttpServletResponse response) throws IOException, PersistenceException {
		configurePlainResponse(response);
		SERVICE_PROVIDER.getXmlExporterService().listProjects(response.getOutputStream());
	}

	private void doReply(final HttpServletRequest request, final HttpServletResponse response) throws UnableToLoadProjectException, IOException {
		configureXMLResponse(response);
		final UUID projectId = new UUID(request.getParameter(PARAMETER_PROJECT_ID));
		generateAndWriteXMLTo(projectId, response);
		response.getOutputStream().flush();
	}

	private void generateAndWriteXMLTo(final UUID projectId, final HttpServletResponse response) throws IOException {
		SERVICE_PROVIDER.getXmlExporterService().export(response.getOutputStream(), projectId);
	}

	private void configurePlainResponse(final HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/plain");
		response.setHeader("Content-Disposition", "attachment; filename=\"ontrack_instance_" + getFormatedDate() + ".csv\"");
	}

	private void configureXMLResponse(final HttpServletResponse response) throws UnableToLoadProjectException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/xml");
		response.setHeader("Content-Disposition", "attachment; filename=\"ontrack_instance_" + getFormatedDate() + ".xml\"");
	}

	private String getFormatedDate() {
		return DATE_FORMATTER.format(new Date());
	}
}
