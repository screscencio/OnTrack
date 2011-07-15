package br.com.oncast.ontrack.server.business;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.mocks.actions.ActionMock;
import br.com.oncast.ontrack.mocks.models.ProjectMock;
import br.com.oncast.ontrack.server.model.project.ProjectSnapshot;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.jpa.PersistenceServiceJpaImpl;
import br.com.oncast.ontrack.shared.exceptions.persistence.PersistenceException;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecuter;

public class BusinessLogicTest {

	private EntityManager entityManager;

	@Before
	public void before() {
		entityManager = Persistence.createEntityManagerFactory("ontrackPU").createEntityManager();
	}

	@After
	public void tearDown() {
		entityManager.close();
	}

	@Test
	public void usingMock() throws Exception {
		final BusinessLogic business = new BusinessLogic(getPersistenceMock());
		shouldConstructAScopeHierarchyFromActions(business);
	}

	@Test
	public void goingToPersistence() throws Exception {
		final BusinessLogic business = new BusinessLogic(new PersistenceServiceJpaImpl());
		shouldConstructAScopeHierarchyFromActions(business);
	}

	private void shouldConstructAScopeHierarchyFromActions(final BusinessLogic business) throws Exception {
		final Project project = ProjectMock.getProject();
		executeActions(ActionMock.getActions(), project);

		for (final ModelAction action : ActionMock.getActions()) {
			business.handleIncomingAction(action);
		}

		final Scope projectScope = business.loadProject().getProjectScope();

		// TODO Remove the following 2 lines when the problem for Mac users to insert a new child is resolved.
		final Scope macWorkaround = projectScope.getChild(0);
		if (macWorkaround.getDescription().equalsIgnoreCase("Example Scope")) projectScope.remove(macWorkaround);
		assertTrue(project.getProjectScope().deepEquals(projectScope));
	}

	private void executeActions(final List<ModelAction> actions, final Project project) throws UnableToCompleteActionException {
		final ProjectContext context = new ProjectContext(project);
		for (final ModelAction action : ActionMock.getActions()) {
			ActionExecuter.executeAction(context, action);
		}
	}

	private PersistenceService getPersistenceMock() {
		return new PersistenceService() {

			private final List<ModelAction> actions = new ArrayList<ModelAction>();

			@Override
			public ProjectSnapshot retrieveProjectSnapshot() throws PersistenceException {
				return new ProjectSnapshot(ProjectMock.getProject(), new Date());
			}

			@Override
			public List<ModelAction> retrieveActionsSince(final Date timestamp) throws PersistenceException {
				return actions;
			}

			@Override
			public void persistAction(final ModelAction action, final Date timestamp) throws PersistenceException {
				actions.add(action);
			}
		};
	}
}
