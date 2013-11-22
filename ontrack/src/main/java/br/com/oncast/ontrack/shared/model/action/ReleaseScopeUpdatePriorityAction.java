package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.release.ReleaseScopeUpdatePriorityActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.exceptions.ActionExecutionErrorMessageCode;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.UUIDUtils;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@ConvertTo(ReleaseScopeUpdatePriorityActionEntity.class)
public class ReleaseScopeUpdatePriorityAction implements ReleaseAction {

	private static final long serialVersionUID = 1L;

	@ConversionAlias("releaseReferenceId")
	@Element(required = false)
	private UUID releaseReferenceId;

	@ConversionAlias("scopeReferenceId")
	@Element
	private UUID scopeReferenceId;

	@ConversionAlias("priority")
	@Attribute
	private int priority;

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

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ReleaseScopeUpdatePriorityAction() {}

	public ReleaseScopeUpdatePriorityAction(final UUID releaseReferenceId, final UUID scopeReferenceId, final int priority) {
		this.uniqueId = new UUID();
		this.releaseReferenceId = releaseReferenceId;
		this.scopeReferenceId = scopeReferenceId;
		this.priority = priority;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Scope scope = ActionHelper.findScope(scopeReferenceId, context, this);
		// IMPORTANT The release id should be used to recover the release, but as it was not being saved there is the need to maintain backwards compatibility.
		final Release release = releaseReferenceId != null ? ActionHelper.findRelease(releaseReferenceId, context, this) : scope.getRelease();

		if (!release.containsScope(scope)) throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.RELEASE_NOT_CONTAINS_SCOPE);
		if (priority < 0) throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.ALREADY_THE_MOST_PRIORITARY);
		if (priority >= release.getScopeList().size()) throw new UnableToCompleteActionException(this, ActionExecutionErrorMessageCode.ALREADY_THE_LEAST_PRIORITARY);

		final int oldPriority = release.getScopeIndex(scope);
		release.removeScope(scope);
		release.addScope(scope, priority);

		return new ReleaseScopeUpdatePriorityAction(releaseReferenceId, scopeReferenceId, oldPriority);
	}

	@Override
	public UUID getReferenceId() {
		return releaseReferenceId;
	}

	public UUID getScopeReferenceId() {
		return scopeReferenceId;
	}
}
