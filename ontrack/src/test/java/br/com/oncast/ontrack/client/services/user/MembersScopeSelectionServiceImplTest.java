package br.com.oncast.ontrack.client.services.user;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.verification.VerificationMode;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientService;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushEventHandler;
import br.com.oncast.ontrack.client.ui.events.ScopeAddMemberSelectionEvent;
import br.com.oncast.ontrack.client.ui.events.ScopeMemberSelectionEvent;
import br.com.oncast.ontrack.client.ui.events.ScopeRemoveMemberSelectionEvent;
import br.com.oncast.ontrack.client.ui.events.ScopeSelectionEvent;
import br.com.oncast.ontrack.client.ui.events.ScopeSelectionEventHandler;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.user.exceptions.UserNotFoundException;
import br.com.oncast.ontrack.shared.services.requestDispatch.UserScopeSelectionMulticastRequest;
import br.com.oncast.ontrack.shared.services.user.UserClosedProjectEvent;
import br.com.oncast.ontrack.shared.services.user.UserSelectedScopeEvent;
import br.com.oncast.ontrack.utils.mocks.models.ScopeTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.UserTestUtils;

import com.google.web.bindery.event.shared.Event;
import com.google.web.bindery.event.shared.EventBus;

public class MembersScopeSelectionServiceImplTest {

	@Mock
	private DispatchService requestDispatchService;

	@Mock
	private ContextProviderService contextProviderService;

	@Mock
	private ServerPushClientService serverPushClientService;

	@Mock
	private EventBus eventBus;

	@Mock
	private UsersStatusService usersStatusServiceImpl;

	private User user1;

	private Scope scope1;

	private ServerPushEventHandler<UserSelectedScopeEvent> userSelectedScopeEventHandler;

	@Mock
	private ProjectContext currentContext;

	private ScopeSelectionEventHandler scopeSelectionEventHandler;

	private ServerPushEventHandler<UserClosedProjectEvent> userClosedProjectEventHandler;

	@Mock
	private ColorPicker colorPicker;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);

		when(contextProviderService.getCurrentProjectContext()).thenReturn(currentContext);
		when(colorPicker.pick()).thenReturn("a color");

		user1 = createUser();
		scope1 = createScope();

		final ArgumentCaptor<ServerPushEventHandler> userSelectedScopeEventHandlerCaptor = ArgumentCaptor.forClass(ServerPushEventHandler.class);
		doNothing().when(serverPushClientService).registerServerEventHandler(eq(UserSelectedScopeEvent.class), userSelectedScopeEventHandlerCaptor.capture());

		final ArgumentCaptor<ServerPushEventHandler> userClosedProjectEventHandlerCaptor = ArgumentCaptor.forClass(ServerPushEventHandler.class);
		doNothing().when(serverPushClientService).registerServerEventHandler(eq(UserClosedProjectEvent.class), userClosedProjectEventHandlerCaptor.capture());

		new ColorProviderServiceImpl(requestDispatchService, contextProviderService, serverPushClientService,
				eventBus, usersStatusServiceImpl, colorPicker);

		userClosedProjectEventHandler = userClosedProjectEventHandlerCaptor.getValue();
		userSelectedScopeEventHandler = userSelectedScopeEventHandlerCaptor.getValue();

		final ArgumentCaptor<ScopeSelectionEventHandler> eventBusCaptor = ArgumentCaptor.forClass(ScopeSelectionEventHandler.class);
		verify(eventBus).addHandler(eq(ScopeSelectionEvent.getType()), eventBusCaptor.capture());
		scopeSelectionEventHandler = eventBusCaptor.getValue();
	}

	@Test
	public void shouldFireScopeAddMemberSelectionEventWhenUserScopeSelectionIsSentByTheServer() throws Exception {
		userSelectedScopeEventHandler.onEvent(new UserSelectedScopeEvent(user1.getId(), scope1.getId()));
		assertEventCalled(ScopeAddMemberSelectionEvent.class, user1, scope1);
	}

	@Test
	public void shouldRequestToServerToMulticastTheCurrentUserScopeSelection() throws Exception {
		scopeSelectionEventHandler.onScopeSelectionRequest(new ScopeSelectionEvent(scope1));
		assertRequested(scope1);
	}

	@Test
	public void shouldFireEventToClearPreviousSelectionBeforeSelectingAnotherScope() throws Exception {
		final Scope scope2 = createScope();
		userSelectedScopeEventHandler.onEvent(new UserSelectedScopeEvent(user1.getId(), scope1.getId()));
		userSelectedScopeEventHandler.onEvent(new UserSelectedScopeEvent(user1.getId(), scope2.getId()));

		assertEventCalled(ScopeRemoveMemberSelectionEvent.class, user1, scope1);
		assertEventCalled(ScopeAddMemberSelectionEvent.class, user1, scope2);
	}

	@Test
	public void shouldNotFireEventToRemovePreviousSelectionBeforeSelectingAnotherScopeWhenThereWasNoPreviousSelection() throws Exception {
		final Scope scope2 = createScope();
		userSelectedScopeEventHandler.onEvent(new UserSelectedScopeEvent(user1.getId(), scope2.getId()));

		assertEventNotCalled(ScopeRemoveMemberSelectionEvent.class, user1, scope1);
		assertEventNotCalled(ScopeRemoveMemberSelectionEvent.class, user1, scope2);
		assertEventCalled(ScopeAddMemberSelectionEvent.class, user1, scope2);
	}

	@Test
	public void shouldFireScopeRemoveMemberSelectionEventWhenTheMemberClosesTheProject() throws Exception {
		userSelectedScopeEventHandler.onEvent(new UserSelectedScopeEvent(user1.getId(), scope1.getId()));
		userClosedProjectEventHandler.onEvent(new UserClosedProjectEvent(user1.getId()));

		assertEventCalled(ScopeAddMemberSelectionEvent.class, user1, scope1);
		assertEventCalled(ScopeRemoveMemberSelectionEvent.class, user1, scope1);
	}

	@Test
	public void shouldNotFireScopeRemoveMemberSelectionEventWhenTheMemberClosesTheProjectButThereWereNoPreviousSelection() throws Exception {
		userClosedProjectEventHandler.onEvent(new UserClosedProjectEvent(user1.getId()));

		assertEventNotCalled(ScopeRemoveMemberSelectionEvent.class, user1, null);
	}

	@Test
	public void memberSelectionShouldSendTheColorRepresentingTheMember() throws Exception {
		userSelectedScopeEventHandler.onEvent(new UserSelectedScopeEvent(user1.getId(), createScope().getId()));
		final ScopeAddMemberSelectionEvent event = assertEventCalled(ScopeAddMemberSelectionEvent.class, user1, null);
		assertNotNull(event.getSelectionColor());
		assertFalse(event.getSelectionColor().isEmpty());
	}

	@Test
	public void memberSelectionShouldSendTheSameColorForSameMember() throws Exception {
		userSelectedScopeEventHandler.onEvent(new UserSelectedScopeEvent(user1.getId(), createScope().getId()));
		final String user1Color = assertEventCalled(ScopeAddMemberSelectionEvent.class, user1, null).getSelectionColor();

		for (int i = 0; i < 10; i++) {
			userSelectedScopeEventHandler.onEvent(new UserSelectedScopeEvent(user1.getId(), createScope().getId()));
			assertEquals(user1Color, assertEventCall(ScopeAddMemberSelectionEvent.class, user1, null, atLeastOnce()).getSelectionColor());
		}

		verify(colorPicker).pick();
	}

	@Test
	public void differentMembersShouldHaveDifferentColors() throws Exception {
		userSelectedScopeEventHandler.onEvent(new UserSelectedScopeEvent(user1.getId(), createScope().getId()));
		assertEventCalled(ScopeAddMemberSelectionEvent.class, user1, null);

		for (int i = 0; i < 10; i++) {
			final User differentUser = createUser();
			userSelectedScopeEventHandler.onEvent(new UserSelectedScopeEvent(differentUser.getId(), createScope().getId()));
			assertEventCall(ScopeAddMemberSelectionEvent.class, differentUser, null, atLeastOnce());
		}

		verify(colorPicker, times(11)).pick();
	}

	@SuppressWarnings("unchecked")
	private void assertRequested(final Scope scope) {
		verify(requestDispatchService).dispatch(argThat(new ArgumentMatcher<UserScopeSelectionMulticastRequest>() {

			@Override
			public boolean matches(final Object argument) {
				if (!(argument instanceof UserScopeSelectionMulticastRequest)) return false;

				final UserScopeSelectionMulticastRequest request = (UserScopeSelectionMulticastRequest) argument;
				return scope.getId().equals(request.getSelectedScopeId());
			}
		}), any(DispatchCallback.class));
	}

	private <T extends Event<?>> T assertEventCalled(final Class<T> clazz, final User member, final Scope scope) {
		return assertEventCall(clazz, member, scope, times(1));
	}

	private <T extends Event<?>> void assertEventNotCalled(final Class<T> clazz, final User member, final Scope scope) {
		assertEventCall(clazz, member, scope, never());
	}

	@SuppressWarnings("unchecked")
	private <T extends Event<?>> T assertEventCall(final Class<T> clazz, final User member, final Scope scope, final VerificationMode mode) {
		final List<T> captured = new ArrayList<T>();
		verify(eventBus, mode).fireEvent(argThat(new BaseMatcher<T>() {
			@Override
			public boolean matches(final Object argument) {
				if (!clazz.equals(argument.getClass())) return false;

				final ScopeMemberSelectionEvent event = (ScopeMemberSelectionEvent) argument;
				captured.add((T) event);
				return member.equals(event.getMember()) && (scope == null ? true : scope.equals(event.getTargetScope()));
			}

			@Override
			public void describeTo(final Description description) {
				description.appendText(clazz.getSimpleName() + "(" + member.getId() + ", " + (scope == null ? "anyScope" : scope.getDescription()) + ")");
			}
		}));

		return captured.isEmpty() ? null : captured.get(captured.size() - 1);
	}

	private User createUser() throws UserNotFoundException {
		final User user = UserTestUtils.createUser();
		when(currentContext.findUser(user.getId())).thenReturn(user);
		return user;
	}

	private Scope createScope() throws Exception {
		final Scope scope = ScopeTestUtils.createScope();

		when(currentContext.findScope(scope.getId())).thenReturn(scope);
		return scope;
	}

}
