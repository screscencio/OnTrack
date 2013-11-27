package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;
import br.com.oncast.ontrack.shared.model.action.UserAction;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@ConvertTo(UserAction.class)
public class UserActionEntity {

	@Id
	@GeneratedValue
	@ConversionAlias("id")
	private long id;

	@ConvertUsing(StringToUuidConverter.class)
	private String uniqueId;

	@ConvertUsing(StringToUuidConverter.class)
	private String projectId;

	@ConvertUsing(StringToUuidConverter.class)
	private String userId;

	@Temporal(TemporalType.TIMESTAMP)
	private Date executionTimestamp;

	@Temporal(TemporalType.TIMESTAMP)
	private Date receiptTimestamp;

	@OneToOne(cascade = CascadeType.ALL)
	@ConversionAlias("action")
	private ModelActionEntity actionEntity;

	protected UserActionEntity() {}

	public long getId() {
		return id;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public Date getExecutionTimestamp() {
		return executionTimestamp;
	}

	public void setExecutionTimestamp(final Date executionTimestamp) {
		this.executionTimestamp = executionTimestamp;
	}

	public Date getReceiptTimestamp() {
		return receiptTimestamp;
	}

	public void setReceiptTimestamp(final Date receiptTimestamp) {
		this.receiptTimestamp = receiptTimestamp;
	}

	public ModelActionEntity getActionEntity() {
		return actionEntity;
	}

	public void setActionEntity(final ModelActionEntity actionEntity) {
		this.actionEntity = actionEntity;
	}

	public String getProjectId() {
		return projectId;
	}

	public void setProjectId(final String projectId) {
		this.projectId = projectId;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(final String userId) {
		this.userId = userId;
	}

	public String getUniqueId() {
		return uniqueId;
	}

	public void setUniqueId(final String uniqueId) {
		this.uniqueId = uniqueId;
	}

}
