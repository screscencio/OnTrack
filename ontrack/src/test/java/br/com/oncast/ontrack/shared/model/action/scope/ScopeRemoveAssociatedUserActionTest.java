package br.com.oncast.ontrack.shared.model.action.scope;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeRemoveAssociatedUserActionEntity;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.ScopeAction;
import br.com.oncast.ontrack.shared.model.action.ScopeRemoveAssociatedUserAction;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.tags.Tag;
import br.com.oncast.ontrack.shared.model.tags.TagFactory;
import br.com.oncast.ontrack.shared.model.tags.UserTag;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;
import br.com.oncast.ontrack.utils.model.UserTestUtils;

public class ScopeRemoveAssociatedUserActionTest extends ModelActionTest {

	private UUID scopeId;
	private UUID tagId;
	private Scope scope;
	private UserTag tag;

	@Before
	public void setup() throws Exception {
		scope = ScopeTestUtils.createScope();
		scopeId = scope.getId();
		tagId = new UUID();
		tag = TagFactory.createUserTag(tagId, scope, UserTestUtils.createUser());

		when(context.findScope(scopeId)).thenReturn(scope);
		when(context.findTag(scope, UserTag.getType(), tagId)).thenReturn(tag);
	}

	@Test
	public void doesNotChangesAnyInference() throws Exception {
		final ScopeAction action = new ScopeRemoveAssociatedUserAction(scopeId, tagId);
		assertFalse(action.changesEffortInference());
		assertFalse(action.changesProgressInference());
		assertFalse(action.changesValueInference());
	}

	@Test
	public void shouldRemoveTheGivenAssociationOfTheGivenScope() throws Exception {
		executeAction();

		final Tag value = captureRemovedTag();
		assertTrue(value instanceof UserTag);
		final UserTag tag = (UserTag) value;

		assertEquals(scope, tag.getSubject());
		assertEquals(tagId, tag.getId());
	}

	@Test
	public void undoShouldAddTheAssociation() throws Exception {
		final ModelAction undoAction = executeAction();
		final Tag tag = captureRemovedTag();

		undoAction.execute(context, actionContext);
		verify(context).addTag(tag);
	}

	@Override
	protected ModelAction getNewInstance() {
		return new ScopeRemoveAssociatedUserAction(scopeId, tagId);
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return ScopeRemoveAssociatedUserAction.class;
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return ScopeRemoveAssociatedUserActionEntity.class;
	}

}
