package br.com.oncast.ontrack.shared.model.action.tag;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.tag.TagRemoveActionEntity;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.TagRemoveAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.tag.Tag;
import br.com.oncast.ontrack.utils.TagTestUtils;

public class TagRemoveActionTest extends ModelActionTest {

	private Tag tag;

	@Before
	public void setup() throws Exception {
		tag = TagTestUtils.create();
		when(context.removeTag(tag.getId())).thenReturn(tag);
	}

	@Test
	public void shouldRemoveTagWithTheGivenId() throws Exception {
		executeAction();
		verify(context).removeTag(tag.getId());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToRemoveAnInexistantTag() throws Exception {
		when(context.removeTag(tag.getId())).thenReturn(null);
		executeAction();
	}

	@Test
	public void undoShouldAddTheSameTagAgain() throws Exception {
		final ModelAction undoAction = executeAction();

		undoAction.execute(context, actionContext);
		verify(context).addTag(Mockito.eq(tag));
	}

	@Override
	protected ModelAction getNewInstance() {
		return new TagRemoveAction(tag.getId());
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return TagRemoveAction.class;
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return TagRemoveActionEntity.class;
	}

}
