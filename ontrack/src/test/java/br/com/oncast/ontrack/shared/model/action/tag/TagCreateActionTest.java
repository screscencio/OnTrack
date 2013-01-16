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
import br.com.oncast.ontrack.shared.model.metadata.TagAssociationMetadata;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.tag.Tag;
import br.com.oncast.ontrack.utils.TagTestUtils;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;

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
		assertEquals(backgroundColor, captor.getValue().getColorPack().getBackground());
	}

	@Test
	public void shouldCreateTagWithTheGivenTextColor() throws Exception {
		executeAction();
		final ArgumentCaptor<Tag> captor = ArgumentCaptor.forClass(Tag.class);
		verify(context).addTag(captor.capture());
		assertEquals(textColor, captor.getValue().getColorPack().getForeground());
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
		when(context.findTag(tag.getId())).thenReturn(tag);
		undoAction.execute(context, actionContext);
		verify(context).removeTag(tag);
	}

	@Test
	public void shouldAssociateToTheGivenScopeWhenRequested() throws Exception {
		final Scope scope = ScopeTestUtils.createScope();
		final TagCreateAction action = new TagCreateAction(scope.getId(), description, textColor, backgroundColor);
		when(context.findScope(scope.getId())).thenReturn(scope);
		final Tag createdTag = TagTestUtils.createTag();
		when(context.findTag(action.getReferenceId())).thenReturn(createdTag);

		action.execute(context, actionContext);

		final TagAssociationMetadata addedMetadata = captureAddedMetadata();
		assertEquals(scope, addedMetadata.getSubject());
		assertEquals(createdTag, addedMetadata.getTag());
	}

	@Override
	protected ModelAction getNewInstance() {
		return new TagCreateAction(description, textColor, backgroundColor);
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
