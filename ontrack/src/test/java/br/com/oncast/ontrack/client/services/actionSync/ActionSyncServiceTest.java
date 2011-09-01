package br.com.oncast.ontrack.client.services.actionSync;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.requestDispatch.DispatchCallback;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.actions.ScopeInsertChildAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.requestDispatch.ModelActionSyncRequest;
import br.com.oncast.ontrack.shared.services.requestDispatch.ProjectContextRequest;

import com.octo.gwt.test.GwtTest;

public class ActionSyncServiceTest extends GwtTest {

	private interface ProjectContextLoadCallback {
		void onProjectContextLoaded(ProjectContext context);
	}

	private class ValueHolder<T> {
		T value;

		public ValueHolder(final T initialValue) {
			value = initialValue;
		}

		public T getValue() {
			return value;
		}

		public void setValue(final T value) {
			this.value = value;
		}
	}

	private ActionSyncServiceTestUtils actionSyncServiceTestUtils;

	private EntityManager entityManager;

	@Before
	public void setUp() {
		entityManager = Persistence.createEntityManagerFactory("ontrackPU").createEntityManager();
		actionSyncServiceTestUtils = new ActionSyncServiceTestUtils();
		new ActionSyncService(actionSyncServiceTestUtils.getRequestDispatchServiceMock(),
				actionSyncServiceTestUtils.getServerPushClientServiceMock(),
				actionSyncServiceTestUtils.getActionExecutionServiceMock(),
				actionSyncServiceTestUtils.getClientIdentificationProviderMock());
	}

	@After
	public void tearDown() {
		entityManager.close();
	}

	@Test
	public void test100ActionBeingExecutedReallyFast() {
		final ValueHolder<Integer> count = new ValueHolder<Integer>(0);
		actionSyncServiceTestUtils.getActionExecutionServiceMock().addActionExecutionListener(new ActionExecutionListener() {

			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final Set<UUID> inferenceInfluencedScopeSet,
					final boolean isUserAction) {
				count.setValue(count.getValue() + 1);
				if (!isUserAction) Assert.fail("The client should not execute a action from the server that was originated from itself.");
			}
		});

		loadProjectContext(new ProjectContextLoadCallback() {

			@Override
			public void onProjectContextLoaded(final ProjectContext context) {
				for (int i = 0; i < 100; i++) {
					actionSyncServiceTestUtils.getActionExecutionServiceMock().onUserActionExecutionRequest(
							new ScopeInsertChildAction(context.getProjectScope().getId(), "filho"));
				}
				Assert.assertTrue("All actions should have been executed.", count.getValue() == 100);
			}
		});
	}

	@Test
	public void testActionOriginatedInClientShouldNotBeExecutedAfterBeingReceivedFromServer() {
		actionSyncServiceTestUtils.getActionExecutionServiceMock().addActionExecutionListener(new ActionExecutionListener() {

			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final Set<UUID> inferenceInfluencedScopeSet,
					final boolean isUserAction) {
				if (!isUserAction) Assert.fail("The client should not execute a action from the server that was originated from itself.");
			}
		});

		loadProjectContext(new ProjectContextLoadCallback() {

			@Override
			public void onProjectContextLoaded(final ProjectContext context) {
				actionSyncServiceTestUtils.getActionExecutionServiceMock().onUserActionExecutionRequest(
						new ScopeInsertChildAction(context.getProjectScope().getId(), "filho"));
			}
		});
	}

	@Test
	public void testActionOriginatedInClientShouldOnlyBeExecutedOnceEvenAfterBeingReceivedFromServer() {
		final ValueHolder<Integer> count = new ValueHolder<Integer>(0);
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
				Assert.assertTrue("The action should be executed once.", count.getValue() == 1);
			}
		});
	}

	@Test
	public void testActionNotOriginatedInClientShouldBeExecutedAfterBeingReceivedFromServer() {
		final ValueHolder<Integer> count = new ValueHolder<Integer>(0);
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
				final ModelActionSyncRequest modelActionSyncRequest = new ModelActionSyncRequest(new UUID(), createValidOneActionActionList(context));
				actionSyncServiceTestUtils.getActionBroadcastMock().broadcast(modelActionSyncRequest);
				Assert.assertTrue("The action should be executed once.", count.getValue() == 1);
			}
		});
	}

	@Test
	public void testActionNotOriginatedInClientShouldNotBeExecutedAsClientActionAfterBeingReceivedFromServer() {
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
				final ModelActionSyncRequest modelActionSyncRequest = new ModelActionSyncRequest(new UUID(), createValidOneActionActionList(context));
				actionSyncServiceTestUtils.getActionBroadcastMock().broadcast(modelActionSyncRequest);
			}
		});
	}

	@Test
	public void testActionReceivedFromServerWithClientIdShouldNotBeExecutedInClient() {
		actionSyncServiceTestUtils.getActionExecutionServiceMock().addActionExecutionListener(new ActionExecutionListener() {

			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final Set<UUID> inferenceInfluencedScopeSet,
					final boolean isUserAction) {
				Assert.fail("The client should not execute a action from the server (that was not originated from itself) as if it was a client action.");
			}
		});

		loadProjectContext(new ProjectContextLoadCallback() {

			@Override
			public void onProjectContextLoaded(final ProjectContext context) {
				final ModelActionSyncRequest modelActionSyncRequest = new ModelActionSyncRequest(actionSyncServiceTestUtils
						.getClientIdentificationProviderMock().getClientId(), createValidOneActionActionList(context));
				actionSyncServiceTestUtils.getActionBroadcastMock().broadcast(modelActionSyncRequest);
			}
		});
	}

	private void loadProjectContext(final ProjectContextLoadCallback projectContextLoadCallback) {
		actionSyncServiceTestUtils.getRequestDispatchServiceMock().dispatch(new ProjectContextRequest(), new DispatchCallback<ProjectContext>() {

			@Override
			public void onRequestCompletition(final ProjectContext context) {
				projectContextLoadCallback.onProjectContextLoaded(context);
			}

			@Override
			public void onFailure(final Throwable caught) {
				Assert.fail("Unable to load project.");
			}
		});
	}

	private List<ModelAction> createValidOneActionActionList(final ProjectContext context) {
		final List<ModelAction> actionList = new ArrayList<ModelAction>();
		actionList.add(new ScopeInsertChildAction(context.getProjectScope().getId(), "filho"));
		return actionList;
	}

	@Override
	public String getModuleName() {
		return "br.com.oncast.ontrack.Application";
	}
}
