package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.kanban;

import br.com.oncast.ontrack.server.services.persistence.jpa.ActionTableColumns;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.KanbanLockAction;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;

@Entity(name = "KanbanLock")
@ConvertTo(KanbanLockAction.class)
public class KanbanLockActionEntity extends ModelActionEntity {

	@ConvertUsing(StringToUuidConverter.class)
	@Column(name = ActionTableColumns.STRING_1)
	private String releaseId;

	@Column(name = ActionTableColumns.STRING_LIST_1)
	@ElementCollection
	private List<String> columnDescriptions;

	@Column(name = ActionTableColumns.STRING_LIST_2)
	@ElementCollection
	private List<String> columnIds;

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

	public List<String> getColumnDescriptions() {
		return columnDescriptions;
	}

	public void setColumnDescriptions(final List<String> columnDescriptions) {
		this.columnDescriptions = columnDescriptions;
	}

	public List<String> getColumnIds() {
		return columnIds;
	}

	public void setColumnIds(final List<String> columnIds) {
		this.columnIds = columnIds;
	}

}
