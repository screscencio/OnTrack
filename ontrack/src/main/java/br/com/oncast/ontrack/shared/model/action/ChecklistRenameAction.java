package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.checklist.ChecklistRenameActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@ConvertTo(ChecklistRenameActionEntity.class)
public class ChecklistRenameAction implements ChecklistAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID checklistId;

	@Attribute
	private String newTitle;

	@Element
	private UUID subjectId;

	public ChecklistRenameAction() {}

	public ChecklistRenameAction(final UUID subjectId, final UUID checklistId, final String newTitle) {
		this.subjectId = subjectId;
		this.checklistId = checklistId;
		this.newTitle = newTitle;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Checklist checklist = ActionHelper.findChecklist(subjectId, checklistId, context, this);
		final String oldTitle = checklist.getTitle();
		checklist.setTitle(newTitle);
		return new ChecklistRenameAction(subjectId, checklistId, oldTitle);
	}

	@Override
	public UUID getReferenceId() {
		return checklistId;
	}

	public UUID getChecklistId() {
		return checklistId;
	}

	public void setChecklistId(final UUID checklistId) {
		this.checklistId = checklistId;
	}

	public String getNewTitle() {
		return newTitle;
	}

	public void setNewTitle(final String newTitle) {
		this.newTitle = newTitle;
	}

	public UUID getSubjectId() {
		return subjectId;
	}

	public void setSubjectId(final UUID subjectId) {
		this.subjectId = subjectId;
	}

}
