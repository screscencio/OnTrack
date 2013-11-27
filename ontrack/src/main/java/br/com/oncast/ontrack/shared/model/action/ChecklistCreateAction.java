package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.checklist.ChecklistCreateActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.checklist.ChecklistItem;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

@ConvertTo(ChecklistCreateActionEntity.class)
public class ChecklistCreateAction implements ChecklistAction {

	private static final long serialVersionUID = 1L;

	@Element
	private UUID subjectId;

	@Element
	private UUID checklistId;

	@Attribute
	private String title;

	@ElementList
	private List<ModelAction> subActionList;

	protected ChecklistCreateAction() {}

	protected ChecklistCreateAction(final UUID subjectId, final Checklist checklist) {
		this.subjectId = subjectId;
		this.checklistId = checklist.getId();
		this.title = checklist.getTitle();
		this.subActionList = new ArrayList<ModelAction>();
		for (final ChecklistItem item : checklist.getItems()) {
			this.subActionList.add(new ChecklistAddItemAction(subjectId, checklistId, item));
		}
	}

	public ChecklistCreateAction(final UUID subjectId, final String title) {
		this.subjectId = subjectId;
		this.title = title;
		checklistId = new UUID();
		this.subActionList = new ArrayList<ModelAction>();
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		context.addChecklist(subjectId, new Checklist(checklistId, title));
		for (final ModelAction subAction : subActionList) {
			subAction.execute(context, actionContext);
		}
		return new ChecklistRemoveAction(subjectId, checklistId);
	}

	@Override
	public UUID getReferenceId() {
		return subjectId;
	}

	public UUID getChecklistId() {
		return checklistId;
	}

}
