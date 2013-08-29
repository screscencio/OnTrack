package br.com.oncast.ontrack.server.services.serverPush.atmosphere;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.atmosphere.gwt.server.AtmosphereGwtHandler;
import org.atmosphere.gwt.server.GwtAtmosphereResource;

public class OntrackAtmosphereHandler extends AtmosphereGwtHandler {

	private static final Logger LOGGER = Logger.getLogger(OntrackAtmosphereHandler.class);
	private static AtmosphereConnectionListener connectionListener;

	@Override
	public void init(final ServletConfig servletConfig) throws ServletException {
		super.init(servletConfig);
	}

	@Override
	public int doComet(final GwtAtmosphereResource resource) throws ServletException, IOException {
		final HttpSession session = resource.getAtmosphereResource().getRequest().getSession(false);
		if (session != null) {
			LOGGER.debug("Got session with id: " + session.getId());
			LOGGER.debug("Time attribute: " + session.getAttribute("time"));
		} else {
			LOGGER.warn("No session");
		}
		connectionListener.connected(resource);

		return NO_TIMEOUT;
	}

	@Override
	public void cometTerminated(final GwtAtmosphereResource cometResponse, final boolean serverInitiated) {
		connectionListener.disconnected(cometResponse);
	}

	/**
	 * Is not used?
	 * 
	 * @see org.atmosphere.gwt.server.AtmosphereGwtHandler#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.util.List,
	 *      org.atmosphere.gwt.server.GwtAtmosphereResource)
	 */
	@Override
	public void doPost(final HttpServletRequest postRequest, final HttpServletResponse postResponse, final List<?> messages, final GwtAtmosphereResource cometResource) {
		final HttpSession session = postRequest.getSession(false);
		if (session != null) {
			LOGGER.info("Post has session with id: " + session.getId());
		} else {
			LOGGER.info("Post has no session");
		}
		super.doPost(postRequest, postResponse, messages, cometResource);
	}

	public static void setConnectionListener(final AtmosphereConnectionListener atmosphereConnectionListener) {
		OntrackAtmosphereHandler.connectionListener = atmosphereConnectionListener;
	}
}
