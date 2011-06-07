
package server;

import jstm.core.Share;
import jstm.core.Site;
import server.generated.Form;
import server.generated.FormObjectModel;

public class MyTransportServiceImpl extends TransportServiceImpl {

    public MyTransportServiceImpl() {
        Site.getLocal().registerObjectModel(new FormObjectModel());

        // Uncomment this to have a complete log of events
        // Site.getLocal().addSiteListener(new Logger());

        // Create a share, and add it to the Group instance representing the
        // server and the clients

        Share share = new Share();

        getServer().getServerAndClients().getOpenShares().add(share);

        // Add a form to this share to make it replicated on all clients

        share.add(new Form());
    }
}
