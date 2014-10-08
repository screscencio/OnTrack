package br.com.oncast.ontrack.client.services.user;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;

import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientService;
import br.com.oncast.ontrack.client.services.user.UsersStatusServiceImpl.UsersStatusChangeListener;

import java.lang.reflect.Field;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.EventBus;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(MockitoJUnitRunner.class)
public class UsersStatusServiceImplTest {

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
	public void shouldBeAbleToRemoveListener() throws IllegalArgumentException, IllegalAccessException, NoSuchFieldException, SecurityException {
		final Field field = service.getClass().getDeclaredField("listenersList");
		field.setAccessible(true);
		final Set<UsersStatusChangeListener> listenersList = (Set<UsersStatusChangeListener>) field.get(service);
		final HandlerRegistration handler = service.register(usersStatusChangeListener);
		assertFalse(listenersList.isEmpty());
		handler.removeHandler();
		assertTrue(listenersList.isEmpty());
	}
}