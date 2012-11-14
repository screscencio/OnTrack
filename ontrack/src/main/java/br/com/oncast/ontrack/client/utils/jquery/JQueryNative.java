package br.com.oncast.ontrack.client.utils.jquery;

interface JQueryNative {
	public void bind(String eventType, EventHandler handler);

	public void unbind(String eventType, EventHandler handler);

	public void slideUp(int duration, JQueryCallback callback);

	public void slideDown(int duration, JQueryCallback callback);

	public void fadeIn(int duration, JQueryCallback callback);

	public void fadeOut(int duration, JQueryCallback callback);

	public void fadeTo(int duration, double opacity, JQueryCallback callback);

	public void clearQueue();

	public void hide();

	public void show();

	public void stop(boolean clearQueue);

	public void customDropDownAbsolutePositioning(int duration, JQueryCallback callback);

	public void customDropUpAbsolutePositioning(int duration, JQueryCallback callback);

	public void slideLeftHide(int duration, JQueryCallback callback);

	public void slideRightShow(int duration, JQueryCallback callback);
}
