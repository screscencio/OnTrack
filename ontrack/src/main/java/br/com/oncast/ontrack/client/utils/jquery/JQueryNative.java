package br.com.oncast.ontrack.client.utils.jquery;

interface JQueryNative {
	public void bind(String eventType, EventHandler handler);

	public void unbind(String eventType, EventHandler handler);

	public void slideUp(int duration);

	public void slideDown(int duration);

	public void hide();
}
