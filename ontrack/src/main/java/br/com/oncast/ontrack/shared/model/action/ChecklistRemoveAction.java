package br.com.oncast.ontrack.shared.model.action;

import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ChecklistRemoveAction implements ChecklistAction {

	private static final long serialVersionUID = 1L;

	@Element
	private final UUID checklistId;

	@Element
	private final UUID subjectId;

	public ChecklistRemoveAction(final UUID checklistId, final UUID subjectId) {
		this.checklistId = checklistId;
		this.subjectId = subjectId;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		context.removeChecklist(checklistId, subjectId);
		return null;
	}

	@Override
	public UUID getReferenceId() {
		// FIXME Auto-generated catch block
		return null;
	}

}
