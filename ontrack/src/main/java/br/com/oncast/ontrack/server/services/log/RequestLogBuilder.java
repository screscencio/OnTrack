package br.com.oncast.ontrack.server.services.log;

import javax.servlet.http.HttpServletRequest;

class RequestLogBuilder {

	private static final String SEPARATOR = "\t";
	private final StringBuilder str;
	private final HttpServletRequest request;

	public RequestLogBuilder(final HttpServletRequest request) {
		this.str = new StringBuilder();
		this.request = request;
	}

	public RequestLogBuilder appendRequestedURI() {
		str.append(request.getRequestURI().toString());

		final String queryString = request.getQueryString();
		if (queryString != null) {
			str.append("?");
			str.append(queryString);
		}

		appendSeparator();
		return this;
	}

	public RequestLogBuilder appendUserAgent() {
		str.append(request.getHeader("User-Agent"));

		appendSeparator();
		return this;
	}

	public RequestLogBuilder appendClientIpAddress() {
		String ipAddress = request.getHeader("x-forwarded-for");
		if (ipAddress == null) ipAddress = request.getHeader("X_FORWARDED_FOR");
		if (ipAddress == null) ipAddress = request.getRemoteAddr();
		str.append(ipAddress);

		appendSeparator();
		return this;
	}

	@Override
	public String toString() {
		return str.toString();
	}

	private void appendSeparator() {
		str.append(SEPARATOR);
	}

}
