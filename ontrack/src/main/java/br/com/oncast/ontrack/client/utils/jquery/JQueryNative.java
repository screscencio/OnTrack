package br.com.oncast.ontrack.client.utils.jquery;

interface JQueryNative {
	public void bind(String eventType, EventHandler handler);

	public void unbind(String string, EventHandler handler);
}
