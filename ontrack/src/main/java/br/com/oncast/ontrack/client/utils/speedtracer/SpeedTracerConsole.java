package br.com.oncast.ontrack.client.utils.speedtracer;

public class SpeedTracerConsole {

	public static native void log(String msg) /*-{
		var logger = $wnd.console;
		if (logger && logger.markTimeline) {
			logger.markTimeline(msg);
		}
	}-*/;

}
