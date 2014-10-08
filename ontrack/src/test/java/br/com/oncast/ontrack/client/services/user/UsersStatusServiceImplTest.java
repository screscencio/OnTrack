package br.com.oncast.ontrack.client.services.user;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;

import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientService;
import br.com.oncast.ontrack.client.services.user.UsersStatusServiceImpl.UsersStatusChangeListener;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.utils.mocks.models.UserRepresentationTestUtils;

import java.lang.reflect.Field;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.common.collect.SetMultimap;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.EventBus;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class UsersStatusServiceImplTest {

	@Rule
	public ExpectedException expected = ExpectedException.none();

	@Mock
	private DispatchService requestDispatchService;

	@Mock
	private ContextProviderService contextProviderService;

	@Mock
	private ServerPushClientService serverPushClientService;

	@Mock
	private EventBus eventBus;

	@Mock
	private UsersStatusChangeListener usersStatusChangeListener;

	@Mock
	private UserSpecificStatusChangeListener listener;

	private UsersStatusServiceImpl service;

	@Before
	public void setup() {
		service = new UsersStatusServiceImpl(requestDispatchService, contextProviderService, serverPushClientService, eventBus);
	}

	@Test
	public void shouldRegisterServerPushEventHandlers() {
		final HandlerRegistration handler = service.register(usersStatusChangeListener);
		assertNotNull(handler);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldAddListenerOnRegister() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		final Field field = service.getClass().getDeclaredField("listenersList");
		field.setAccessible(true);
		final Set<UsersStatusChangeListener> listenersList = (Set<UsersStatusChangeListener>) field.get(service);
		service.register(usersStatusChangeListener);
		assertFalse(listenersList.isEmpty());
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldBeAbleToRemoveUserStatusChangeListener() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		final Field field = service.getClass().getDeclaredField("listenersList");
		field.setAccessible(true);
		final Set<UsersStatusChangeListener> listenersList = (Set<UsersStatusChangeListener>) field.get(service);
		final HandlerRegistration handler = service.register(usersStatusChangeListener);
		assertFalse(listenersList.isEmpty());
		handler.removeHandler();
		assertTrue(listenersList.isEmpty());
	}

	@Test
	public void getActiveUsers_shouldReturnAnException_WhenNoUsersLoaded() {
		expected.expect(RuntimeException.class);
		expected.expectMessage("There is no loaded active users");
		service.getActiveUsers();
	}

	@Test
	public void getOnlineUsers_shouldReturnAnException_WhenNoUsersLoaded() {
		expected.expect(RuntimeException.class);
		expected.expectMessage("There is no loaded online users");
		service.getOnlineUsers();
	}

	@Test
	public void shouldRegisterListenerForSpecificUser() {
		final HandlerRegistration handler = service.registerListenerForSpecificUser(UserRepresentationTestUtils.createUser(), listener);
		assertNotNull(handler);
	}

	@SuppressWarnings("unchecked")
	@Test
	public void shouldBeAbleToRemoveUserSpecificStatusChangeListener() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		final Field field = service.getClass().getDeclaredField("userSpecificListeners");
		field.setAccessible(true);
		final SetMultimap<UserRepresentation, UserSpecificStatusChangeListener> listenersList = 
				(SetMultimap<UserRepresentation, UserSpecificStatusChangeListener>) field.get(service);
		final HandlerRegistration handler = service.registerListenerForSpecificUser(UserRepresentationTestUtils.createUser(), listener);
		assertFalse(listenersList.isEmpty());
		handler.removeHandler();
		assertTrue(listenersList.isEmpty());
	}
}