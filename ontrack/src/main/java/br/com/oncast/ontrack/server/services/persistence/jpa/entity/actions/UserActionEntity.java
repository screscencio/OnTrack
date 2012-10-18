package br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.com.oncast.ontrack.server.model.project.UserAction;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.actions.model.ModelActionEntity;
import br.com.oncast.ontrack.server.services.persistence.jpa.entity.project.ProjectRepresentationEntity;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertUsing;
import br.com.oncast.ontrack.server.utils.typeConverter.custom.StringToUuidConverter;

@Entity
@ConvertTo(UserAction.class)
public class UserActionEntity {

	@Id
	@GeneratedValue
	@ConversionAlias("id")
	private long id;

	@Temporal(TemporalType.TIMESTAMP)
	@ConversionAlias("timestamp")
	private Date timestamp;

	@OneToOne(cascade = CascadeType.ALL)
	@ConversionAlias("action")
	private ModelActionEntity actionEntity;

	@ManyToOne(optional = false)
	@ConversionAlias("projectRepresentation")
	private ProjectRepresentationEntity projectRepresentation;

	@ConvertUsing(StringToUuidConverter.class)
	@ConversionAlias("userId")
	private String userId;

	public UserActionEntity() {}

	public UserActionEntity(final ModelActionEntity actionEntity, final String userId, final ProjectRepresentationEntity projectRepresentation,
			final Date timestamp) {
		this.actionEntity = actionEntity;
		this.userId = userId;
		this.projectRepresentation = projectRepresentation;
		this.timestamp = timestamp;
	}

	public long getId() {
		return id;
	}

	public void setId(final long id) {
		this.id = id;
	}

	public ModelActionEntity getActionEntity() {
		return actionEntity;
	}

	public void setActionEntity(final ModelActionEntity actionEntity) {
		this.actionEntity = actionEntity;
	}

	public void setTimestamp(final Date timestamp) {
		this.timestamp = timestamp;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public ProjectRepresentationEntity getProjectRepresentation() {
		return projectRepresentation;
	}

	public void setProjectRepresentation(final ProjectRepresentationEntity projectRepresentation) {
		this.projectRepresentation = projectRepresentation;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(final String userId) {
		this.userId = userId;
	}
}
