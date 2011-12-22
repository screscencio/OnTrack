package br.com.oncast.ontrack.shared.model.action;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeDeclareEffortActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ScopeDeclareEffortActionEntity.class)
public class ScopeDeclareEffortAction implements ScopeAction {

	private static final long serialVersionUID = 1L;

	@ConversionAlias("referenceId")
	@Element
	private UUID referenceId;

	@ConversionAlias("hasDeclaredEffort")
	@Attribute
	private boolean hasDeclaredEffort;

	@ConversionAlias("newDeclaredEffort")
	@Attribute
	private float newDeclaredEffort;

	public ScopeDeclareEffortAction(final UUID referenceId, final boolean hasDeclaredEffort, final float newDeclaredEffort) {
		this.referenceId = referenceId;
		this.hasDeclaredEffort = hasDeclaredEffort;
		this.newDeclaredEffort = newDeclaredEffort;
	}

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ScopeDeclareEffortAction() {}

	@Override
	public ModelAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Scope selectedScope = ScopeActionHelper.findScope(referenceId, context);

		final boolean oldHasDeclared = selectedScope.getEffort().hasDeclared();
		final float oldDeclaredEffort = selectedScope.getEffort().getDeclared();

		if (hasDeclaredEffort) selectedScope.getEffort().setDeclared(newDeclaredEffort);
		else selectedScope.getEffort().resetDeclared();

		return new ScopeDeclareEffortAction(referenceId, oldHasDeclared, oldDeclaredEffort);
	}

	@Override
	public UUID getReferenceId() {
		return referenceId;
	}

	@Override
	public boolean changesEffortInference() {
		return true;
	}

	@Override
	public boolean changesProgressInference() {
		return true;
	}
}
