package br.com.oncast.ontrack.server.services.authentication;

import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.shared.exceptions.authentication.AuthenticationException;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class OnboardingServlet extends HttpServlet {

	private static final ServerServiceProvider SERVICE_PROVIDER = ServerServiceProvider.getInstance();

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		final String[] paths = request.getPathInfo().split("/");
		final String userId = paths[paths.length - 1];
		try {
			SERVICE_PROVIDER.getSessionManager().configureCurrentHttpSession(request);
			SERVICE_PROVIDER.getAuthenticationManager().authenticateByToken(userId);
			response.sendRedirect("/");
		} catch (final AuthenticationException e) {
			response.sendRedirect("/error/token.html");
		}
	}

}
