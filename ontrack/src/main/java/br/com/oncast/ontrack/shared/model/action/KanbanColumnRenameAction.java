package br.com.oncast.ontrack.shared.model.action;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.kanban.KanbanColumnRenameActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.kanban.Kanban;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@ConvertTo(KanbanColumnRenameActionEntity.class)
public class KanbanColumnRenameAction implements KanbanAction {

	private static final long serialVersionUID = 1L;

	@Element
	@ConversionAlias("releaseId")
	private UUID releaseId;

	@Attribute
	@ConversionAlias("columnDescription")
	private String columnDescription;

	@Attribute
	@ConversionAlias("newDescription")
	private String newDescription;

	// IMPORTANT A package-visible default constructor is necessary for serialization. Do not remove this.
	protected KanbanColumnRenameAction() {}

	public KanbanColumnRenameAction(final UUID releaseId, final String columnDescription, final String newDescription) {
		this.releaseId = releaseId;
		this.columnDescription = columnDescription;
		this.newDescription = newDescription;
	}

	@Override
	public ModelAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		final String trimmedDescription = newDescription.trim();
		if (trimmedDescription.isEmpty()) throw new UnableToCompleteActionException("The new column description can't be empty");

		final Release release = ReleaseActionHelper.findRelease(releaseId, context);
		final Kanban kanban = context.getKanban(release);

		try {
			kanban.renameColumn(columnDescription, newDescription);
		}
		catch (final RuntimeException e) {
			throw new UnableToCompleteActionException(e);
		}
		for (final Scope scope : release.getScopeList()) {
			if (scope.getProgress().getDescription().equals(columnDescription)) new ScopeDeclareProgressAction(scope.getId(), newDescription).execute(context);
		}
		kanban.setLocked(true);
		return new KanbanColumnRenameAction(releaseId, newDescription, columnDescription);
	}

	@Override
	public UUID getReferenceId() {
		return releaseId;
	}

}
