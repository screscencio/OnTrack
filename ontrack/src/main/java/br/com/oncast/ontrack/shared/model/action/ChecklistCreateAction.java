package br.com.oncast.ontrack.shared.model.action;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.checklist.ChecklistCreateActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ChecklistCreateActionEntity.class)
public class ChecklistCreateAction implements ChecklistAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID subjectId;

	@Element
	private UUID checklistId;

	@Attribute
	private String title;

	protected ChecklistCreateAction() {}

	public ChecklistCreateAction(final UUID subjectId, final String title) {
		this.subjectId = subjectId;
		this.title = title;
		checklistId = new UUID();
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		context.addChecklist(new Checklist(checklistId, title), subjectId);
		return new ChecklistRemoveAction(checklistId, subjectId);
	}

	@Override
	public UUID getReferenceId() {
		return subjectId;
	}

}
