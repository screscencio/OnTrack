package br.com.oncast.ontrack.server.business;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.oncast.ontrack.server.services.persistence.jpa.PersistenceServiceJpaImpl;
import br.com.oncast.ontrack.server.util.mindmapconverter.freemindconverter.FreeMindExporter;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToLoadProjectException;
import br.com.oncast.ontrack.shared.model.project.Project;

public class MindMapExporterServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		final Project project = getProject();
		configureResponse(response, project.getProjectScope().getDescription());

		FreeMindExporter.export(project, response.getOutputStream());
	}

	private Project getProject() {
		final BusinessLogic business = new BusinessLogic(new PersistenceServiceJpaImpl());
		try {
			return business.loadProject();
		}
		catch (final UnableToLoadProjectException e) {
			throw new RuntimeException("There was not possible to export the mind map.", e);
		}
	}

	private void configureResponse(final HttpServletResponse response, final String fileName) {
		response.setContentType("text/xml");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + ".mm\"");
	}

}
