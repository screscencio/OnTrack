package br.com.oncast.ontrack.server.services.safareCacheWorkaroundFilter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SafareCacheWorkaroundFilter implements Filter {

	@Override
	public void destroy() {}

	@Override
	public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain)
			throws IOException, ServletException {

		final HttpServletRequest req = (HttpServletRequest) servletRequest;
		final HttpServletResponse resp = (HttpServletResponse) servletResponse;

		if ("post".equalsIgnoreCase(req.getMethod())) resp.addHeader("Cache-Control", "no-cache");
		if (filterChain != null) filterChain.doFilter(req, resp);
	}

	@Override
	public void init(final FilterConfig arg0) throws ServletException {}

}
