package br.com.oncast.ontrack.shared.model.action;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.checklist.ChecklistEditItemDescriptionActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.checklist.ChecklistItem;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(ChecklistEditItemDescriptionActionEntity.class)
public class ChecklistEditItemDescriptionAction implements ChecklistAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID itemId;

	@Element
	private UUID checklistId;

	@Element
	private UUID subjectId;

	@Attribute
	private String newDescription;

	protected ChecklistEditItemDescriptionAction() {}

	public ChecklistEditItemDescriptionAction(final UUID subjectId, final UUID checklistId, final UUID itemId, final String newDescription) {
		this.subjectId = subjectId;
		this.checklistId = checklistId;
		this.itemId = itemId;
		this.newDescription = newDescription;
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Checklist checklist = ActionHelper.findChecklist(subjectId, checklistId, context);
		final ChecklistItem item = checklist.getItem(itemId);
		if (item == null) throw new UnableToCompleteActionException("It's not possible to edit the description on an inexitent Checklist Item.");
		final String oldDescription = item.getDescription();
		item.setDescription(newDescription);
		return new ChecklistEditItemDescriptionAction(subjectId, checklistId, itemId, oldDescription);
	}

	@Override
	public UUID getReferenceId() {
		return itemId;
	}

}
