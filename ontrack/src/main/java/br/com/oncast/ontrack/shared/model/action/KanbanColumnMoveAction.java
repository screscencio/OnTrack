package br.com.oncast.ontrack.shared.model.action;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.kanban.KanbanColumnMoveActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
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

	// IMPORTANT The default constructor is used by GWT and by Mind map converter to construct new scopes. Do not remove this.
	protected KanbanColumnMoveAction() {}

	public KanbanColumnMoveAction(final UUID releaseId, final String kanbanColumnDescription, final int desiredIndex) {
		this.releaseId = releaseId;
		this.columnDescription = kanbanColumnDescription;
		this.desiredIndex = desiredIndex;

	}

	@Override
	public ModelAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		final Kanban kanban = context.getKanban(ReleaseActionHelper.findRelease(releaseId, context));
		try {
			final int previousIndex = kanban.indexOf(columnDescription);
			kanban.moveColumn(columnDescription, desiredIndex);
			kanban.setLocked(true);
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
