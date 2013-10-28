package br.com.oncast.ontrack.server.services.metrics;

import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

public class InvitationDataExporterServlet extends CsvExporterServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void writeResponse(final HttpServletResponse response) throws IOException, PersistenceException {
		SERVICE_PROVIDER.getServerMetricsService().exportInvitationDataCsv(response.getOutputStream());
	}

	@Override
	protected String getFileName() {
		return "invitation_data";
	}

}
