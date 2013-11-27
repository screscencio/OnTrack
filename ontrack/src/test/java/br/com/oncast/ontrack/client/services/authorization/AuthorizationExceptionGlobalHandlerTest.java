package br.com.oncast.ontrack.client.services.authorization;

import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.places.ApplicationPlaceController;
import br.com.oncast.ontrack.client.ui.places.projectSelection.ProjectSelectionPlace;
import br.com.oncast.ontrack.shared.exceptions.authorization.AuthorizationException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthorizationExceptionGlobalHandlerTest {

	@Mock
	private ApplicationPlaceController placeController;

	@Mock
	private ContextProviderService contextProvider;

	private AuthorizationExceptionGlobalHandler handler;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		handler = new AuthorizationExceptionGlobalHandler(placeController, contextProvider);
	}

	@Test
	public void shouldGoToProjectSelectionPlace() throws Exception {
		handler.handle(new AuthorizationException());
		verify(placeController).goTo(Mockito.any(ProjectSelectionPlace.class));
	}

	@Test
	public void shouldNotUnloadProjectContextWhenThereIsNoSpecifiedProjectId() throws Exception {
		handler.handle(new AuthorizationException());
		verify(contextProvider, never()).unloadProjectContext();
	}

	@Test
	public void shouldNotUnloadProjectContextWhenThereIsNoAvailableContextForTheGivenProject() throws Exception {
		final UUID projectId = new UUID();

		when(contextProvider.isContextAvailable(Mockito.any(UUID.class))).thenReturn(true);
		when(contextProvider.isContextAvailable(projectId)).thenReturn(false);

		handler.handle(new AuthorizationException().setProjectId(projectId));
		verify(contextProvider, never()).unloadProjectContext();
	}

	@Test
	public void shouldUnloadProjectContextWhenThereIsAvailableContextForTheGivenProject() throws Exception {
		final UUID projectId = new UUID();

		when(contextProvider.isContextAvailable(projectId)).thenReturn(true);

		handler.handle(new AuthorizationException().setProjectId(projectId));
		verify(contextProvider).unloadProjectContext();
	}

}
