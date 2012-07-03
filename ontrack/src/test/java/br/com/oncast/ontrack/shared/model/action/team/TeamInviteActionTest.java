package br.com.oncast.ontrack.shared.model.action.team;

import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.team.TeamInviteActionEntity;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.TeamInviteAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.utils.mocks.models.UserTestUtils;

public class TeamInviteActionTest extends ModelActionTest {

	private User invitor;

	@Mock
	private ProjectContext context;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		invitor = UserTestUtils.createUser();
	}

	@Test
	public void shouldNotBeAbleToUndoThisAction() throws Exception {
		assertNull(getNewInstance().execute(context, Mockito.mock(ActionContext.class)));
	}

	@Test
	public void shouldAddUserToProjectContext() throws Exception {
		getNewInstance().execute(context, Mockito.mock(ActionContext.class));

		final ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
		Mockito.verify(context).addUser(captor.capture());
	}

	@Override
	protected ModelAction getNewInstance() {
		return new TeamInviteAction(invitor);
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return TeamInviteAction.class;
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return TeamInviteActionEntity.class;
	}

}
