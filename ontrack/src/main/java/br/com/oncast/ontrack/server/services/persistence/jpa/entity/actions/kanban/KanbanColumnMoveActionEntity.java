package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.kanban;

import br.com.oncast.ontrack.server.services.persistence.jpa.ActionTableColumns;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.KanbanColumnMoveAction;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;

@Entity(name = "KanbanColumnMove")
@ConvertTo(KanbanColumnMoveAction.class)
public class KanbanColumnMoveActionEntity extends ModelActionEntity {

	@ConversionAlias("releaseId")
	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = ActionTableColumns.STRING_1)
	private String releaseId;

	@ConversionAlias("columnDescription")
	@Column(name = ActionTableColumns.STRING_2)
	private String columnDescription;

	@ConversionAlias("desiredIndex")
	@Column(name = ActionTableColumns.INT_1)
	private int desiredIndex;

	@ConversionAlias("subActions")
	@OneToMany(cascade = CascadeType.ALL)
	@Column(name = ActionTableColumns.ACTION_LIST)
	@JoinTable(name = "KanbanColumnMove_subActionList")
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

	public List<ModelActionEntity> getSubActionList() {
		return subActionList;
	}

	public void setSubActionList(final List<ModelActionEntity> subActionList) {
		this.subActionList = subActionList;
	}

}
