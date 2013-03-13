package br.com.oncast.ontrack.client.services.applicationState;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.com.oncast.ontrack.client.i18n.ClientErrorMessages;
import br.com.oncast.ontrack.client.services.alerting.ClientAlertingService;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.storage.ClientStorageService;
import br.com.oncast.ontrack.client.ui.events.ScopeSelectionEvent;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;

import com.google.web.bindery.event.shared.EventBus;
import com.googlecode.gwt.test.GwtModule;
import com.googlecode.gwt.test.GwtTest;

@GwtModule("br.com.oncast.ontrack.Application")
public class ClientApplicationStateServiceTest extends GwtTest {

	@Mock
	private EventBus eventBus;

	@Mock
	private ContextProviderService contextProviderService;

	@Mock
	private ClientStorageService clientStorageService;

	@Mock
	private ClientAlertingService alertingService;

	@Mock
	private ClientErrorMessages messages;

	@Mock
	private ProjectContext context;

	private ClientApplicationStateServiceImpl service;

	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		Mockito.when(contextProviderService.getCurrent()).thenReturn(context);
		Mockito.when(context.getProjectScope()).thenReturn(ScopeTestUtils.createScope());

		service = new ClientApplicationStateServiceImpl(eventBus, contextProviderService, clientStorageService, alertingService, messages);
	}

	@Test
	public void shouldSelectPreviouslySelectedScopeWhenTheGivenScopeIdIsNotNull() throws Exception {
		final Scope storedSelectedScope = createScope();
		setAsLoadedSelectedScope(storedSelectedScope);

		service.restore(null);
		assertFiredScopeSelectionEventFor(storedSelectedScope);
	}

	@Test
	public void shouldSelectTheGivenScopeWhenTheGivenScopeIdIsNotNull() throws Exception {
		setAsLoadedSelectedScope(createScope());

		final Scope givenScope = createScope();

		service.restore(givenScope.getId());
		assertFiredScopeSelectionEventFor(givenScope);
	}

	public void assertFiredScopeSelectionEventFor(final Scope scope) {
		Mockito.verify(eventBus).fireEvent(Mockito.argThat(new ArgumentMatcher<ScopeSelectionEvent>() {
			@Override
			public boolean matches(final Object arg0) {
				final ScopeSelectionEvent e = (ScopeSelectionEvent) arg0;
				return e.getTargetScope().equals(scope);
			}
		}));
	}

	public void setAsLoadedSelectedScope(final Scope scope) {
		Mockito.when(clientStorageService.loadSelectedScopeId(Mockito.any(UUID.class))).thenReturn(scope.getId());
	}

	private Scope createScope() throws Exception {
		final Scope scope = ScopeTestUtils.createScope();
		Mockito.when(context.findScope(scope.getId())).thenReturn(scope);
		return scope;
	}
}
