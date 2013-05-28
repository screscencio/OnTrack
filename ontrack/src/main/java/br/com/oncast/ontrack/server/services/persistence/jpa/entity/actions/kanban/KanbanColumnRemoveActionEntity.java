package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.kanban;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

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
	@Column(name = ActionTableColumns.STRING_2)
	private String columnDescription;

	@ConversionAlias("shouldUnfixKanban")
	@Column(name = ActionTableColumns.BOOLEAN_1)
	private boolean shouldUnfixKanban;

	@ConversionAlias("subActions")
	@OneToMany(cascade = CascadeType.ALL)
	@Column(name = ActionTableColumns.ACTION_LIST)
	@JoinTable(name = "KanbanColumnRemove_subActionList")
	private List<ModelActionEntity> subActionList;

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

	public List<ModelActionEntity> getSubActionList() {
		return subActionList;
	}

	public void setSubActionList(List<ModelActionEntity> subActionList) {
		this.subActionList = subActionList;
	}
}
