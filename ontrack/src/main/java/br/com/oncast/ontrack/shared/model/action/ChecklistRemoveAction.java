package br.com.oncast.ontrack.shared.model.action;

import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.checklist.ChecklistRemoveActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.checklist.exception.ChecklistNotFoundException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ChecklistRemoveActionEntity.class)
public class ChecklistRemoveAction implements ChecklistAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID checklistId;

	@Element
	private UUID subjectId;

	protected ChecklistRemoveAction() {}

	public ChecklistRemoveAction(final UUID subjectId, final UUID checklistId) {
		this.checklistId = checklistId;
		this.subjectId = subjectId;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		try {
			final Checklist checklist = context.removeChecklist(subjectId, checklistId);
			return new ChecklistCreateAction(subjectId, checklist);
		}
		catch (final ChecklistNotFoundException e) {
			throw new UnableToCompleteActionException("Could not remove the requested checklist", e);
		}
	}

	@Override
	public UUID getReferenceId() {
		return subjectId;
	}

}
