package br.com.oncast.ontrack.client.services.serverPush;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface GwtCometPingServiceAsync {

	void ping(AsyncCallback<Void> callback);
}
