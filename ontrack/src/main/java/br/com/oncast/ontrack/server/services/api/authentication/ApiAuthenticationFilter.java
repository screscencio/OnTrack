package br.com.oncast.ontrack.server.services.api.authentication;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import br.com.oncast.ontrack.server.configuration.Configurations;
import br.com.oncast.ontrack.server.services.authentication.BasicRequestAuthenticator;

public class ApiAuthenticationFilter implements Filter {

	private final String integrationUser = Configurations.get().integrationUsername();

	private final String integrationPassword = Configurations.get().integrationPassword();

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
		BasicRequestAuthenticator.authenticate((HttpServletRequest) request, integrationUser, integrationPassword);
		chain.doFilter(request, response);
	}

	@Override
	public void init(final FilterConfig arg0) throws ServletException {}

	@Override
	public void destroy() {}

}
