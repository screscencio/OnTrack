package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeBindHumanIdActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.metadata.HumanIdMetadata;
import br.com.oncast.ontrack.shared.model.metadata.MetadataFactory;
import br.com.oncast.ontrack.shared.model.metadata.MetadataType;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@ConvertTo(ScopeBindHumanIdActionEntity.class)
public class ScopeBindHumanIdAction implements ScopeAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID scopeId;

	@Element
	private UUID metadataId;

	@Attribute
	private String humanId;

	protected ScopeBindHumanIdAction() {}

	public ScopeBindHumanIdAction(final UUID scopeId, final String humanId) {
		this(scopeId, humanId, new UUID());
	}

	ScopeBindHumanIdAction(final HumanIdMetadata metadata) {
		this(metadata.getSubject().getId(), metadata.getHumanId(), metadata.getId());
	}

	private ScopeBindHumanIdAction(final UUID scopeId, final String humanId, final UUID metadataId) {
		this.scopeId = scopeId;
		this.humanId = humanId;
		this.metadataId = metadataId;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Scope scope = ActionHelper.findScope(scopeId, context, this);
		final List<HumanIdMetadata> metadataList = context.getMetadataList(scope, MetadataType.HUMAN_ID);
		if (!metadataList.isEmpty()) throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.CREATE_EXISTENT);

		final HumanIdMetadata metadata = MetadataFactory.createHumanIdMetadata(metadataId, scope, humanId);
		context.addMetadata(metadata);
		return new ScopeUnbindHumanIdAction(metadata);
	}

	@Override
	public UUID getReferenceId() {
		return scopeId;
	}

	@Override
	public boolean changesEffortInference() {
		return false;
	}

	@Override
	public boolean changesProgressInference() {
		return false;
	}

	@Override
	public boolean changesValueInference() {
		return false;
	}

}
