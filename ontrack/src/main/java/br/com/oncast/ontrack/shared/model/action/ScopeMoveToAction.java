package br.com.oncast.ontrack.shared.model.action;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeMoveToActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ScopeMoveToActionEntity.class)
public class ScopeMoveToAction implements ScopeMoveAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID movingScopeId;

	@Element
	private UUID desiredParentId;

	@Attribute
	private int desiredIndex;

	protected ScopeMoveToAction() {}

	public ScopeMoveToAction(final UUID movingScopeId, final UUID futureParentId, final int futureIndex) {
		this.movingScopeId = movingScopeId;
		this.desiredParentId = futureParentId;
		this.desiredIndex = futureIndex;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Scope movingScope = ActionHelper.findScope(movingScopeId, context);
		final Scope parent = ActionHelper.findScope(desiredParentId, context);

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
