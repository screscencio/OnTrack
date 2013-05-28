package br.com.oncast.ontrack.client.services.metrics;

public class GoogleAnalytics {

	public static native void trackPageview(String trackerPrefix) /*-{
		$wnd.ga(trackerPrefix + 'send', 'pageview');
	}-*/;

	public static native void set(String trackerPrefix, String name, String value) /*-{
		$wnd.ga(trackerPrefix + 'set', name, value);
	}-*/;

	public static native void trackTiming(String trackerPrefix, String category, String variable, Number time) /*-{
		$wnd.ga(trackerPrefix + 'send', 'timing', {
			'timingCategory' : category,
			'timingVar' : variable,
			'timingValue' : time
		});
	}-*/;

	public static native void sendException(String trackerPrefix, final String description) /*-{
		$wnd.ga(trackerPrefix + 'send', 'exception', {
			'exDescription' : description
		});
	}-*/;

	public static native void create(final String trackerName, String clientId) /*-{
		$wnd.ga('create', 'UA-41266386-1', {
			'name' : trackerName,
			'clientId' : clientId
		});
	}-*/;

}
