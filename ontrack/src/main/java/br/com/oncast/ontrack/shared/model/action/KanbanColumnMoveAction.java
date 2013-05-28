package br.com.oncast.ontrack.shared.model.action;

import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.kanban.KanbanColumnMoveActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.action.helper.ActionHelper;
import br.com.oncast.ontrack.shared.model.kanban.Kanban;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(KanbanColumnMoveActionEntity.class)
public class KanbanColumnMoveAction implements KanbanAction {

	private static final long serialVersionUID = 1L;

	@ConversionAlias("releaseId")
	@Element
	private UUID releaseId;

	@ConversionAlias("columnDescription")
	@Attribute
	private String columnDescription;

	@ConversionAlias("desiredIndex")
	@Attribute
	private int desiredIndex;

	@ElementList(required = false)
	private List<ModelAction> subActions;

	// IMPORTANT The default constructor is used by GWT and by Mind map converter to construct new scopes. Do not remove this.
	protected KanbanColumnMoveAction() {}

	public KanbanColumnMoveAction(final UUID releaseId, final String kanbanColumnDescription, final int desiredIndex) {
		this.releaseId = releaseId;
		this.columnDescription = kanbanColumnDescription;
		this.desiredIndex = desiredIndex;
		this.subActions = new ArrayList<ModelAction>();
	}

	@Override
	public ModelAction execute(final ProjectContext context, final ActionContext actionContext) throws UnableToCompleteActionException {
		final Kanban kanban = context.getKanban(ActionHelper.findRelease(releaseId, context));
		try {
			if (kanban.getColumn(columnDescription) == null) return new KanbanColumnCreateAction(releaseId, columnDescription, true, desiredIndex).execute(
					context, actionContext);
			final int previousIndex = kanban.indexOf(columnDescription);
			kanban.moveColumn(columnDescription, desiredIndex);
			if (subActions.isEmpty() && !kanban.isLocked()) subActions.add(new KanbanLockAction(releaseId));
			ActionHelper.executeSubActions(subActions, context, actionContext);
			return new KanbanColumnMoveAction(releaseId, columnDescription, previousIndex);
		}
		catch (final RuntimeException e) {
			throw new UnableToCompleteActionException(e);
		}
	}

	@Override
	public UUID getReferenceId() {
		return releaseId;
	}

}
