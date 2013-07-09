package br.com.oncast.ontrack.server.services.api.authentication;

import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.server.services.authentication.BasicRequestAuthenticator;
import br.com.oncast.ontrack.shared.model.user.User;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public class ApiAuthenticationFilter implements Filter {

	private static final Logger LOGGER = Logger.getLogger(ApiAuthenticationFilter.class);

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
		final HttpServletRequest httpRequest = (HttpServletRequest) request;

		ensureHasHttpSession(httpRequest);

		authenticate(httpRequest);

		chain.doFilter(request, response);
	}

	private void authenticate(final HttpServletRequest request) throws ServletException {
		try {
			final String[] credentials = BasicRequestAuthenticator.extractCredentials(request);
			final User user = ServerServiceProvider.getInstance().getAuthenticationManager().authenticate(credentials[0], credentials[1]);
			LOGGER.info("The user " + user + " has authenticated successfully for api services");
		} catch (final Exception e) {
			final String message = "Could not authenticate for api services";
			LOGGER.error(message, e);
			throw new ServletException(message, e);
		}
	}

	private void ensureHasHttpSession(final HttpServletRequest httpRequest) {
		ServerServiceProvider.getInstance().getSessionManager().configureCurrentHttpSession(httpRequest);
	}

	@Override
	public void init(final FilterConfig arg0) throws ServletException {}

	@Override
	public void destroy() {}

}
