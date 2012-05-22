package br.com.oncast.ontrack.server.services.exportImport;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.server.services.authentication.BasicAutheticator;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToLoadProjectException;

public class XMLExporterServlet extends HttpServlet {

	private static final String ATTRIBUTE_PROJECT_ID = "projectId";
	private static final ServerServiceProvider SERVICE_PROVIDER = ServerServiceProvider.getInstance();
	private static final long serialVersionUID = 1L;
	private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy_MM_dd");

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		try {
			BasicAutheticator.authenticate(request);
			doReply(request, response);
		}
		catch (final Exception e) {
			// TODO Display an user-friendly error message.
			throw new ServletException(e);
		}
	}

	private void doReply(final HttpServletRequest request, final HttpServletResponse response) throws UnableToLoadProjectException, IOException {
		configureResponse(response);
		final long projectId = Long.valueOf(request.getParameter(ATTRIBUTE_PROJECT_ID));
		generateAndWriteXMLTo(projectId, response);
		response.getOutputStream().flush();
	}

	private void generateAndWriteXMLTo(final long projectId, final HttpServletResponse response) throws IOException {
		SERVICE_PROVIDER.getXmlExporterService().export(response.getOutputStream(), projectId);
	}

	private void configureResponse(final HttpServletResponse response) throws UnableToLoadProjectException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/xml");
		response.setHeader("Content-Disposition", "attachment; filename=\"ontrack_instance_" + getFormatedDate() + ".xml\"");
	}

	private String getFormatedDate() {
		return DATE_FORMATTER.format(new Date());
	}
}
