package br.com.oncast.ontrack.server.services.exportImport;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.oncast.ontrack.server.business.BusinessLogic;
import br.com.oncast.ontrack.server.business.ServerBusinessLogicLocator;
import br.com.oncast.ontrack.server.services.ServerServiceProvider;
import br.com.oncast.ontrack.server.services.authentication.basic.BasicAutheticator;
import br.com.oncast.ontrack.server.services.exportImport.xml.XMLExporter;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToLoadProjectException;

public class XMLExporterServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

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
		generateAndWriteXMLTo(response);

		response.getOutputStream().flush();
	}

	private void generateAndWriteXMLTo(final HttpServletResponse response) throws IOException {
		new XMLExporter(ServerServiceProvider.getInstance().getPersistenceService(), response.getOutputStream()).mountXML().export();
	}

	private void configureResponse(final HttpServletResponse response) throws UnableToLoadProjectException {
		final BusinessLogic business = ServerBusinessLogicLocator.getInstance().getBusinessLogic();

		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/xml");
		// FIXME Use a correct projetId
		response.setHeader("Content-Disposition", "attachment; filename=\"" + business.loadProject(null).getProjectScope().getDescription() + ".xml\"");
	}
}
