package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.kanban;

import javax.persistence.Column;
import javax.persistence.Entity;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.KanbanColumnMoveAction;

@Entity(name = "KanbanColumnMove")
@ConvertTo(KanbanColumnMoveAction.class)
public class KanbanColumnMoveActionEntity extends ModelActionEntity {

	@ConversionAlias("releaseId")
	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = "referenceId")
	private String releaseId;

	@ConversionAlias("columnDescription")
	@Column(name = "secondaryReferenceId")
	private String columnDescription;

	@ConversionAlias("desiredIndex")
	@Column(name = "pos")
	private int desiredIndex;

	public String getReleaseId() {
		return releaseId;
	}

	public void setReleaseId(final String releaseId) {
		this.releaseId = releaseId;
	}

	public String getColumnDescription() {
		return columnDescription;
	}

	public void setColumnDescription(final String columnDescription) {
		this.columnDescription = columnDescription;
	}

	public int getDesiredIndex() {
		return desiredIndex;
	}

	public void setDesiredIndex(final int desiredIndex) {
		this.desiredIndex = desiredIndex;
	}

}
