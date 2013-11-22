package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeUnbindHumanIdActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.metadata.HasMetadata;
import br.com.oncast.ontrack.shared.model.metadata.HumanIdMetadata;
import br.com.oncast.ontrack.shared.model.metadata.MetadataType;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.UUIDUtils;

import org.simpleframework.xml.Element;

@ConvertTo(ScopeUnbindHumanIdActionEntity.class)
public class ScopeUnbindHumanIdAction implements ScopeAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID scopeId;

	@Element
	private UUID metadataId;

	@Element
	private UUID uniqueId;

	@Override
	public UUID getId() {
		return uniqueId;
	}

	@Override
	public int hashCode() {
		return UUIDUtils.hashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return UUIDUtils.equals(this, obj);
	}

	protected ScopeUnbindHumanIdAction() {}

	public ScopeUnbindHumanIdAction(final HumanIdMetadata metadata) {
		this.uniqueId = new UUID();
		this.metadataId = metadata.getId();
		this.scopeId = metadata.getSubject().getId();
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final HasMetadata subject = ActionHelper.findScope(scopeId, context, this);
		final HumanIdMetadata metadata = ActionHelper.findMetadata(subject, MetadataType.HUMAN_ID, metadataId, context, this);
		context.removeMetadata(metadata);
		return new ScopeBindHumanIdAction(metadata);
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
