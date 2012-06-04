package br.com.oncast.ontrack.shared.model.action;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.release.ReleaseScopeUpdatePriorityActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

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

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ReleaseScopeUpdatePriorityAction() {}

	public ReleaseScopeUpdatePriorityAction(final UUID releaseReferenceId, final UUID scopeReferenceId, final int priority) {
		this.releaseReferenceId = releaseReferenceId;
		this.scopeReferenceId = scopeReferenceId;
		this.priority = priority;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Scope scope = ActionHelper.findScope(scopeReferenceId, context);
		// IMPORTANT The release id should be used to recover the release, but as it was not being saved there is the need to maintain backwards compatibility.
		final Release release = releaseReferenceId != null ? ActionHelper.findRelease(releaseReferenceId, context) : scope.getRelease();

		if (!release.containsScope(scope)) throw new UnableToCompleteActionException(
				"The scope is not part of the referenced release.");
		if (priority < 0) throw new UnableToCompleteActionException(
				"It's already the most prioritary scope.");
		if (priority >= release.getScopeList().size()) throw new UnableToCompleteActionException(
				"It's already the least prioritary scope.");

		final int oldPriority = release.getScopeIndex(scope);
		release.removeScope(scope);
		release.addScope(scope, priority);

		return new ReleaseScopeUpdatePriorityAction(releaseReferenceId, scopeReferenceId, oldPriority);
	}

	@Override
	public UUID getReferenceId() {
		return releaseReferenceId;
	}
}
