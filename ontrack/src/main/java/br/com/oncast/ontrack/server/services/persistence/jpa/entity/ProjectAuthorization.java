package br.com.oncast.ontrack.server.services.persistence.jpa.entity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "user", "project" }))
public class ProjectAuthorization {

	@Id
	@GeneratedValue
	private long id;

	@OneToOne
	@JoinColumn(name = "user", nullable = false, updatable = false)
	private User user;

	@OneToOne
	@JoinColumn(name = "project", nullable = false, updatable = false)
	private ProjectRepresentation project;

	@SuppressWarnings("unused")
	// IMPORTANT A package-visible default constructor is necessary for JPA. Do not remove this.
	private ProjectAuthorization() {}

	public ProjectAuthorization(final User user, final ProjectRepresentation project) {
		this.user = user;
		this.project = project;
	}

	public long getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public ProjectRepresentation getProject() {
		return project;
	}

}