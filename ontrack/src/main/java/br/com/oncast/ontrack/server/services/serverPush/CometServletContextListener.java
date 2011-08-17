package br.com.oncast.ontrack.server.services.serverPush;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import br.com.oncast.ontrack.server.business.ServerBusinessLogicLocator;

public class CometServletContextListener implements ServletContextListener {

	@Override
	public void contextInitialized(final ServletContextEvent event) {
		ServerBusinessLogicLocator.getInstance().initializeServerPushDependencies();
	}

	@Override
	public void contextDestroyed(final ServletContextEvent event) {}
}
