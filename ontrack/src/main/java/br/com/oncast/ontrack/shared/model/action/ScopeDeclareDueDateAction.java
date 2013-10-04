package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.scope.ScopeDeclareDueDateActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import java.util.Date;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@ConvertTo(ScopeDeclareDueDateActionEntity.class)
public class ScopeDeclareDueDateAction implements ScopeAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID scopeId;

	@Attribute(required = false)
	private Date dueDate;

	public ScopeDeclareDueDateAction() {}

	public ScopeDeclareDueDateAction(final UUID scopeId, final Date dueDate) {
		this.scopeId = scopeId;
		this.dueDate = dueDate;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Scope scope = ActionHelper.findScope(scopeId, context, this);
		final Date previousDueDate = scope.getDueDate();
		scope.setDueDate(dueDate);
		return new ScopeDeclareDueDateAction(scopeId, previousDueDate);
	}

	@Override
	public UUID getReferenceId() {
		return scopeId;
	}

	public void setScopeId(final UUID scopeId) {
		this.scopeId = scopeId;
	}

	public void setDueDate(final Date dueDate) {
		this.dueDate = dueDate;
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

	public UUID getScopeId() {
		return scopeId;
	}

	public Date getDueDate() {
		return dueDate;
	}

}
