package br.com.oncast.ontrack.shared.model.action.release;

import static junit.framework.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.release.ReleaseRenameActionEntity;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.ReleaseRenameAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.mocks.models.ProjectTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.ReleaseTestUtils;
import br.com.oncast.ontrack.utils.mocks.models.ScopeTestUtils;

public class ReleaseRenameActionTest extends ModelActionTest {

	private ProjectContext context;
	private Scope rootScope;
	private Release rootRelease;

	@Before
	public void setUp() {
		rootScope = ScopeTestUtils.getScope();
		rootRelease = ReleaseTestUtils.getRelease();
		context = ProjectTestUtils.createProjectContext(rootScope, rootRelease);

		assertEquals(3, rootRelease.getChildren().size());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void releaseCantBeRenamedToDescriptionWithSlashes() throws UnableToCompleteActionException {
		new ReleaseRenameAction(rootRelease.getChild(0).getId(), "bla/assssd").execute(context, Mockito.mock(ActionContext.class));
	}

	@Test
	public void releaseShouldBeRenamedWithValidDescription() throws UnableToCompleteActionException {
		final Release release = rootRelease.getChild(0);
		new ReleaseRenameAction(release.getId(), "release1").execute(context, Mockito.mock(ActionContext.class));
		assertEquals("release1", release.getDescription());
	}

	@Test
	public void subReleaseFullDescriptionShouldReflectNewReleaseDescriptionWhenReleaseIsRenamed() throws UnableToCompleteActionException {
		final Release release = rootRelease.getChild(0);
		new ReleaseRenameAction(release.getId(), "release1").execute(context, Mockito.mock(ActionContext.class));
		assertEquals("release1/It1", release.getChild(0).getFullDescription());
	}

	@Test
	public void allSubReleasesFullDescriptionsShouldReflectNewReleaseDescriptionWhenReleaseIsRenamed() throws UnableToCompleteActionException {
		final Release release = rootRelease.getChild(0);
		new ReleaseRenameAction(release.getId(), "release1").execute(context, Mockito.mock(ActionContext.class));
		for (final Release subRelease : release.getChildren())
			assertEquals("release1/" + subRelease.getDescription(), subRelease.getFullDescription());
	}

	@Test
	public void releaseDescriptionRenameShouldBeRolledbackAfterUndo() throws UnableToCompleteActionException {
		final Release release = rootRelease.getChild(0);
		final ModelAction rollbackAction = new ReleaseRenameAction(release.getId(), "release1").execute(context, Mockito.mock(ActionContext.class));
		assertEquals("release1", release.getDescription());
		rollbackAction.execute(context, Mockito.mock(ActionContext.class));
		assertEquals("R1", release.getDescription());
	}

	@Test
	public void releaseDescriptionRenameShouldBeUpdatedAfterUndoAndRedo() throws UnableToCompleteActionException {
		final Release release = rootRelease.getChild(0);
		final ModelAction undoAction = new ReleaseRenameAction(release.getId(), "release1").execute(context, Mockito.mock(ActionContext.class));
		assertEquals("release1", release.getDescription());
		final ModelAction redoAction = undoAction.execute(context, Mockito.mock(ActionContext.class));
		assertEquals("R1", release.getDescription());
		redoAction.execute(context, Mockito.mock(ActionContext.class));
		assertEquals("release1", release.getDescription());
	}

	@Test
	public void releaseDescriptionRenameShouldBeUpdatedAfterSeveralUndosAndRedos() throws UnableToCompleteActionException {
		final Release release = rootRelease.getChild(0);
		ModelAction action = new ReleaseRenameAction(release.getId(), "release1").execute(context, Mockito.mock(ActionContext.class));
		assertEquals("release1", release.getDescription());

		for (int i = 0; i < 5; i++) {
			action = action.execute(context, Mockito.mock(ActionContext.class));
			assertEquals("R1", release.getDescription());
			action = action.execute(context, Mockito.mock(ActionContext.class));
			assertEquals("release1", release.getDescription());
		}
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return ReleaseRenameActionEntity.class;
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return ReleaseRenameAction.class;
	}

	@Override
	protected ModelAction getNewInstance() {
		return new ReleaseRenameAction(new UUID(), "");
	}
}
