package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.kanban;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.KanbanColumnCreateAction;

@Entity(name = "KanbanCreateColumn")
@ConvertTo(KanbanColumnCreateAction.class)
public class KanbanColumnCreateActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	@ConversionAlias("referenceId")
	@Column(name = "referenceId")
	private String referenceId;

	@ConversionAlias("columnDescription")
	@Column(name = "description", length = 400)
	private String columnDescription;

	@ConversionAlias("shouldFixKanban")
	@Column(name = "boleano")
	private boolean shouldFixKanban;

	@ConversionAlias("subActions")
	@OneToMany(cascade = CascadeType.ALL)
	@Column(name = "modelActionEntity_subActionList")
	@JoinTable(name = "KanbanColumnCreate_subActionList")
	private List<ModelActionEntity> subActions;

	@ConversionAlias("columnIndex")
	@Column(name = "pos")
	private int columnIndex;

	public int getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(final int columnIndex) {
		this.columnIndex = columnIndex;
	}

	public List<ModelActionEntity> getSubActions() {
		return subActions;
	}

	public void setSubActions(final List<ModelActionEntity> subActions) {
		this.subActions = subActions;
	}

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

	public boolean isShouldFixKanban() {
		return shouldFixKanban;
	}

	public void setShouldFixKanban(final boolean shouldFixKanban) {
		this.shouldFixKanban = shouldFixKanban;
	}
}
