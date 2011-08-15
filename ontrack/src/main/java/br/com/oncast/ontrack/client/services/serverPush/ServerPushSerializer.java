package br.com.oncast.ontrack.client.services.serverPush;

import net.zschech.gwt.comet.client.CometSerializer;
import net.zschech.gwt.comet.client.SerialTypes;
import br.com.oncast.ontrack.shared.services.serverPush.ServerPushEvent;

@SerialTypes({ ServerPushEvent.class })
public abstract class ServerPushSerializer extends CometSerializer {

}
