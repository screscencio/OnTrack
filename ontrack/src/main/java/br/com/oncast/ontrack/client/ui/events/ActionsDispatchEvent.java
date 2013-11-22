package br.com.oncast.ontrack.client.ui.events;

import com.google.gwt.event.shared.GwtEvent;

public class ActionsDispatchEvent extends GwtEvent<ActionsDispatchEventHandler> {

	public static Type<ActionsDispatchEventHandler> TYPE;

	private final boolean isDispatching;

	private final int actionsCount;

	public ActionsDispatchEvent(final boolean isDispatching, final int actionsCount) {
		this.isDispatching = isDispatching;
		this.actionsCount = actionsCount;
	}

	public boolean isDispatching() {
		return isDispatching;
	}

	public int getDispatchingActionsCount() {
		return actionsCount;
	}

	public static Type<ActionsDispatchEventHandler> getType() {
		return TYPE == null ? TYPE = new Type<ActionsDispatchEventHandler>() : TYPE;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<ActionsDispatchEventHandler> getAssociatedType() {
		return getType();
	}

	@Override
	protected void dispatch(final ActionsDispatchEventHandler handler) {
		handler.onActionsDispatchEvent(this);
	}

}
