package br.com.oncast.ontrack.client.services.metric;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;

public class ClientMetricServiceNewRelicImpl implements ClientMetricService {

	@Override
	public void onBrowserLoadStart() {
		header();
	}

	@Override
	public void onBrowserLoadEnd() {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				footer();
			}
		});
	}

	public static native void header() /*-{
		$wnd.NREUMQ = $wnd.NREUMQ || [];
		$wnd.NREUMQ.push([ "mark", "firstbyte", new Date().getTime() ]);
	}-*/;

	public static native void footer() /*-{
		(function() {
			var d = $wnd.document;
			var e = d.createElement("script");
			e.async = true;
			e.src = "https://d1ros97qkrwjf5.cloudfront.net/28/eum/rum-staging.js";
			e.type = "text/javascript";
			var s = d.getElementsByTagName("script")[0];
			s.parentNode.insertBefore(e, s);
		})();
		$wnd.NREUMQ
				.push([ "nrf2", "beacon-1.newrelic.com", "api-key", appID,
						"cA1WFkEJX1UBFxpYXF4HFwtdAlZB", 0, 1058,
						new Date().getTime() ]);
	}-*/;

}
