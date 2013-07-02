package br.com.oncast.ontrack.shared.model.action;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeDeclareValueActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ScopeDeclareValueActionEntity.class)
public class ScopeDeclareValueAction implements ScopeAction {

	private static final long serialVersionUID = 1L;

	@ConversionAlias("referenceId")
	@Element
	private UUID referenceId;

	@ConversionAlias("hasDeclaredValue")
	@Attribute
	private boolean hasDeclaredValue;

	@ConversionAlias("newDeclaredValue")
	@Attribute
	private float newDeclaredValue;

	public ScopeDeclareValueAction(final UUID referenceId, final boolean hasDeclaredValue, final float newDeclaredValue) {
		this.referenceId = referenceId;
		this.hasDeclaredValue = hasDeclaredValue;
		this.newDeclaredValue = newDeclaredValue;
	}

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected ScopeDeclareValueAction() {}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Scope selectedScope = ActionHelper.findScope(referenceId, context, this);

		final boolean oldHasDeclared = selectedScope.getValue().hasDeclared();
		final float oldDeclaredValue = selectedScope.getValue().getDeclared();

		if (hasDeclaredValue) selectedScope.getValue().setDeclared(newDeclaredValue);
		else selectedScope.getValue().resetDeclared();

		return new ScopeDeclareValueAction(referenceId, oldHasDeclared, oldDeclaredValue);
	}

	@Override
	public UUID getReferenceId() {
		return referenceId;
	}

	@Override
	public boolean changesEffortInference() {
		return false;
	}

	@Override
	public boolean changesValueInference() {
		return true;
	}

	@Override
	public boolean changesProgressInference() {
		return false;
	}
}