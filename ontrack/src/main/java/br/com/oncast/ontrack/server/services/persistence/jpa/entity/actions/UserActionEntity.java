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
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConversionAlias;
import br.com.oncast.ontrack.server.utils.typeConverter.annotations.ConvertTo;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;

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
	private ProjectRepresentation projectRepresentation;

	public UserActionEntity() {}

	public UserActionEntity(final ModelActionEntity actionEntity, final ProjectRepresentation projectRepresentation, final Date timestamp) {
		this.actionEntity = actionEntity;
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

	public ProjectRepresentation getProjectRepresentation() {
		return projectRepresentation;
	}

	public void setProjectRepresentation(final ProjectRepresentation projectRepresentation) {
		this.projectRepresentation = projectRepresentation;
	}
}
