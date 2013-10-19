package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeMoveToActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@ConvertTo(ScopeMoveToActionEntity.class)
public class ScopeMoveToAction implements ScopeMoveAction, HasDestination {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID movingScopeId;

	@Element
	private UUID desiredParentId;

	@Attribute
	private int desiredIndex;

	protected ScopeMoveToAction() {}

	public ScopeMoveToAction(final UUID movingScopeId, final UUID desiredParentId, final int desiredIndex) {
		this(movingScopeId);
		setDestination(desiredParentId, desiredIndex);
	}

	public ScopeMoveToAction(final UUID movingScopeId) {
		this.movingScopeId = movingScopeId;
	}

	@Override
	public ScopeMoveToAction setDestination(final UUID desiredParentId, final int desiredIndex) {
		this.desiredParentId = desiredParentId;
		this.desiredIndex = desiredIndex;
		return this;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Scope movingScope = ActionHelper.findScope(movingScopeId, context, this);
		final Scope parent = ActionHelper.findScope(desiredParentId, context, this);

		final Scope previousParent = movingScope.getParent();
		int previousIndex = previousParent.getChildIndex(movingScope);
		previousParent.remove(movingScope);

		int index = desiredIndex;

		if (previousParent.equals(parent)) {
			index -= previousIndex < desiredIndex ? 1 : 0;
			previousIndex += previousIndex < desiredIndex ? 0 : 1;
		}
		parent.add(index, movingScope);

		return new ScopeMoveToAction(movingScopeId, previousParent.getId(), previousIndex);
	}

	@Override
	public UUID getReferenceId() {
		return movingScopeId;
	}

	@Override
	public UUID getSourceScopeId() {
		return movingScopeId;
	}

	@Override
	public boolean changesEffortInference() {
		return true;
	}

	@Override
	public boolean changesProgressInference() {
		return true;
	}

	@Override
	public boolean changesValueInference() {
		return true;
	}

}
