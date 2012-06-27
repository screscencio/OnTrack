package br.com.oncast.ontrack.server.services.persistence.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "user", "projectId" }))
public class ProjectAuthorization {

	@Id
	@GeneratedValue
	private long id;

	@OneToOne
	@JoinColumn(name = "user", nullable = false, updatable = false)
	private User user;

	@Column(name = "projectId", nullable = false, updatable = false)
	private String projectId;

	// IMPORTANT A package-visible default constructor is necessary for JPA. Do not remove this.
	protected ProjectAuthorization() {}

	public ProjectAuthorization(final User user, final ProjectRepresentation project) {
		this(user, project.getId());
	}

	public ProjectAuthorization(final User user, final UUID projectId) {
		this.user = user;
		this.projectId = projectId.toStringRepresentation();
	}

	public long getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public UUID getProjectId() {
		return new UUID(projectId);
	}

}
