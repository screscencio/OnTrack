package br.com.oncast.ontrack.shared.model.action;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.UserActionEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.action.exceptions.UnableToCompleteActionException;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.HasUUID;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.UUIDUtils;
import br.com.oncast.ontrack.utils.deepEquality.IgnoredByDeepEquality;

import java.io.Serializable;
import java.util.Date;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;

@ConvertTo(UserActionEntity.class)
public class UserAction implements HasUUID, Serializable {

	private static final long serialVersionUID = 1L;

	@IgnoredByDeepEquality
	@ConversionAlias("id")
	private long sequencialId;

	@Element
	@IgnoredByDeepEquality
	private UUID uniqueId;

	@IgnoredByDeepEquality
	private UUID projectId;

	@Element
	@IgnoredByDeepEquality
	private UUID userId;

	@Attribute
	@IgnoredByDeepEquality
	private Date executionTimestamp;

	@Attribute
	@IgnoredByDeepEquality
	private Date receiptTimestamp;

	@Element
	private ModelAction action;

	protected UserAction() {}

	public UserAction(final ModelAction action, final UUID userId, final UUID projectId, final Date executionTimestamp) {
		this.uniqueId = new UUID();
		this.action = action;
		this.userId = userId;
		this.projectId = projectId;
		this.executionTimestamp = executionTimestamp;
	}

	public ModelAction execute(final ProjectContext context) throws UnableToCompleteActionException {
		return action.execute(context, new ActionContext(userId, executionTimestamp));
	}

	public UUID getUniqueId() {
		return getId();
	}

	public void setProjectId(final UUID projectId) {
		this.projectId = projectId;
	}

	public void setSequencialId(final long id) {
		this.sequencialId = id;
	}

	public long getSequencialId() {
		return sequencialId;
	}

	public ModelAction getModelAction() {
		return action;
	}

	public Date getExecutionTimestamp() {
		return executionTimestamp;
	}

	public UUID getProjectId() {
		return projectId;
	}

	public UUID getUserId() {
		return userId;
	}

	public Date getReceiptTimestamp() {
		return receiptTimestamp;
	}

	public void setReceiptTimestamp(final Date receiptTimestamp) {
		this.receiptTimestamp = receiptTimestamp;
	}

	@Override
	public boolean equals(final Object obj) {
		return UUIDUtils.equals(this, obj);
	}

	@Override
	public int hashCode() {
		return UUIDUtils.hashCode(this);
	}

	@Override
	public UUID getId() {
		return uniqueId;
	}

}
