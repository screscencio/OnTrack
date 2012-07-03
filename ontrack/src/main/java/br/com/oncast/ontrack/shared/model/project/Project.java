package br.com.oncast.ontrack.shared.model.project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.shared.model.kanban.Kanban;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class Project implements Serializable {

	private static final long serialVersionUID = 1L;

	private ProjectRepresentation projectRepresentation;
	private Scope projectScope;
	private Release projectRelease;
	private Map<Release, Kanban> kanbanMap;
	private Set<User> users;
	private Map<UUID, List<Annotation>> annotationsMap;
	private Set<FileRepresentation> fileRepresentations;
	private ListMultimap<UUID, Checklist> checklistMap;

	// IMPORTANT The default constructor is used by GWT and by Mind map converter to construct new scopes. Do not remove this.
	protected Project() {}

	public Project(final ProjectRepresentation projectRepresentation, final Scope projectScope, final Release projectRelease) {
		kanbanMap = new HashMap<Release, Kanban>();

		this.projectRepresentation = projectRepresentation;
		this.projectScope = projectScope;
		this.projectRelease = projectRelease;
		annotationsMap = new HashMap<UUID, List<Annotation>>();
		checklistMap = ArrayListMultimap.create();
		users = new HashSet<User>();
		fileRepresentations = new HashSet<FileRepresentation>();
	}

	public Scope getProjectScope() {
		return projectScope;
	}

	public Release getProjectRelease() {
		return projectRelease;
	}

	public ProjectRepresentation getProjectRepresentation() {
		return projectRepresentation;
	}

	public boolean hasKanbanFor(final Release release) {
		return kanbanMap.containsKey(release);
	}

	public Kanban getKanban(final Release release) {
		return kanbanMap.get(release);
	}

	public void setKanban(final Release release, final Kanban kanban) {
		kanbanMap.put(release, kanban);
	}

	public User getUser(final String userEmail) {
		for (final User user : users) {
			if (user.getEmail().equals(userEmail)) return user;
		}
		return null;
	}

	public void addAnnotation(final Annotation annotation, final UUID annotatedObjectId) {
		if (!hasAnnotationsFor(annotatedObjectId)) annotationsMap.put(annotatedObjectId, new ArrayList<Annotation>());
		annotationsMap.get(annotatedObjectId).add(0, annotation);
	}

	public void removeAnnotation(final Annotation annotation, final UUID annotatedObjectId) {
		if (!annotationsMap.containsKey(annotatedObjectId)) return;

		annotationsMap.get(annotatedObjectId).remove(annotation);
	}

	public Annotation getAnnotation(final UUID annotationId, final UUID annotatedObjectId) {
		for (final Annotation annotation : annotationsMap.get(annotatedObjectId)) {
			if (annotation.getId().equals(annotationId)) return annotation;
		}
		return null;
	}

	public boolean hasAnnotationsFor(final UUID annotatedObjectId) {
		return annotationsMap.containsKey(annotatedObjectId);
	}

	public List<Annotation> getAnnotationsFor(final UUID annotatedObjectId) {
		return new ArrayList<Annotation>(annotationsMap.get(annotatedObjectId));
	}

	public void setUserList(final Set<User> userList) {
		users = userList;
	}

	public void addUser(final User user) {
		users.add(user);
	}

	public void addFileRepresentation(final FileRepresentation representation) {
		fileRepresentations.add(representation);
	}

	public FileRepresentation findFileRepresentation(final UUID fileRepresentationId) {
		for (final FileRepresentation file : fileRepresentations) {
			if (file.getId().equals(fileRepresentationId)) return file;
		}
		return null;
	}

	public void addChecklist(final Checklist checklist, final UUID subjectId) {
		if (!checklistMap.containsEntry(subjectId, checklist)) checklistMap.put(subjectId, checklist);
	}

	public Checklist findChecklist(final UUID checklistId, final UUID subjectId) {
		for (final Checklist checklist : checklistMap.get(subjectId)) {
			if (checklist.getId().equals(checklistId)) return checklist;
		}
		return null;
	}

	public List<Checklist> findChecklistsFor(final UUID subjectId) {
		return checklistMap.get(subjectId);
	}

	public void removeChecklist(final UUID checklistId, final UUID subjectId) {
		checklistMap.remove(subjectId, new Checklist(checklistId, null));
	}

}
