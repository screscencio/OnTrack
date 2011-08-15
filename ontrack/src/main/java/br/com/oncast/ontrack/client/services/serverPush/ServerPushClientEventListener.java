package br.com.oncast.ontrack.client.services.serverPush;

import java.io.Serializable;

interface ServerPushClientEventListener {

	void onEvent(Serializable event);

}
