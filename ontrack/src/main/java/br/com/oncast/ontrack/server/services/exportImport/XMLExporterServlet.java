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
			doHandleError(request, response, e);
		}
	}

	private void doReply(final HttpServletRequest request, final HttpServletResponse response)
			throws UnableToLoadProjectException, IOException {
		final BusinessLogic business = ServerBusinessLogicLocator.getInstance().getBusinessLogic();

		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/xml");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + business.loadProject().getProjectScope().getDescription() + ".xml\"");

		new XMLExporter(ServerServiceProvider.getInstance().getPersistenceService(), response.getOutputStream()).mountXML().export();
		response.getOutputStream().flush();
	}

	// TODO Display an user-friendly error message.
	private void doHandleError(final HttpServletRequest request, final HttpServletResponse response, final Exception e) throws ServletException {
		throw new ServletException(e);
	}

}
