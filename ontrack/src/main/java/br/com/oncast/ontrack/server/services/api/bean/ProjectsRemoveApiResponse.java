package br.com.oncast.ontrack.server.services.api.bean;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ProjectsRemoveApiResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	@XmlElementWrapper(name = "removedProjects")
	@XmlElement(name = "removedProject")
	private Set<UUID> removedProjects = null;

	@XmlElementWrapper(name = "failedProjects")
	@XmlElement(name = "failedProject")
	private Set<ProjectRemoveFail> failures = null;

	public ProjectsRemoveApiResponse() {
		this.removedProjects = new HashSet<UUID>();
		this.failures = new HashSet<ProjectRemoveFail>();
	}

	public void removedSuccessfully(final UUID... projects) {
		removedProjects.addAll(Arrays.asList(projects));
	}

	public void failedToRemove(final UUID projectId, final Exception e) {
		failures.add(new ProjectRemoveFail(projectId, e.getMessage()));
	}

	public Set<UUID> getRemovedProjects() {
		return removedProjects;
	}

	public Set<ProjectRemoveFail> getFailures() {
		return failures;
	}

	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static class ProjectRemoveFail {

		private UUID projectId;

		private String errorMessage;

		ProjectRemoveFail() {}

		ProjectRemoveFail(final UUID projectId, final String error) {
			this.projectId = projectId;
			this.errorMessage = error;
		}

		public UUID getProjectId() {
			return projectId;
		}

		public String getErrorMessage() {
			return errorMessage;
		}

	}

}
