package br.com.oncast.ontrack.client.services.user;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchService;

import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.serverPush.ServerPushClientService;
import br.com.oncast.ontrack.client.services.user.UsersStatusServiceImpl.UsersStatusChangeListener;

import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.web.bindery.event.shared.EventBus;

import static org.junit.Assert.assertNotNull;

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

	@Test
	public void shouldRegisterServerPushEventHandlers() {
		MockitoAnnotations.initMocks(this);
		final UsersStatusServiceImpl service = new UsersStatusServiceImpl(requestDispatchService, contextProviderService, serverPushClientService, eventBus);
		final HandlerRegistration registration = service.register(usersStatusChangeListener);
		assertNotNull(registration);
	}
}