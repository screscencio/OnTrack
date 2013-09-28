package br.com.oncast.ontrack.shared.model.action.team;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.team.TeamDeclareProfileActionEntity;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.TeamDeclareProfileAction;
import br.com.oncast.ontrack.shared.model.user.Profile;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.fail;

public class TeamDeclareProfileActionTest extends ModelActionTest {

	@Before
	public void setUp() throws Exception {}

	// FIXME implement the tests for this action
	@Ignore("//TODO Implement tests")
	@Test
	public void test() {
		fail("Not yet implemented");
	}

	@Override
	protected ModelAction getNewInstance() {
		return new TeamDeclareProfileAction(new UUID(), Profile.PEOPLE_MANAGER);
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return TeamDeclareProfileAction.class;
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return TeamDeclareProfileActionEntity.class;
	}

}
