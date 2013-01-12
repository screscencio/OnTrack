package br.com.oncast.ontrack.shared.model.action.tag;

import static br.com.oncast.ontrack.shared.model.metadata.MetadataFactory.createTagMetadata;
import static br.com.oncast.ontrack.utils.model.ScopeTestUtils.createScope;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.tag.TagRemoveActionEntity;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.TagRemoveAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.metadata.HasMetadata;
import br.com.oncast.ontrack.shared.model.metadata.TagAssociationMetadata;
import br.com.oncast.ontrack.shared.model.tag.Tag;
import br.com.oncast.ontrack.shared.model.tag.exception.TagNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.TagTestUtils;

public class TagRemoveActionTest extends ModelActionTest {

	private Tag tag;

	@Before
	public void setup() throws Exception {
		tag = TagTestUtils.createTag();
		when(context.findTag(tag.getId())).thenReturn(tag);
	}

	@Test
	public void shouldRemoveTagWithTheGivenId() throws Exception {
		executeAction();
		verify(context).removeTag(tag);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToRemoveAnInexistantTag() throws Exception {
		when(context.findTag(tag.getId())).thenThrow(new TagNotFoundException());
		executeAction();
	}

	@Test
	public void undoShouldAddTheSameTagAgain() throws Exception {
		final ModelAction undoAction = executeAction();

		undoAction.execute(context, actionContext);
		verify(context).addTag(Mockito.eq(tag));
	}

	@Test
	public void shouldRemoveExistentAssociations() throws Exception {
		final List<TagAssociationMetadata> metadataList = configureToReturnMetadataList(
				createTagMetadata(new UUID(), createScope(), tag),
				createTagMetadata(new UUID(), createScope(), tag),
				createTagMetadata(new UUID(), createScope(), tag),
				createTagMetadata(new UUID(), createScope(), tag)
				);

		executeAction();

		final ArgumentCaptor<TagAssociationMetadata> captor = ArgumentCaptor.forClass(TagAssociationMetadata.class);
		verify(context, atLeastOnce()).removeMetadata(captor.capture());
		for (final TagAssociationMetadata removedMetadata : captor.getAllValues()) {
			assertTrue(metadataList.contains(removedMetadata));
		}
	}

	@Test
	public void undoShouldAddRemovedAssociationsAgain() throws Exception {
		final List<TagAssociationMetadata> metadataList = configureToReturnMetadataList(
				createTagMetadata(new UUID(), createScope(), tag),
				createTagMetadata(new UUID(), createScope(), tag),
				createTagMetadata(new UUID(), createScope(), tag),
				createTagMetadata(new UUID(), createScope(), tag)
				);

		final ModelAction undoAction = executeAction();
		configureToReturnMetadataList();

		undoAction.execute(context, actionContext);

		final ArgumentCaptor<TagAssociationMetadata> captor = ArgumentCaptor.forClass(TagAssociationMetadata.class);
		verify(context, atLeastOnce()).addMetadata(captor.capture());
		for (final TagAssociationMetadata addedMetadata : captor.getAllValues()) {
			assertTrue(metadataList.contains(addedMetadata));
		}
	}

	private List<TagAssociationMetadata> configureToReturnMetadataList(final TagAssociationMetadata... metadata) {
		final List<TagAssociationMetadata> metadataList = Arrays.asList(metadata);
		when(context.<TagAssociationMetadata> getAllMetadata(TagAssociationMetadata.getType())).thenReturn(metadataList);
		when(context.<TagAssociationMetadata> getMetadataList(any(HasMetadata.class), eq(TagAssociationMetadata.getType()))).thenReturn(metadataList);

		return metadataList;
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
