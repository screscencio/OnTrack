package br.com.oncast.ontrack.shared.model.action.tag;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.tag.TagCreateActionEntity;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.TagCreateAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.color.Color;
import br.com.oncast.ontrack.shared.model.tag.Tag;

public class TagCreateActionTest extends ModelActionTest {

	private String description;
	private Color backgroundColor;
	private Color textColor;

	@Before
	public void setup() throws Exception {
		description = "description";
		backgroundColor = Color.GREEN;
		textColor = Color.YELLOW;
	}

	@Test
	public void shouldCreateTagWithTheGivenDescription() throws Exception {
		executeAction();
		final ArgumentCaptor<Tag> captor = ArgumentCaptor.forClass(Tag.class);
		verify(context).addTag(captor.capture());
		assertEquals(description, captor.getValue().getDescription());
	}

	@Test
	public void shouldCreateTagWithTheGivenBackgroundColor() throws Exception {
		executeAction();
		final ArgumentCaptor<Tag> captor = ArgumentCaptor.forClass(Tag.class);
		verify(context).addTag(captor.capture());
		assertEquals(backgroundColor, captor.getValue().getBackgroundColor());
	}

	@Test
	public void shouldCreateTagWithTheGivenTextColor() throws Exception {
		executeAction();
		final ArgumentCaptor<Tag> captor = ArgumentCaptor.forClass(Tag.class);
		verify(context).addTag(captor.capture());
		assertEquals(textColor, captor.getValue().getTextColor());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToCreateTagsWithSameId() throws Exception {
		when(context.hasTag(Mockito.anyString())).thenReturn(true);
		executeAction();
	}

	@Test
	public void undoShouldRemoveTheCreatedTag() throws Exception {
		final ModelAction undoAction = executeAction();
		final ArgumentCaptor<Tag> captor = ArgumentCaptor.forClass(Tag.class);
		verify(context).addTag(captor.capture());

		final Tag tag = captor.getValue();
		when(context.removeTag(tag.getId())).thenReturn(tag);
		undoAction.execute(context, actionContext);
		verify(context).removeTag(tag.getId());
	}

	@Override
	protected ModelAction getNewInstance() {
		return new TagCreateAction(description, backgroundColor, textColor);
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return TagCreateAction.class;
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return TagCreateActionEntity.class;
	}

}
