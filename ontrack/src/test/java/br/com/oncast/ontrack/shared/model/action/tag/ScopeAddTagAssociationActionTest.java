package br.com.oncast.ontrack.shared.model.action.tag;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.tag.ScopeAddTagAssociationActionEntity;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.ScopeAddTagAssociationAction;
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

public class ScopeAddTagAssociationActionTest extends ModelActionTest {

	private Scope scope;
	private Tag tag;

	@Before
	public void setup() throws Exception {
		scope = ScopeTestUtils.createScope();
		tag = TagTestUtils.create();

		when(context.findScope(scope.getId())).thenReturn(scope);
		when(context.findTag(tag.getId())).thenReturn(tag);
	}

	@Test
	public void shouldAssociateTheGivenScopeWithTheGivenTag() throws Exception {
		executeAction();
		final TagAssociationMetadata tagMetadata = captureAddedMetadata();
		assertEquals(scope.getId(), tagMetadata.getSubject().getId());
		assertEquals(tag.getId(), tagMetadata.getTag().getId());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToAssociateAnInexistentScope() throws Exception {
		when(context.findScope(scope.getId())).thenThrow(new ScopeNotFoundException());
		executeAction();
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToAssociateAnInexistentTag() throws Exception {
		when(context.findTag(tag.getId())).thenThrow(new TagNotFoundException());
		executeAction();
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToAssociateTheSameScopeWithSameTagTwice() throws Exception {
		final List<Metadata> list = new ArrayList<Metadata>();
		list.add(MetadataFactory.createTagMetadata(new UUID(), scope, tag));
		when(context.getMetadataList(scope, TagAssociationMetadata.getType())).thenReturn(list);

		executeAction();
	}

	@Test
	public void undoShouldRemoveAddedAssociation() throws Exception {
		final ModelAction undoAction = executeAction();

		final Metadata addedMetadata = captureAddedMetadata();
		final List<Metadata> list = new ArrayList<Metadata>();
		list.add(addedMetadata);
		when(context.getMetadataList(scope, TagAssociationMetadata.getType())).thenReturn(list);

		undoAction.execute(context, actionContext);

		final TagAssociationMetadata removedMetadata = captureRemovedMetadata();
		assertEquals(scope, removedMetadata.getSubject());
		assertEquals(tag, removedMetadata.getTag());
	}

	@Override
	protected ModelAction getNewInstance() {
		return new ScopeAddTagAssociationAction(scope.getId(), tag.getId());
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return ScopeAddTagAssociationAction.class;
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return ScopeAddTagAssociationActionEntity.class;
	}

}
