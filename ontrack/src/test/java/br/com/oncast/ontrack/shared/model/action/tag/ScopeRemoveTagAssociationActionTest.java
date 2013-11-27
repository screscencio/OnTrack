package br.com.oncast.ontrack.shared.model.action.tag;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.tag.ScopeRemoveTagAssociationActionEntity;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveTagAssociationAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.metadata.Metadata;
import br.com.oncast.ontrack.shared.model.metadata.MetadataFactory;
import br.com.oncast.ontrack.shared.model.metadata.TagAssociationMetadata;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.tag.Tag;
import br.com.oncast.ontrack.shared.model.tag.exception.TagNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.TagTestUtils;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.when;

public class ScopeRemoveTagAssociationActionTest extends ModelActionTest {

	private Scope scope;
	private Tag tag;
	private List<Metadata> metadataList;

	@Before
	public void setup() throws Exception {
		metadataList = new ArrayList<Metadata>();
		scope = ScopeTestUtils.createScope();
		tag = TagTestUtils.createTag();
		metadataList.add(MetadataFactory.createTagMetadata(new UUID(), scope, tag));

		when(context.findScope(scope.getId())).thenReturn(scope);
		when(context.findTag(tag.getId())).thenReturn(tag);

		when(context.getMetadataList(scope, TagAssociationMetadata.getType())).thenReturn(metadataList);
	}

	@Test
	public void shouldRemoveTheAssociationOfTheGivenScopeWithTheGivenTag() throws Exception {
		executeAction();
		final TagAssociationMetadata removedMetadata = captureRemovedMetadata();
		assertEquals(scope.getId(), removedMetadata.getSubject().getId());
		assertEquals(tag.getId(), removedMetadata.getTag().getId());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToRemoveAssociationOfAnInexistentScope() throws Exception {
		when(context.findScope(scope.getId())).thenThrow(new ScopeNotFoundException());
		executeAction();
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToRemoveAssociationOfAnInexistentTag() throws Exception {
		when(context.findTag(tag.getId())).thenThrow(new TagNotFoundException());
		executeAction();
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToRemoveAnInexistentAssociation() throws Exception {
		metadataList.clear();
		executeAction();
	}

	@Test
	public void undoShouldAddTheRemovedAssociationAgain() throws Exception {
		final ModelAction undoAction = executeAction();
		captureRemovedMetadata();
		metadataList.clear();

		undoAction.execute(context, actionContext);

		final TagAssociationMetadata addedMetadata = captureAddedMetadata();

		assertEquals(scope, addedMetadata.getSubject());
		assertEquals(tag, addedMetadata.getTag());
	}

	@Override
	protected ModelAction getNewInstance() {
		return new ScopeRemoveTagAssociationAction(scope.getId(), tag.getId());
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return ScopeRemoveTagAssociationAction.class;
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return ScopeRemoveTagAssociationActionEntity.class;
	}

}
