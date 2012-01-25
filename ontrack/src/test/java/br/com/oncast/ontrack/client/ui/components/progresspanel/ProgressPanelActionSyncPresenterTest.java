package br.com.oncast.ontrack.client.ui.components.progresspanel;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.ui.components.progresspanel.ProgressPanelActionSyncPresenter.Display;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseAction;
import br.com.oncast.ontrack.shared.model.action.ScopeAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.mocks.models.ReleaseTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.ScopeTestUtils;

public class ProgressPanelActionSyncPresenterTest {

	private ActionExecutionListener actionExecutionListener;
	private Display display;
	private ProjectContext context;
	private Release myRelease;
	private Scope scopePresentOnDisplay;

	@Before
	public void setUp() throws Exception {
		display = Mockito.mock(Display.class);
		context = mock(ProjectContext.class);
		scopePresentOnDisplay = createScope();
		myRelease = createRelease();
		when(display.containsScope(Mockito.any(Scope.class))).thenAnswer(new Answer<Boolean>() {
			@Override
			public Boolean answer(final InvocationOnMock invocation) throws Throwable {
				return scopePresentOnDisplay.equals(invocation.getArguments()[0]);
			}
		});

		final ActionExecutionService actionExecutionServiceMock = mock(ActionExecutionService.class);
		final ArgumentCaptor<ActionExecutionListener> captor = ArgumentCaptor.forClass(ActionExecutionListener.class);
		Mockito.doNothing().when(actionExecutionServiceMock).addActionExecutionListener(captor.capture());

		new ProgressPanelActionSyncPresenter(myRelease, display, actionExecutionServiceMock);
		actionExecutionListener = captor.getValue();
	}

	@Test
	public void shouldNotBrokeWhenAnUnhandledActionOccurs() throws Exception {
		onActionExecution(createBrandNewModelAction(null));
		shouldNotBeUpdated();
	}

	@Test
	public void shouldDoNothingWhenAScopeActionWithDifferentReleaseAndScopeNotPresentOnDisplayOccurs() throws Exception {
		final Scope scopeNotPresentOnDisplay = createScope();
		createRelease().addScope(scopeNotPresentOnDisplay);

		onActionExecution(createScopeAction(scopeNotPresentOnDisplay.getId()));
		shouldNotBeUpdated();
	}

	@Test
	public void shouldDoNothingWhenAScopeActionWithScopeWithoutReleaseAndNotPresentOnDisplayOccurs() throws Exception {
		final Scope scopeNotPresentOnDisplay = createScope();

		onActionExecution(createScopeAction(scopeNotPresentOnDisplay.getId()));
		shouldNotBeUpdated();
	}

	@Test
	public void shouldUpdateDisplayWhenAScopeActionWithTheSameReleaseOccurs() throws Exception {
		final Scope notRelatedScopeOnSameRelease = createScope();
		myRelease.addScope(notRelatedScopeOnSameRelease);

		onActionExecution(createScopeAction(notRelatedScopeOnSameRelease.getId()));
		shouldBeUpdated();
	}

	@Test
	public void shouldUpdateDisplayWhenAScopeActionWithAScopeThatIsPresentOnDisplay() throws Exception {
		scopePresentOnDisplay.setRelease(ReleaseTestUtils.createRelease());

		onActionExecution(createScopeAction(scopePresentOnDisplay.getId()));
		shouldBeUpdated();
	}

	@Test
	public void shouldNotUpdateDisplayWhenAReleaseActionWithDifferentReleaseOccurs() throws Exception {
		final Release differentRelease = createRelease();
		onActionExecution(createReleaseAction(differentRelease.getId()));
		shouldNotBeUpdated();
	}

	@Test
	public void shouldUpdateDisplayWhenAReleaseActionWithTheSameReleaseOccurs() throws Exception {
		onActionExecution(createReleaseAction(myRelease.getId()));
		shouldBeUpdated();
	}

	private ModelAction createReleaseAction(final UUID referenceId) {
		return createAction(ReleaseAction.class, referenceId);
	}

	private void onActionExecution(final ModelAction action) throws UnableToCompleteActionException {
		actionExecutionListener.onActionExecution(action, context, new HashSet<UUID>(), true);
	}

	private void addToContext(final Release release) throws ReleaseNotFoundException {
		when(context.findRelease(release.getId())).thenReturn(release);
	}

	private void addToContext(final Scope scope) throws ScopeNotFoundException {
		when(context.findScope(scope.getId())).thenReturn(scope);
	}

	private ModelAction createBrandNewModelAction(final UUID referenceId) {
		return createAction(ModelAction.class, referenceId);
	}

	private ModelAction createScopeAction(final UUID referenceId) {
		return createAction(ScopeAction.class, referenceId);
	}

	private ModelAction createAction(final Class<? extends ModelAction> clazz, final UUID referenceId) {
		final ModelAction mock = Mockito.mock(clazz);
		when(mock.getReferenceId()).thenReturn(referenceId);
		return mock;
	}

	private Release createRelease() throws ReleaseNotFoundException {
		final Release release = ReleaseTestUtils.createRelease();
		addToContext(release);
		return release;
	}

	private Scope createScope() throws ScopeNotFoundException {
		final Scope scope = ScopeTestUtils.createScope();
		addToContext(scope);
		return scope;
	}

	private void shouldBeUpdated() {
		verify(display, times(1)).update();
	}

	private void shouldNotBeUpdated() {
		verify(display, never()).update();
	}

}
