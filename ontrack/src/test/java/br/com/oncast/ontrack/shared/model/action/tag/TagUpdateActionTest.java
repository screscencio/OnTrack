package br.com.oncast.ontrack.shared.model.action.tag;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.tag.TagUpdateActionEntity;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.TagUpdateAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.color.Color;
import br.com.oncast.ontrack.shared.model.tag.Tag;
import br.com.oncast.ontrack.shared.model.tag.exception.TagNotFoundException;
import br.com.oncast.ontrack.utils.TagTestUtils;

public class TagUpdateActionTest extends ModelActionTest {

	private Tag tag;

	private String newDescription;
	private Color newBackgoundColor;
	private Color newForegroundColor;

	private String previousDescription;
	private Color previousBackgroundColor;
	private Color previousForegroundColor;

	@Before
	public void setup() throws Exception {
		newDescription = "new Description";
		newBackgoundColor = Color.GRAY;
		newForegroundColor = Color.RED;

		tag = TagTestUtils.createTag();
		previousDescription = tag.getDescription();
		previousBackgroundColor = tag.getColorPack().getBackground();
		previousForegroundColor = tag.getColorPack().getForeground();
		when(context.findTag(tag.getId())).thenReturn(tag);
	}

	@Test
	public void shouldUpdateTheTagWithTheGivenTagId() throws Exception {
		executeAction();
		verify(context).findTag(tag.getId());
	}

	@Test
	public void shouldBeAbleToUpdateOnlyTheDescription() throws Exception {
		new TagUpdateAction(tag.getId(), newDescription, null, null).execute(context, actionContext);

		assertEquals(newDescription, tag.getDescription());
		assertEquals(previousForegroundColor, tag.getColorPack().getForeground());
		assertEquals(previousBackgroundColor, tag.getColorPack().getBackground());
	}

	@Test
	public void shouldBeAbleToUpdateOnlyTheForegroundColor() throws Exception {
		new TagUpdateAction(tag.getId(), "", newForegroundColor, null).execute(context, actionContext);
		assertEquals(previousDescription, tag.getDescription());
		assertEquals(newForegroundColor, tag.getColorPack().getForeground());
		assertEquals(previousBackgroundColor, tag.getColorPack().getBackground());
	}

	@Test
	public void shouldBeAbleToUpdateOnlyTheBackgroundColor() throws Exception {
		new TagUpdateAction(tag.getId(), null, null, newBackgoundColor).execute(context, actionContext);
		assertEquals(previousDescription, tag.getDescription());
		assertEquals(previousForegroundColor, tag.getColorPack().getForeground());
		assertEquals(newBackgoundColor, tag.getColorPack().getBackground());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToUpdateAnInexistentTag() throws Exception {
		when(context.findTag(tag.getId())).thenThrow(new TagNotFoundException());
		executeAction();
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToUpdateWhenAllValuesAreInvalid() throws Exception {
		new TagUpdateAction(tag.getId(), "", null, null).execute(context, actionContext);
	}

	@Test
	public void undoShouldReturnTheTagToPreviousState() throws Exception {
		final ModelAction undoAction = executeAction();
		undoAction.execute(context, actionContext);

		assertEquals(previousDescription, tag.getDescription());
		assertEquals(previousForegroundColor, tag.getColorPack().getForeground());
		assertEquals(previousBackgroundColor, tag.getColorPack().getBackground());
	}

	@Override
	protected ModelAction getNewInstance() {
		return new TagUpdateAction(tag.getId(), newDescription, newForegroundColor, newBackgoundColor);
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return TagUpdateAction.class;
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return TagUpdateActionEntity.class;
	}

}
