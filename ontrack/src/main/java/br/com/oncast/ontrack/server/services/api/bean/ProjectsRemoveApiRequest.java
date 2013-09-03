package br.com.oncast.ontrack.server.services.api.bean;

import br.com.oncast.ontrack.shared.model.uuid.UUID;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class ProjectsRemoveApiRequest implements Serializable, Iterable<UUID> {

	private static final long serialVersionUID = 1L;

	@XmlElementWrapper(name = "projectsToBeRemoved")
	@XmlElement(name = "projectId")
	private Set<UUID> projectsToBeRemoved;

	ProjectsRemoveApiRequest() {}

	public ProjectsRemoveApiRequest(final UUID... projectId) {
		this(Arrays.asList(projectId));
	}

	public ProjectsRemoveApiRequest(final Collection<UUID> projectsToBeRemoved) {
		this.projectsToBeRemoved = new HashSet<UUID>(projectsToBeRemoved);
	}

	public void add(final UUID... projectIds) {
		this.projectsToBeRemoved.addAll(Arrays.asList(projectIds));
	}

	@Override
	public Iterator<UUID> iterator() {
		return projectsToBeRemoved.iterator();
	}

}
