package br.com.oncast.ontrack.shared.model.action.scope;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

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
import br.com.oncast.ontrack.shared.model.tags.UserAssociationTag;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.mocks.models.UserRepresentationTestUtils;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;

public class ScopeRemoveAssociatedUserActionTest extends ModelActionTest {

	private Scope scope;
	private UUID scopeId;

	private UserRepresentation user;
	private UUID userId;

	private UserAssociationTag tag;

	@Before
	public void setup() throws Exception {
		scope = ScopeTestUtils.createScope();
		scopeId = scope.getId();

		user = UserRepresentationTestUtils.createUser();
		userId = user.getId();

		tag = TagFactory.createUserTag(new UUID(), scope, user);

		when(context.findScope(scopeId)).thenReturn(scope);
		when(context.findUser(userId)).thenReturn(user);
		final List<Tag> list = new ArrayList<Tag>();
		list.add(tag);
		when(context.getTags(scope, UserAssociationTag.getType())).thenReturn(list);
	}

	@Test
	public void doesNotChangesAnyInference() throws Exception {
		final ScopeAction action = new ScopeRemoveAssociatedUserAction(scopeId, userId);
		assertFalse(action.changesEffortInference());
		assertFalse(action.changesProgressInference());
		assertFalse(action.changesValueInference());
	}

	@Test
	public void shouldRemoveTheGivenAssociationOfTheGivenScope() throws Exception {
		executeAction();

		final Tag value = captureRemovedTag();
		assertTrue(value instanceof UserAssociationTag);
		final UserAssociationTag tag = (UserAssociationTag) value;

		assertEquals(scope, tag.getSubject());
		assertEquals(userId, tag.getUser().getId());
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
		return new ScopeRemoveAssociatedUserAction(scopeId, userId);
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
