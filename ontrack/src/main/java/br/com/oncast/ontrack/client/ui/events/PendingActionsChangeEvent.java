package br.com.oncast.ontrack.client.ui.events;

import br.com.oncast.ontrack.shared.model.action.ModelAction;

import java.util.List;

import com.google.gwt.event.shared.GwtEvent;

public class PendingActionsChangeEvent extends GwtEvent<PendingActionsCountChangeEventHandler> {

	public static Type<PendingActionsCountChangeEventHandler> TYPE;

	private final List<ModelAction> notSentActions;

	private final List<ModelAction> actionsWaitingServerAnswer;

	public PendingActionsChangeEvent(final List<ModelAction> notSentActions, final List<ModelAction> actionsWaitingServerAnswer) {
		this.notSentActions = notSentActions;
		this.actionsWaitingServerAnswer = actionsWaitingServerAnswer;
	}

	public List<ModelAction> getNotSentActions() {
		return notSentActions;
	}

	public List<ModelAction> getActionsWaitingServerAnswer() {
		return actionsWaitingServerAnswer;
	}

	public int getNotSentActionsCount() {
		return notSentActions.size();
	}

	public int getWaitingAnswerActionsCount() {
		return actionsWaitingServerAnswer.size();
	}

	public static Type<PendingActionsCountChangeEventHandler> getType() {
		return TYPE == null ? TYPE = new Type<PendingActionsCountChangeEventHandler>() : TYPE;
	}

	@Override
	public com.google.gwt.event.shared.GwtEvent.Type<PendingActionsCountChangeEventHandler> getAssociatedType() {
		return getType();
	}

	@Override
	protected void dispatch(final PendingActionsCountChangeEventHandler handler) {
		handler.onPendingActionsCountChange(this);
	}

}
