package br.com.oncast.ontrack.server.services.serverPush;

import net.zschech.gwt.comet.server.CometSession;

public interface CometSessionListener {

	void onSessionCreated(CometSession cometSession);
}
