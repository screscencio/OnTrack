package br.com.oncast.ontrack.server.services.metrics;

import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.server.services.authentication.BasicRequestAuthenticator;
import br.com.oncast.ontrack.server.services.authentication.DefaultAuthenticationCredentials;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToLoadProjectException;
import br.com.oncast.ontrack.shared.model.user.User;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class CsvExporterServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected static final ServerServiceProvider SERVICE_PROVIDER = ServerServiceProvider.getInstance();
	private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("yyyy_MM_dd");

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		try {
			if (!isAdminAuthenticated()) BasicRequestAuthenticator.authenticate(request);;

			configureResponse(response);
			writeResponse(response);
		} catch (final Exception e) {
			throw new ServletException(e);
		} finally {
			response.getOutputStream().flush();
		}
	}

	private boolean isAdminAuthenticated() {
		final User authenticatedUser = SERVICE_PROVIDER.getAuthenticationManager().getAuthenticatedUser();
		return authenticatedUser != null && authenticatedUser.getId().equals(DefaultAuthenticationCredentials.USER_ID);
	}

	private void configureResponse(final HttpServletResponse response) throws UnableToLoadProjectException {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/plain");
		response.setHeader("Content-Disposition", "attachment; filename=\"ontrack_" + getFileName() + "_" + getFormatedDate() + ".csv\"");
	}

	private String getFormatedDate() {
		return DATE_FORMATTER.format(new Date());
	}

	protected abstract void writeResponse(HttpServletResponse response) throws IOException, PersistenceException;

	protected abstract String getFileName();

}