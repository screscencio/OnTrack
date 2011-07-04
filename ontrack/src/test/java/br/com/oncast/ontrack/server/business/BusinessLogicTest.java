package br.com.oncast.ontrack.server.business;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Test;

import br.com.oncast.ontrack.server.model.project.ProjectSnapshot;
import br.com.oncast.ontrack.server.services.persistence.PersistenceService;
import br.com.oncast.ontrack.server.services.persistence.jpa.PersistenceServiceJpaImpl;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToHandleActionException;
import br.com.oncast.ontrack.shared.exceptions.business.UnableToLoadProjectException;
import br.com.oncast.ontrack.shared.exceptions.persistence.PersistenceException;
import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.project.Project;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeInsertChildAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeInsertParentAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeInsertSiblingDownAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeInsertSiblingUpAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeMoveDownAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeMoveLeftAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeMoveRightAction;
import br.com.oncast.ontrack.shared.model.scope.actions.ScopeMoveUpAction;
import br.com.oncast.ontrack.shared.model.scope.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class BusinessLogicTest {

	private Project getProject() {
		return new Project(new Scope("Project", new UUID("0")), new Release("proj"));
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

	private void shouldConstructAScopeHierarchyFromActions(final BusinessLogic business) throws UnableToCompleteActionException, UnableToHandleActionException,
			UnableToLoadProjectException {

		final Project project = getProject();
		executeActions(getActions(), project);

		for (final ModelAction action : getActions()) {
			business.handleIncomingAction(action);
		}

		assertTrue(project.getProjectScope().deepEquals(business.loadProject().getProjectScope()));
	}

	private void executeActions(final List<ModelAction> actions, final Project project) throws UnableToCompleteActionException {
		for (final ModelAction action : getActions()) {
			action.execute(new ProjectContext(project));
		}
	}

	private List<ModelAction> getActions() {
		final List<ModelAction> actions = new ArrayList<ModelAction>();

		final ScopeInsertChildAction insertChild1 = new ScopeInsertChildAction(getProject().getProjectScope().getId(), "1");
		insertChild1.getNewScopeId();

		actions.add(insertChild1);
		actions.add(new ScopeInsertChildAction(getProject().getProjectScope().getId(), "2"));
		actions.add(new ScopeInsertSiblingUpAction(insertChild1.getNewScopeId(), "Before 1"));
		actions.add(new ScopeInsertSiblingDownAction(insertChild1.getNewScopeId(), "After 1"));

		final ScopeInsertChildAction insertChild2 = new ScopeInsertChildAction(insertChild1.getNewScopeId(), "1.1");
		actions.add(insertChild2);
		actions.add(new ScopeInsertParentAction(insertChild2.getNewScopeId(), "Parent of 1.1"));

		final ScopeInsertChildAction insertChild3 = new ScopeInsertChildAction(getProject().getProjectScope().getId(), "3");
		actions.add(insertChild3);
		actions.add(new ScopeMoveRightAction(insertChild3.getNewScopeId()));
		actions.add(new ScopeMoveLeftAction(insertChild3.getNewScopeId()));
		actions.add(new ScopeMoveDownAction(insertChild1.getNewScopeId()));
		actions.add(new ScopeMoveUpAction(insertChild1.getNewScopeId()));

		return actions;
	}

	private PersistenceService getPersistenceMock() {
		return new PersistenceService() {

			private final List<ModelAction> actions = new ArrayList<ModelAction>();

			@Override
			public ProjectSnapshot retrieveProjectSnapshot() throws PersistenceException {
				return new ProjectSnapshot(getProject(), new Date());
			}

			@Override
			public List<ModelAction> retrieveActionsSince(final Date timestamp) throws PersistenceException {
				return actions;
			}

			@Override
			public void persist(final ModelAction action, final Date timestamp) throws PersistenceException {
				actions.add(action);
			}
		};
	}
}
