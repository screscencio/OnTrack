package br.com.oncast.ontrack.client.services.metrics;

public class GoogleAnalyticsNativeImpl {

	public static native void trackPageview() /*-{
		$wnd.ga('send', 'pageview');
	}-*/;

	public static native void set(String name, String value) /*-{
		$wnd.ga('set', name, value);
	}-*/;

	public static native void trackTiming(String category, String variable, Number time) /*-{
		$wnd.ga('send', 'timing', {
			'timingCategory' : category,
			'timingVar' : variable,
			'timingValue' : time
		});
	}-*/;

	public static native void sendEvent(String category, String action) /*-{
		$wnd.ga('send', 'event', category, action);
	}-*/;

	public static native void sendEvent(String category, String action, String label) /*-{
		$wnd.ga('send', 'event', category, action, label);
	}-*/;

	public static native void sendEvent(String category, String action, String label, Number value) /*-{
		$wnd.ga('send', 'event', category, action, label, value);
	}-*/;

	public static native void sendException(final String description) /*-{
		$wnd.ga('send', 'exception', {
			'exDescription' : description
		});
	}-*/;

	public static native void sendCustomDimension(int dimensionIndex, final String value) /*-{
		$wnd.ga('dimension' + dimensionIndex, value);
	}-*/;

}
