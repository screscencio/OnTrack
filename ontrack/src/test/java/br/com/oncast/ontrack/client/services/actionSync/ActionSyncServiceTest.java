package br.com.oncast.ontrack.client.services.actionSync;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.drycode.api.web.gwt.dispatchService.client.DispatchCallback;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.actionSync.ActionSyncServiceTestUtils.ProjectContextLoadCallback;
import br.com.oncast.ontrack.client.services.actionSync.ActionSyncServiceTestUtils.ValueHolder;
import br.com.oncast.ontrack.client.services.context.ProjectRepresentationProvider;
import br.com.oncast.ontrack.server.business.BusinessLogicMockFactoryTestUtils;
import br.com.oncast.ontrack.server.services.authentication.AuthenticationManager;
import br.com.oncast.ontrack.server.services.persistence.jpa.PersistenceServiceJpaImpl;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeInsertChildAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextResponse;

public class ActionSyncServiceTest {

	private static final String SAME_CLIENT_EXCEPTION_MESSAGE = "This client received the same action it sent to server. Please notify OnTrack team.";
	private ActionSyncServiceTestUtils actionSyncServiceTestUtils;
	private EntityManager entityManager;

	private ProjectRepresentation projectRepresentation;

	@Before
	public void setUp() throws Exception {
		entityManager = Persistence.createEntityManagerFactory("ontrackPU").createEntityManager();
		actionSyncServiceTestUtils = new ActionSyncServiceTestUtils();

		final ProjectRepresentationProvider projectRepresentationProvider = actionSyncServiceTestUtils.getProjectRepresentationProviderMock();
		projectRepresentation = projectRepresentationProvider.getCurrentProjectRepresentation();
		new ActionSyncService(actionSyncServiceTestUtils.getRequestDispatchServiceMock(),
				actionSyncServiceTestUtils.getServerPushClientServiceMock(),
				actionSyncServiceTestUtils.getActionExecutionServiceMock(),
				actionSyncServiceTestUtils.getClientIdentificationProviderMock(),
				projectRepresentationProvider,
				actionSyncServiceTestUtils.getErrorTreatmentServiceMock());

		assureDefaultProjectRepresentationExistance();
	}

	@After
	public void tearDown() {
		entityManager.close();
	}

	@Test
	public void anActionOriginatedInClientShouldNotBeExecutedEvenIfReceivedFromServer() {
		actionSyncServiceTestUtils.getActionExecutionServiceMock().addActionExecutionListener(new ActionExecutionListener() {

			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final Set<UUID> inferenceInfluencedScopeSet,
					final boolean isUserAction) {}
		});

		loadProjectContext(new ProjectContextLoadCallback() {

			@Override
			public void onProjectContextLoaded(final ProjectContext context) {
				actionSyncServiceTestUtils.getActionExecutionServiceMock().onUserActionExecutionRequest(
						new ScopeInsertChildAction(context.getProjectScope().getId(), "filho"));
			}

			@Override
			public void onProjectContextFailed(final Throwable caught) {
				assertEquals(SAME_CLIENT_EXCEPTION_MESSAGE, caught.getMessage());
			}
		});
	}

	@Test
	public void anActionOriginatedInClientShouldOnlyBeExecutedOnceEvenAfterBeingReceivedFromServer() {
		final ValueHolder<Integer> count = actionSyncServiceTestUtils.new ValueHolder<Integer>(0);
		actionSyncServiceTestUtils.getActionExecutionServiceMock().addActionExecutionListener(new ActionExecutionListener() {

			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final Set<UUID> inferenceInfluencedScopeSet,
					final boolean isUserAction) {
				count.setValue(count.getValue() + 1);
			}
		});

		loadProjectContext(new ProjectContextLoadCallback() {

			@Override
			public void onProjectContextLoaded(final ProjectContext context) {
				actionSyncServiceTestUtils.getActionExecutionServiceMock().onUserActionExecutionRequest(
						new ScopeInsertChildAction(context.getProjectScope().getId(), "filho"));
				assertTrue("The action should be executed once.", count.getValue() == 1);
			}

			@Override
			public void onProjectContextFailed(final Throwable caught) {}
		});
	}

	@Test
	public void anActionNotOriginatedInClientShouldBeExecutedAfterBeingReceivedFromServer() {
		final ValueHolder<Integer> count = actionSyncServiceTestUtils.new ValueHolder<Integer>(0);
		actionSyncServiceTestUtils.getActionExecutionServiceMock().addActionExecutionListener(new ActionExecutionListener() {

			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final Set<UUID> inferenceInfluencedScopeSet,
					final boolean isUserAction) {
				count.setValue(count.getValue() + 1);
			}
		});

		loadProjectContext(new ProjectContextLoadCallback() {

			@Override
			public void onProjectContextLoaded(final ProjectContext context) {
				final ModelActionSyncRequest modelActionSyncRequest = new ModelActionSyncRequest(new UUID(),
						projectRepresentation,
						createValidOneActionActionList(context));
				actionSyncServiceTestUtils.getMulticastServiceMock().multicastActionSyncRequest(modelActionSyncRequest);
				Assert.assertTrue("The action should be executed once.", count.getValue() == 1);
			}

			@Override
			public void onProjectContextFailed(final Throwable caught) {
				fail();
			}
		});
	}

	@Test
	public void anActionNotOriginatedInClientShouldNotBeExecutedAsClientActionAfterBeingReceivedFromServer() {
		actionSyncServiceTestUtils.getActionExecutionServiceMock().addActionExecutionListener(new ActionExecutionListener() {

			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final Set<UUID> inferenceInfluencedScopeSet,
					final boolean isUserAction) {
				if (isUserAction)
				Assert.fail("The client should not execute a action from the server (that was not originated from itself) as if it was a client action.");
			}
		});

		loadProjectContext(new ProjectContextLoadCallback() {

			@Override
			public void onProjectContextLoaded(final ProjectContext context) {
				final ModelActionSyncRequest modelActionSyncRequest = new ModelActionSyncRequest(new UUID(),
						projectRepresentation, createValidOneActionActionList(context));
				actionSyncServiceTestUtils.getMulticastServiceMock().multicastActionSyncRequest(modelActionSyncRequest);
			}

			@Override
			public void onProjectContextFailed(final Throwable caught) {
				fail();
			}
		});
	}

	@Test
	public void anActionReceivedFromServerWithClientIdShouldNotBeExecutedInClient() {
		actionSyncServiceTestUtils.getActionExecutionServiceMock().addActionExecutionListener(new ActionExecutionListener() {

			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final Set<UUID> inferenceInfluencedScopeSet,
					final boolean isUserAction) {
				Assert.fail("The client should not execute an action from the server (that was not originated from itself) as if it was a client action.");
			}
		});

		loadProjectContext(new ProjectContextLoadCallback() {

			@Override
			public void onProjectContextLoaded(final ProjectContext context) {
				final ModelActionSyncRequest modelActionSyncRequest = new ModelActionSyncRequest(actionSyncServiceTestUtils
						.getClientIdentificationProviderMock().getClientId(), projectRepresentation,
						createValidOneActionActionList(context));
				actionSyncServiceTestUtils.getMulticastServiceMock().multicastActionSyncRequest(modelActionSyncRequest);
			}

			@Override
			public void onProjectContextFailed(final Throwable caught) {
				assertEquals(SAME_CLIENT_EXCEPTION_MESSAGE, caught.getMessage());
			}
		});
	}

	@Test
	public void anActionReceivedFromServerThatBelongsToAProjectDifferentFromClientCurrentProjectShouldNotBeExecuted() {
		final ProjectRepresentation otherProjectRepresentation = new ProjectRepresentation(2, "other project");

		actionSyncServiceTestUtils.getActionExecutionServiceMock().addActionExecutionListener(new ActionExecutionListener() {

			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final Set<UUID> inferenceInfluencedScopeSet,
					final boolean isUserAction) {
				fail("The client should not execute an action received from the server (that was not originated from itself) if the action project is different than the current client project.");
			}
		});

		loadProjectContext(new ProjectContextLoadCallback() {

			private final String DIFFERENT_PROJECT_EXCEPTION_MESSAGE = "This client received an action for project '2' but it is currently on project '1'. Please notify OnTrack team.";

			@Override
			public void onProjectContextLoaded(final ProjectContext context) {
				final ModelActionSyncRequest modelActionSyncRequest = new ModelActionSyncRequest(new UUID(),
						otherProjectRepresentation,
						createValidOneActionActionList(context));
				actionSyncServiceTestUtils.getMulticastServiceMock().multicastActionSyncRequest(modelActionSyncRequest);
			}

			@Override
			public void onProjectContextFailed(final Throwable caught) {
				assertEquals(DIFFERENT_PROJECT_EXCEPTION_MESSAGE, caught.getMessage());
			}
		});
	}

	@Test
	public void anActionReceivedFromServerThatBelongsToTheSameProjectAsClientCurrentProjectShouldBeExecuted() {
		final ValueHolder<Integer> count = actionSyncServiceTestUtils.new ValueHolder<Integer>(0);

		actionSyncServiceTestUtils.getActionExecutionServiceMock().addActionExecutionListener(new ActionExecutionListener() {

			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final Set<UUID> inferenceInfluencedScopeSet,
					final boolean isUserAction) {
				count.setValue(count.getValue() + 1);
			}
		});

		loadProjectContext(new ProjectContextLoadCallback() {

			@Override
			public void onProjectContextLoaded(final ProjectContext context) {
				final ModelActionSyncRequest modelActionSyncRequest = new ModelActionSyncRequest(new UUID(),
						projectRepresentation,
						createValidOneActionActionList(context));
				actionSyncServiceTestUtils.getMulticastServiceMock().multicastActionSyncRequest(modelActionSyncRequest);
				Assert.assertTrue("The action should be executed once.", count.getValue() == 1);
			}

			@Override
			public void onProjectContextFailed(final Throwable caught) {
				fail();
			}
		});
	}

	private void loadProjectContext(final ProjectContextLoadCallback projectContextLoadCallback) {
		actionSyncServiceTestUtils.getRequestDispatchServiceMock().dispatch(
				new ProjectContextRequest(new UUID(), projectRepresentation.getId()), new DispatchCallback<ProjectContextResponse>() {

					@Override
					public void onSuccess(final ProjectContextResponse response) {
						projectContextLoadCallback.onProjectContextLoaded(new ProjectContext(response.getProject()));
					}

					@Override
					public void onTreatedFailure(final Throwable caught) {}

					@Override
					public void onUntreatedFailure(final Throwable caught) {
						projectContextLoadCallback.onProjectContextFailed(caught);
					}
				});
	}

	private List<ModelAction> createValidOneActionActionList(final ProjectContext context) {
		final List<ModelAction> actionList = new ArrayList<ModelAction>();
		actionList.add(new ScopeInsertChildAction(context.getProjectScope().getId(), "filho"));
		return actionList;
	}

	private void assureDefaultProjectRepresentationExistance() throws Exception {
		final PersistenceServiceJpaImpl persistenceService = spy(new PersistenceServiceJpaImpl());
		doNothing().when(persistenceService).authorize(Mockito.any(User.class), Mockito.any(ProjectRepresentation.class));
		BusinessLogicMockFactoryTestUtils
				.createWithCustomPersistenceMockAndDumbBroadcastMockAndCustomAuthManagerMock(persistenceService, Mockito.mock(AuthenticationManager.class))
				.createProject(projectRepresentation.getName());
	}
}
