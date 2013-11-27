package br.com.oncast.ontrack.shared.model.action.scope;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeAddAssociatedUserActionEntity;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.ScopeAction;
import br.com.oncast.ontrack.shared.model.action.ScopeAddAssociatedUserAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.metadata.Metadata;
import br.com.oncast.ontrack.shared.model.metadata.MetadataFactory;
import br.com.oncast.ontrack.shared.model.metadata.UserAssociationMetadata;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.mocks.models.UserRepresentationTestUtils;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ScopeAddAssociatedUserActionTest extends ModelActionTest {

	private UUID scopeId;
	private UUID userId;
	private Scope scope;
	private UserRepresentation user;

	@Before
	public void setup() throws Exception {
		scope = ScopeTestUtils.createScope();
		scopeId = scope.getId();
		user = UserRepresentationTestUtils.getAdmin();
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

		final Metadata value = captureAddedMetadata();
		assertTrue(value instanceof UserAssociationMetadata);
		final UserAssociationMetadata metadata = (UserAssociationMetadata) value;

		assertEquals(scope, metadata.getSubject());
		assertEquals(user, metadata.getUser());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToAssociateTheSameScopeWithSameUserTwice() throws Exception {
		final List<Metadata> list = new ArrayList<Metadata>();
		list.add(MetadataFactory.createUserMetadata(new UUID(), scope, user));
		when(context.getMetadataList(scope, UserAssociationMetadata.getType())).thenReturn(list);

		executeAction();
	}

	@Test
	public void undoShouldRemoveTheAssociation() throws Exception {
		final ModelAction undoAction = executeAction();
		final UserAssociationMetadata metadata = captureAddedMetadata();

		final List<Metadata> list = new ArrayList<Metadata>();
		list.add(metadata);
		when(context.getMetadataList(scope, UserAssociationMetadata.getType())).thenReturn(list);
		undoAction.execute(context, actionContext);
		verify(context).removeMetadata(metadata);
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
