package br.com.oncast.ontrack.shared.model.action.scope;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeBindHumanIdActionEntity;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionTest;
import br.com.oncast.ontrack.shared.model.action.ScopeBindHumanIdAction;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.metadata.HumanIdMetadata;
import br.com.oncast.ontrack.shared.model.metadata.Metadata;
import br.com.oncast.ontrack.shared.model.metadata.MetadataFactory;
import br.com.oncast.ontrack.shared.model.metadata.MetadataType;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.when;

public class ScopeBindHumanIdActionTest extends ModelActionTest {

	private String humanId;

	private Scope scope;

	@Before
	public void setup() throws Exception {
		humanId = "23";
		scope = ScopeTestUtils.createScope();
		when(context.findScope(scope.getId())).thenReturn(scope);
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToSetHumanIdToAnInexistentScope() throws Exception {
		when(context.findScope(scope.getId())).thenThrow(new ScopeNotFoundException());
		executeAction();
	}

	@Test
	public void shouldSetHumanIdToGivenScope() throws Exception {
		executeAction();
		final HumanIdMetadata addedMetadata = captureAddedMetadata();
		assertEquals(scope, addedMetadata.getSubject());
		assertEquals(humanId, addedMetadata.getHumanId());
	}

	@Test(expected = UnableToCompleteActionException.class)
	public void shouldNotBeAbleToSetTheHumanIdWhenTheScopeAlreadyHasAnId() throws Exception {
		final List<Metadata> list = new ArrayList<Metadata>();
		list.add(MetadataFactory.createHumanIdMetadata(new UUID(), scope, "any"));
		when(context.getMetadataList(scope, MetadataType.HUMAN_ID)).thenReturn(list);
		executeAction();
	}

	@Test
	public void undoShouldRemoveTheSettedHumanId() throws Exception {
		final ModelAction undoAction = executeAction();

		final HumanIdMetadata addedMetadata = captureAddedMetadata();
		when(context.findMetadata(scope, MetadataType.HUMAN_ID, addedMetadata.getId())).thenReturn(addedMetadata);

		undoAction.execute(context, actionContext);

		final HumanIdMetadata removedMetadata = captureRemovedMetadata();
		assertEquals(scope, removedMetadata.getSubject());
		assertEquals(humanId, removedMetadata.getHumanId());
		assertEquals(addedMetadata.getId(), removedMetadata.getId());
	}

	@Override
	protected ModelAction getNewInstance() {
		return new ScopeBindHumanIdAction(scope.getId(), humanId);
	}

	@Override
	protected Class<? extends ModelAction> getActionType() {
		return ScopeBindHumanIdAction.class;
	}

	@Override
	protected Class<? extends ModelActionEntity> getEntityType() {
		return ScopeBindHumanIdActionEntity.class;
	}

}
