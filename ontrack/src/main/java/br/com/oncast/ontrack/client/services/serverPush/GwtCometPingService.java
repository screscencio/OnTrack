package br.com.oncast.ontrack.client.services.serverPush;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("gwtCometPingService")
public interface GwtCometPingService extends RemoteService {

	void ping();
}
