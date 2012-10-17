package br.com.oncast.ontrack.client.services.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;
import br.com.drycode.api.web.gwt.dispatchService.shared.responses.VoidResult;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientService;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushEventHandler;
import br.com.oncast.ontrack.client.ui.events.ScopeAddMemberSelectionEvent;
import br.com.oncast.ontrack.client.ui.events.ScopeRemoveMemberSelectionEvent;
import br.com.oncast.ontrack.client.ui.events.ScopeSelectionEvent;
import br.com.oncast.ontrack.client.ui.events.ScopeSelectionEventHandler;
import br.com.oncast.ontrack.shared.model.ModelBeanNotFoundException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.user.exceptions.UserNotFoundException;
import br.com.oncast.ontrack.shared.services.requestDispatch.UserScopeSelectionMulticastRequest;
import br.com.oncast.ontrack.shared.services.user.UserClosedProjectEvent;
import br.com.oncast.ontrack.shared.services.user.UserSelectedScopeEvent;

import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;

public class MembersScopeSelectionServiceImpl implements MembersScopeSelectionService {

	private HashMap<User, Scope> selectionMap;
	private HashMap<User, String> colorMap;

	private final ColorPicker colorPicker;
	private final EventBus eventBus;

	public MembersScopeSelectionServiceImpl(final DispatchService requestDispatchService, final ContextProviderService contextProviderService,
			final ServerPushClientService serverPushClientService, final EventBus eventBus, final UsersStatusService usersStatusServiceImpl,
			final ColorPicker colorPicker) {
		this.colorPicker = colorPicker;
		this.eventBus = eventBus;
		selectionMap = new HashMap<User, Scope>();
		colorMap = new HashMap<User, String>();

		eventBus.addHandler(ScopeSelectionEvent.getType(), new ScopeSelectionEventHandler() {
			private Scope selectedScope;

			@Override
			public void onScopeSelectionRequest(final ScopeSelectionEvent event) {
				if (selectedScope != null && selectedScope.equals(event.getTargetScope())) return;

				selectedScope = event.getTargetScope();
				notifyScopeSelectionToServer();
			}

			private void notifyScopeSelectionToServer() {
				requestDispatchService.dispatch(new UserScopeSelectionMulticastRequest(selectedScope.getId()), new DispatchCallback<VoidResult>() {
					@Override
					public void onSuccess(final VoidResult result) {}

					@Override
					public void onTreatedFailure(final Throwable caught) {}

					@Override
					public void onUntreatedFailure(final Throwable caught) {}
				});
			}
		});

		serverPushClientService.registerServerEventHandler(UserSelectedScopeEvent.class, new ServerPushEventHandler<UserSelectedScopeEvent>() {
			@Override
			public void onEvent(final UserSelectedScopeEvent event) {
				try {
					final ProjectContext context = contextProviderService.getCurrentProjectContext();
					final User member = context.findUser(event.getUserEmail());

					removePreviousSelection(member);

					final Scope scope = context.findScope(event.getScopeId());
					selectionMap.put(member, scope);
					eventBus.fireEvent(new ScopeAddMemberSelectionEvent(member, scope, getSelectionColor(member)));
				}
				catch (final ModelBeanNotFoundException e) {
					GWT.log("Error handling UserSelectedScopeEvent!", e);
				}
			}

		});

		serverPushClientService.registerServerEventHandler(UserClosedProjectEvent.class, new ServerPushEventHandler<UserClosedProjectEvent>() {
			@Override
			public void onEvent(final UserClosedProjectEvent event) {
				try {
					final ProjectContext context = contextProviderService.getCurrentProjectContext();
					final User member = context.findUser(event.getUserEmail());
					removePreviousSelection(member);
				}
				catch (final UserNotFoundException e) {
					GWT.log("UserClosedProjectEventHandler Failed", e);
				}
			}
		});
	}

	private void removePreviousSelection(final User member) {
		final Scope previousSelection = selectionMap.remove(member);
		if (previousSelection != null) {
			eventBus.fireEvent(new ScopeRemoveMemberSelectionEvent(member, previousSelection));
		}
	}

	@Override
	public synchronized String getSelectionColor(final User user) {
		if (!colorMap.containsKey(user)) colorMap.put(user, colorPicker.pick());
		return colorMap.get(user);
	}

	@Override
	public List<Selection> getSelectionsFor(final Scope scope) {
		final List<Selection> selections = new ArrayList<Selection>();
		if (!selectionMap.containsValue(scope)) return selections;

		for (final Entry<User, Scope> e : selectionMap.entrySet()) {
			if (e.getValue().equals(scope)) {
				final User user = e.getKey();
				selections.add(new Selection(user, getSelectionColor(user)));
			}
		}

		return selections;
	}
}
