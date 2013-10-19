package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.services.internet.ConnectionListener;
import br.com.oncast.ontrack.client.ui.events.PendingActionsCountChangeEvent;
import br.com.oncast.ontrack.client.ui.events.PendingActionsCountChangeEventHandler;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class ActionSyncStateMenuItem extends Composite implements IsWidget, PendingActionsCountChangeEventHandler, ConnectionListener {

	private static final ActionSyncStateMenuItemMessages MESSAGES = GWT.create(ActionSyncStateMenuItemMessages.class);

	private ActionSyncStateMenuItemHeaderWidget headerWidget;

	private final Set<HandlerRegistration> handlerRegistrations;

	private boolean connected = true;

	private int notSent;

	private int waiting;

	public ActionSyncStateMenuItem() {
		handlerRegistrations = new HashSet<HandlerRegistration>();

		initWidget(headerWidget = new ActionSyncStateMenuItemHeaderWidget());
		headerWidget.setIcon("icon-ok");
		setPendingActionsCount(0);
	}

	@Override
	protected void onLoad() {
		if (!handlerRegistrations.isEmpty()) return;

		handlerRegistrations.add(ClientServices.get().eventBus().addHandler(PendingActionsCountChangeEvent.getType(), this));
		handlerRegistrations.add(ClientServices.get().networkMonitor().addConnectionListener(this));
	}

	@Override
	protected void onUnload() {
		for (final HandlerRegistration r : handlerRegistrations)
			r.removeHandler();

		handlerRegistrations.clear();
	}

	public void setPendingActionsCount(final int count) {
		headerWidget.setCounterLabelVisible(count > 0);
		headerWidget.setText("" + count);
	}

	@Override
	public Widget asWidget() {
		return headerWidget;
	}

	private void updateHeader() {
		String styleName = connected ? "icon-ok" : "icon-remove";
		String connectionStateDescription = connected ? MESSAGES.connected() : MESSAGES.noConnection();
		int count = notSent + waiting;

		if (waiting > 0) {
			styleName = "icon-refresh icon-spin";
			connectionStateDescription = MESSAGES.syncing();
			count = waiting;
		}

		final String actionsCountText = count > 0 ? (count + " " + (count == 1 ? MESSAGES.singleModificationNeedToBeSent() : MESSAGES.multipleModificationsNeedsToBeSent())) : MESSAGES.upToDate();

		headerWidget.setTitle(connectionStateDescription + ": " + actionsCountText);
		headerWidget.setIcon(styleName);
		setPendingActionsCount(count);
		headerWidget.setConnected(connected);
	}

	@Override
	public void onPendingActionsCountChange(final PendingActionsCountChangeEvent e) {
		notSent = e.getNotSentActionsCount();
		waiting = e.getWaitingAnswerActionsCount();
		updateHeader();
	}

	@Override
	public void onConnectionRecovered() {
		connected = true;
		updateHeader();
	}

	@Override
	public void onConnectionLost() {
		connected = false;
		updateHeader();
	}

}