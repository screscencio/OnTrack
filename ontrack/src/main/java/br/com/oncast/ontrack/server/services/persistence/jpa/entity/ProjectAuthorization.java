package br.com.oncast.ontrack.server.services.persistence.jpa.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "userId", "projectId" }))
public class ProjectAuthorization {

	@Id
	@GeneratedValue
	private long id;

	@Column(name = "userId", nullable = false, updatable = false)
	private String userId;

	@Column(name = "projectId", nullable = false, updatable = false)
	private String projectId;

	// IMPORTANT A package-visible default constructor is necessary for JPA. Do not remove this.
	protected ProjectAuthorization() {}

	public ProjectAuthorization(final User user, final ProjectRepresentation project) {
		this(user.getId(), project.getId());
	}

	public ProjectAuthorization(final UUID userId, final UUID projectId) {
		this.userId = userId.toString();
		this.projectId = projectId.toString();
	}

	public long getId() {
		return id;
	}

	public UUID getUserId() {
		return new UUID(userId);
	}

	public UUID getProjectId() {
		return new UUID(projectId);
	}

}
