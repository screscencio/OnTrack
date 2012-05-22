package net.zschech.gwt.comet.server.impl;

import java.io.OutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.zschech.gwt.comet.server.CometServlet;

import com.google.gwt.rpc.server.ClientOracle;
import com.google.gwt.user.server.rpc.SerializationPolicy;


/**
 * @author Icky from https://groups.google.com/forum/?fromgroups#!topic/gwt-comet/NkfGh6-nREM
 * Workaround for IE 9 Security rule 
 */
public class IEXDRCometServletResponse extends RawDataCometServletResponse {

	private static final int PADDING_REQUIRED = 2048;

	public IEXDRCometServletResponse(HttpServletRequest request,
			HttpServletResponse response,
			SerializationPolicy serializationPolicy, ClientOracle clientOracle,
			CometServlet servlet, AsyncServlet async, int heartbeat) {
		super(request, response, serializationPolicy, clientOracle, servlet,
				async, heartbeat);
	}

	@Override
	protected void setupHeaders(HttpServletResponse response) {
		super.setupHeaders(response);
		response.setContentType("application/comet");
		response.setCharacterEncoding("UTF-8");

		String origin = getRequest().getHeader("Origin");
		if (origin != null) {
			response.setHeader("Access-Control-Allow-Origin", origin);
		}
	}

	@Override
	protected OutputStream getOutputStream(OutputStream outputStream) {
		return setupCountOutputStream(outputStream);
	}

	@Override
	protected int getPaddingRequired() {
		return PADDING_REQUIRED;
	}
}