package br.com.oncast.ontrack.shared.model.action.scope;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeAddAssociatedUserActionEntity;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.ScopeAction;
import br.com.oncast.ontrack.shared.model.action.ScopeAddAssociatedUserAction;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.tags.Tag;
import br.com.oncast.ontrack.shared.model.tags.UserTag;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;
import br.com.oncast.ontrack.utils.model.UserTestUtils;

public class ScopeAddAssociatedUserActionTest extends ModelActionTest {

	private UUID scopeId;
	private UUID userId;
	private Scope scope;
	private User user;

	@Before
	public void setup() throws Exception {
		scope = ScopeTestUtils.createScope();
		scopeId = scope.getId();
		user = UserTestUtils.getAdmin();
		userId = user.getId();

		when(context.findScope(scopeId)).thenReturn(scope);
	}

	@Test
	public void doesNotChangesAnyInference() throws Exception {
		final ScopeAction action = new ScopeAddAssociatedUserAction(scopeId, userId);
		assertFalse(action.changesEffortInference());
		assertFalse(action.changesProgressInference());
		assertFalse(action.changesValueInference());
	}

	@Test
	public void shouldAddTheGivenUserAsAssociatedUserToTheGivenScope() throws Exception {
		executeAction();

		final Tag value = captureAddedTag();
		assertTrue(value instanceof UserTag);
		final UserTag tag = (UserTag) value;

		assertEquals(scope, tag.getSubject());
		assertEquals(user, tag.getUser());
	}

	@Test
	public void undoShouldRemoveTheAssociation() throws Exception {
		final ModelAction undoAction = executeAction();
		final UserTag tag = captureAddedTag();

		when(context.findTag(scope, UserTag.getType(), tag.getId())).thenReturn(tag);
		undoAction.execute(context, actionContext);
		verify(context).removeTag(tag);
	}

	@Override
	protected ModelAction getNewInstance() {
		return new ScopeAddAssociatedUserAction(scopeId, userId);
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return ScopeAddAssociatedUserAction.class;
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return ScopeAddAssociatedUserActionEntity.class;
	}

}
