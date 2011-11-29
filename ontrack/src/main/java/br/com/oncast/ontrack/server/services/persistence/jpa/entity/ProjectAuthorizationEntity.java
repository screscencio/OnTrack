package br.com.oncast.ontrack.server.services.persistence.jpa.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.oncast.ontrack.server.services.persistence.jpa.entity.user.UserEntity;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "user", "project" }))
public class ProjectAuthorizationEntity {

	@Id
	@GeneratedValue
	private long id;

	@OneToOne
	@JoinColumn(name = "user", nullable = false, updatable = false)
	private final UserEntity user;

	@OneToOne
	@JoinColumn(name = "project", nullable = false, updatable = false)
	private final ProjectRepresentation project;

	public ProjectAuthorizationEntity(final UserEntity user, final ProjectRepresentation project) {
		this.user = user;
		this.project = project;
	}

	public long getId() {
		return id;
	}

	public UserEntity getUser() {
		return user;
	}

	public ProjectRepresentation getProject() {
		return project;
	}

}
