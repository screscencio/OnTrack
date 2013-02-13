package br.com.oncast.ontrack.shared.model.action;

import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

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

@ConvertTo(ScopeBindHumanIdActionEntity.class)
public class ScopeBindHumanIdAction implements ScopeAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID scopeId;

	@Element
	private UUID metadataId;

	@Attribute(required = false)
	private String humanId;

	protected ScopeBindHumanIdAction() {}

	public ScopeBindHumanIdAction(final UUID scopeId, final String humanId) {
		this.scopeId = scopeId;
		this.humanId = humanId;
		this.metadataId = new UUID();
	}

	ScopeBindHumanIdAction(final HumanIdMetadata metadata) {
		this.scopeId = metadata.getSubject().getId();
		this.humanId = metadata.getHumanId();
		this.metadataId = metadata.getId();
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Scope scope = ActionHelper.findScope(scopeId, context);
		final List<HumanIdMetadata> metadataList = context.getMetadataList(scope, MetadataType.HUMAN_ID);
		if (!metadataList.isEmpty()) throw new UnableToCompleteActionException(ActionExecutionErrorMessageCode.CREATE_EXISTENT);

		final HumanIdMetadata metadata = MetadataFactory.createHumanIdMetadata(metadataId, scope, humanId);
		context.addMetadata(metadata);
		return new ScopeUnbindHumanIdAction(metadata);
	}

	// @Override
	// public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
	// final Scope scope = ActionHelper.findScope(scopeId, context);
	// final List<Metadata> metadataList = context.getMetadataList(scope, MetadataType.HUMAN_ID);
	// if (!metadataList.isEmpty()) throw new UnableToCompleteActionException(ActionExecutionErrorMessageCode.CREATE_EXISTENT);
	//
	// context.addMetadata(MetadataFactory.createHumanIdMetadata(metadataId, scope, humanId));
	// return new ScopeBindHumanIdAction(scopeId, null);
	// }

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
