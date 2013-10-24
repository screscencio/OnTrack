package br.com.oncast.ontrack.server.services.metrics;

import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.server.services.authentication.DefaultAuthenticationCredentials;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.exceptions.authentication.AuthenticationException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToLoadProjectException;
import br.com.oncast.ontrack.shared.model.user.User;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CsvMetricsExporterServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final ServerServiceProvider SERVICE_PROVIDER = ServerServiceProvider.getInstance();

	private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy_MM_dd");

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		try {
			final User authenticatedUser = SERVICE_PROVIDER.getAuthenticationManager().getAuthenticatedUser();
			if (authenticatedUser == null || !authenticatedUser.getId().equals(DefaultAuthenticationCredentials.USER_ID)) throw new AuthenticationException();

			configureResponse(response);
			writePcfl(response);
		} catch (final Exception e) {
			throw new ServletException(e);
		} finally {
			response.getOutputStream().flush();
		}
	}

	private void writePcfl(final HttpServletResponse response) throws IOException, PersistenceException {
		SERVICE_PROVIDER.getServerMetricsService().exportPcflCsv(response.getOutputStream());
	}

	private void configureResponse(final HttpServletResponse response) throws UnableToLoadProjectException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/plain");
		response.setHeader("Content-Disposition", "attachment; filename=\"ontrack_pcfl_" + getFormatedDate() + ".csv\"");
	}

	private String getFormatedDate() {
		return DATE_FORMATTER.format(new Date());
	}
}
