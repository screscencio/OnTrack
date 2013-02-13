package br.com.oncast.ontrack.shared.model.action.scope;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeUnbindHumanIdActionEntity;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.ScopeUnbindHumanIdAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.metadata.HumanIdMetadata;
import br.com.oncast.ontrack.shared.model.metadata.Metadata;
import br.com.oncast.ontrack.shared.model.metadata.MetadataFactory;
import br.com.oncast.ontrack.shared.model.metadata.exceptions.MetadataNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;

public class ScopeUnbindHumanIdActionTest extends ModelActionTest {

	private HumanIdMetadata metadata;
	private Scope scope;

	@Before
	public void setup() throws Exception {
		scope = ScopeTestUtils.createScope();
		metadata = MetadataFactory.createHumanIdMetadata(new UUID(), scope, "assa");

		when(context.findScope(scope.getId())).thenReturn(scope);
		when(context.findMetadata(scope, metadata.getMetadataType(), metadata.getId())).thenReturn(metadata);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToRemoveAnInexistentMetadata() throws Exception {
		when(context.findMetadata(scope, metadata.getMetadataType(), metadata.getId())).thenThrow(new MetadataNotFoundException(""));
		executeAction();
	}

	@Test
	public void shouldRemoveTheGivenMetadata() throws Exception {
		executeAction();
		final Metadata removedMetadata = captureRemovedMetadata();
		assertEquals(metadata, removedMetadata);
	}

	@Test
	public void undoShouldAddTheRemovedMetadata() throws Exception {
		final ModelAction undoAction = executeAction();

		undoAction.execute(context, actionContext);
		final Metadata addedMetadata = captureAddedMetadata();

		assertEquals(metadata, addedMetadata);
	}

	@Override
	protected ModelAction getNewInstance() {
		return new ScopeUnbindHumanIdAction(metadata);
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return ScopeUnbindHumanIdAction.class;
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return ScopeUnbindHumanIdActionEntity.class;
	}

}
