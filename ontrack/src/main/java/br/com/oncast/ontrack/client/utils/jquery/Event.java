package br.com.oncast.ontrack.client.utils.jquery;

import com.google.gwt.core.client.JavaScriptObject;

public final class Event extends JavaScriptObject {
	protected Event() {}

	public native int which() /*-{
		return this.which;
	}-*/;

	public native boolean shiftKey() /*-{
		return this.shiftKey;
	}-*/;

	public native boolean ctrlKey() /*-{
		return this.ctrlKey;
	}-*/;

	public native boolean altKey() /*-{
		return this.altKey;
	}-*/;

	public native boolean metaKey() /*-{
		return this.metaKey;
	}-*/;

	public native void preventDefault() /*-{
		this.preventDefault();
	}-*/;

	public native void stopPropagation() /*-{
		this.stopPropagation();
	}-*/;
}
