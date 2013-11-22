package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.kanban;

import br.com.oncast.ontrack.server.services.persistence.jpa.ActionTableColumns;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.KanbanColumnCreateAction;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

@Entity(name = "KanbanColumnCreate")
@ConvertTo(KanbanColumnCreateAction.class)
public class KanbanColumnCreateActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	@ConversionAlias("referenceId")
	@Column(name = ActionTableColumns.STRING_1)
	private String referenceId;

	@ConversionAlias("columnDescription")
	@Column(name = ActionTableColumns.STRING_2)
	private String columnDescription;

	@ConvertUsing(StringToUuidConverter.class)
	@ConversionAlias("columnId")
	@Column(name = ActionTableColumns.STRING_3)
	private String columnId;

	@ConversionAlias("shouldFixKanban")
	@Column(name = ActionTableColumns.BOOLEAN_1)
	private boolean shouldFixKanban;

	@ConversionAlias("subActions")
	@OneToMany(cascade = CascadeType.ALL)
	@Column(name = ActionTableColumns.ACTION_LIST)
	@JoinTable(name = "KanbanColumnCreate_subActionList")
	private List<ModelActionEntity> subActionList;

	@Column(name = ActionTableColumns.UNIQUE_ID)
	@ConvertUsing(StringToUuidConverter.class)
	private String uniqueId;

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(final String uniqueId) {
		this.uniqueId = uniqueId;
	}

	@ConversionAlias("columnIndex")
	@Column(name = ActionTableColumns.INT_1)
	private int columnIndex;

	public int getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(final int columnIndex) {
		this.columnIndex = columnIndex;
	}

	public List<ModelActionEntity> getSubActionList() {
		return subActionList;
	}

	public void setSubActionList(final List<ModelActionEntity> subActionList) {
		this.subActionList = subActionList;
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

	public String getColumnId() {
		return columnId;
	}

	public void setColumnId(final String columnId) {
		this.columnId = columnId;
	}
}
