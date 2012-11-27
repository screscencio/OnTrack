package br.com.oncast.ontrack.shared.model.action.scope;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeDeclareProgressActionEntity;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareProgressAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.ReleaseFactoryTestUtil;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.tags.UserAssociationTag;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.model.ProjectTestUtils;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;
import br.com.oncast.ontrack.utils.model.UserTestUtils;

public class ScopeDeclareProgressActionTest extends ModelActionTest {

	private Scope scope;
	private ProjectContext context;

	@Before
	public void setUp() {
		scope = ScopeTestUtils.createScope("scope description");
		context = ProjectTestUtils.createProjectContext(scope, ReleaseFactoryTestUtil.create(""));
	}

	@Test
	public void notStartedShouldBeTheDefaultProgress() throws UnableToCompleteActionException {
		assertThatProgressIs(ProgressState.NOT_STARTED);
	}

	@Test
	public void shouldSetProgressToAScope() throws UnableToCompleteActionException {
		new ScopeDeclareProgressAction(scope.getId(), "Not started").execute(context, actionContext);

		assertEquals(ProgressState.NOT_STARTED.getDescription(), scope.getProgress().getDescription());
	}

	@Test
	public void shouldResetProgressOfScope() throws UnableToCompleteActionException {
		ScopeTestUtils.setProgress(scope, "Under work");

		new ScopeDeclareProgressAction(scope.getId(), "").execute(context, actionContext);

		assertEquals("", scope.getProgress().getDescription());
		assertThatProgressIs(ProgressState.NOT_STARTED);
	}

	@Test
	public void shouldSetProgressOfScopeToUnderWorkIfDescriptionDoesntFitInOthersStatuses() throws UnableToCompleteActionException {
		new ScopeDeclareProgressAction(scope.getId(), "Anything").execute(context, actionContext);

		assertEquals("Anything", scope.getProgress().getDescription());
		assertThatProgressIs(ProgressState.UNDER_WORK);
	}

	@Test
	public void shouldSetProgressOfScopeToUnderWork() throws UnableToCompleteActionException {
		new ScopeDeclareProgressAction(scope.getId(), "Under work").execute(context, actionContext);

		assertEquals("Under work", scope.getProgress().getDescription());
		assertThatProgressIs(ProgressState.UNDER_WORK);
	}

	@Test
	public void shouldSetProgressOfScopeToDone() throws UnableToCompleteActionException {
		new ScopeDeclareProgressAction(scope.getId(), "Done").execute(context, actionContext);

		assertEquals(ProgressState.DONE.getDescription(), scope.getProgress().getDescription());
		assertThatProgressIs(ProgressState.DONE);
	}

	@Test
	public void shouldSetProgressStatusOfScopeToNotStartedConsideringVariations() throws UnableToCompleteActionException {
		final String[] acceptableNotStartedDescriptions = { "NotStarted", "Not Started", "Not started", "not started", "not_started", "Not_Started", "NS",
				"ns", "N", "n" };
		for (final String notStartedDespription : acceptableNotStartedDescriptions) {
			new ScopeDeclareProgressAction(scope.getId(), notStartedDespription).execute(context, actionContext);
			assertThatProgressIs(ProgressState.NOT_STARTED);
		}
	}

	@Test
	public void shouldSetProgressStatusOfScopeToUnderWorkConsideringVariations() throws UnableToCompleteActionException {
		final String[] acceptableUnderWorkDescriptions = { "Under work", "Under_work", "under work", "design", "coding", "testing", "acceptance", "anything" };
		for (final String underWorkDespription : acceptableUnderWorkDescriptions) {
			new ScopeDeclareProgressAction(scope.getId(), underWorkDespription).execute(context, actionContext);
			assertThatProgressIs(ProgressState.UNDER_WORK);
		}
	}

	@Test
	public void shouldSetProgressStatusOfScopeToDoneConsideringVariations() throws UnableToCompleteActionException {
		final String[] acceptableDoneDescriptions = { "Done", "DONE", "done", "DN", "Dn", "dn", "D", "d" };
		for (final String doneDespription : acceptableDoneDescriptions) {
			new ScopeDeclareProgressAction(scope.getId(), doneDespription).execute(context, actionContext);
			assertThatProgressIs(ProgressState.DONE);
		}
	}

	@Test
	public void shouldRevertChangesAfterARollback() throws UnableToCompleteActionException {
		ScopeTestUtils.setProgress(scope, "Done");
		assertEquals(ProgressState.DONE.getDescription(), scope.getProgress().getDescription());
		assertThatProgressIs(ProgressState.DONE);

		final ScopeDeclareProgressAction progressAction = new ScopeDeclareProgressAction(scope.getId(), "Under work");
		final ModelAction rollbackAction = progressAction.execute(context, actionContext);

		assertEquals("Under work", scope.getProgress().getDescription());
		assertThatProgressIs(ProgressState.UNDER_WORK);

		rollbackAction.execute(context, actionContext);

		assertEquals(ProgressState.DONE.getDescription(), scope.getProgress().getDescription());
		assertThatProgressIs(ProgressState.DONE);
	}

	@Test
	public void shouldRevertChangesAfterARollback2() throws UnableToCompleteActionException {
		assertThatProgressIs(ProgressState.NOT_STARTED);

		final ScopeDeclareProgressAction progressAction = new ScopeDeclareProgressAction(scope.getId(), "Under work");
		final ModelAction rollbackAction = progressAction.execute(context, actionContext);

		assertEquals("Under work", scope.getProgress().getDescription());
		assertThatProgressIs(ProgressState.UNDER_WORK);

		rollbackAction.execute(context, actionContext);

		assertThatProgressIs(ProgressState.NOT_STARTED);
	}

	@Test
	public void whenDeclaringAScopeAsUnderWorkAndNoUserIsAssociatedItShouldAssociateTheActionAuthor() throws Exception {
		final User author = UserTestUtils.createUser();

		when(actionContext.getUserId()).thenReturn(author.getId());
		context.addUser(author);

		final ScopeDeclareProgressAction progressAction = new ScopeDeclareProgressAction(scope.getId(), "Under work");
		progressAction.execute(context, actionContext);

		final List<UserAssociationTag> tags = context.getTags(scope, UserAssociationTag.getType());
		final User associatedUser = tags.get(0).getUser();
		assertEquals(author, associatedUser);
	}

	@Test
	public void undoShouldRemoveCreatedAssociations() throws Exception {
		final User author = UserTestUtils.createUser();

		when(actionContext.getUserId()).thenReturn(author.getId());
		context.addUser(author);

		final ScopeDeclareProgressAction progressAction = new ScopeDeclareProgressAction(scope.getId(), "Under work");
		final ModelAction undoAction = progressAction.execute(context, actionContext);

		final User associatedUser = context.<UserAssociationTag> getTags(scope, UserAssociationTag.getType()).get(0).getUser();
		assertEquals(author, associatedUser);

		undoAction.execute(context, actionContext);
		assertTrue(context.getTags(scope, UserAssociationTag.getType()).isEmpty());
	}

	private void assertThatProgressIs(final Progress.ProgressState status) {
		assertEquals(status, scope.getProgress().getState());
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return ScopeDeclareProgressActionEntity.class;
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return ScopeDeclareProgressAction.class;
	}

	@Override
	protected ModelAction getNewInstance() {
		return new ScopeDeclareProgressAction(new UUID(), "");
	}
}
