package br.com.oncast.ontrack.server.services.httpSessionProvider;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import br.com.oncast.ontrack.server.business.ServerBusinessLogicLocator;

public class RequestFilter implements Filter {

	@Override
	public void destroy() {}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain) throws IOException, ServletException {
		ServerBusinessLogicLocator.getInstance().getHttpSessionProvider().setCurrentSession(((HttpServletRequest) request).getSession());
		filterChain.doFilter(request, response);
	}

	@Override
	public void init(final FilterConfig config) throws ServletException {}

}