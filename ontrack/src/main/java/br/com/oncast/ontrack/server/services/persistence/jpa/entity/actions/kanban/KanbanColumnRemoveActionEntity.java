package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.kanban;

import javax.persistence.Column;
import javax.persistence.Entity;

import br.com.oncast.ontrack.server.services.persistence.jpa.ActionTableColumns;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.KanbanColumnRemoveAction;

@Entity(name = "KanbanColumnRemove")
@ConvertTo(KanbanColumnRemoveAction.class)
public class KanbanColumnRemoveActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	@ConversionAlias("referenceId")
	@Column(name = ActionTableColumns.STRING_1)
	private String referenceId;

	@ConversionAlias("columnDescription")
	@Column(name = ActionTableColumns.STRING_2, length = ActionTableColumns.STRING_2_LENGTH)
	private String columnDescription;

	@ConversionAlias("shouldUnfixKanban")
	@Column(name = ActionTableColumns.BOOLEAN_1)
	private boolean shouldUnfixKanban;

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(final String referenceId) {
		this.referenceId = referenceId;
	}

	public String getColumnDescription() {
		return columnDescription;
	}

	public void setColumnDescription(final String columnDescription) {
		this.columnDescription = columnDescription;
	}

	public boolean isShouldUnfixKanban() {
		return shouldUnfixKanban;
	}

	public void setShouldUnfixKanban(final boolean shouldUnfixKanban) {
		this.shouldUnfixKanban = shouldUnfixKanban;
	}
}
