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
import br.com.oncast.ontrack.shared.model.action.KanbanColumnRenameAction;

@Entity(name = "KanbanColumnRename")
@ConvertTo(KanbanColumnRenameAction.class)
public class KanbanColumnRenameActionEntity extends ModelActionEntity {

	@ConversionAlias("releaseId")
	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = ActionTableColumns.STRING_1)
	private String releaseId;

	@ConversionAlias("newDescription")
	@Column(name = ActionTableColumns.STRING_2)
	private String newDescription;

	@ConversionAlias("columnDescription")
	@Column(name = ActionTableColumns.STRING_3)
	private String columnDescription;

	@ConversionAlias("subActions")
	@OneToMany(cascade = CascadeType.ALL)
	@Column(name = ActionTableColumns.ACTION_LIST)
	@JoinTable(name = "KanbanColumnRename_subActionList")
	private List<ModelActionEntity> subActionList;

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

	public String getNewDescription() {
		return newDescription;
	}

	public void setNewDescription(final String newDescription) {
		this.newDescription = newDescription;
	}

	public List<ModelActionEntity> getSubActionList() {
		return subActionList;
	}

	public void setSubActionList(List<ModelActionEntity> subActionList) {
		this.subActionList = subActionList;
	}
}
