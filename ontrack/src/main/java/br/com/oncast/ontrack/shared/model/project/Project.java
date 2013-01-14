package br.com.oncast.ontrack.shared.model.project;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.description.Description;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.shared.model.kanban.Kanban;
import br.com.oncast.ontrack.shared.model.metadata.HasMetadata;
import br.com.oncast.ontrack.shared.model.metadata.Metadata;
import br.com.oncast.ontrack.shared.model.metadata.MetadataType;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.tag.Tag;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.HasUUID;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.UUIDUtils;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.SetMultimap;

public class Project implements Serializable, HasUUID {

	private static final long serialVersionUID = 1L;

	private ProjectRepresentation projectRepresentation;
	private Scope projectScope;
	private Release projectRelease;
	private Map<Release, Kanban> kanbanMap;
	private Set<UserRepresentation> users;
	private Map<UUID, List<Annotation>> annotationsMap;
	private Set<FileRepresentation> fileRepresentations;
	private ListMultimap<UUID, Checklist> checklistMap;
	private Map<UUID, Description> descriptionMap;
	private Map<HasMetadata, SetMultimap<MetadataType, Metadata>> metadataMap;
	private Map<String, Tag> tags;

	// IMPORTANT The default constructor is used by GWT and by Mind map converter to construct new scopes. Do not remove this.
	protected Project() {}

	public Project(final ProjectRepresentation projectRepresentation, final Scope projectScope, final Release projectRelease) {
		kanbanMap = new HashMap<Release, Kanban>();

		this.projectRepresentation = projectRepresentation;
		this.projectScope = projectScope;
		this.projectRelease = projectRelease;

		annotationsMap = new HashMap<UUID, List<Annotation>>();
		descriptionMap = new HashMap<UUID, Description>();
		tags = new HashMap<String, Tag>();

		checklistMap = ArrayListMultimap.create();
		users = new HashSet<UserRepresentation>();
		fileRepresentations = new HashSet<FileRepresentation>();
		metadataMap = new HashMap<HasMetadata, SetMultimap<MetadataType, Metadata>>();
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

	public UserRepresentation getUser(final UUID userId) {
		for (final UserRepresentation user : users) {
			if (user.getId().equals(userId)) return user;
		}
		return null;
	}

	public void addAnnotation(final UUID subjectId, final Annotation annotation) {
		if (!annotationsMap.containsKey(subjectId)) annotationsMap.put(subjectId, new ArrayList<Annotation>());
		annotationsMap.get(subjectId).add(0, annotation);
	}

	public void removeAnnotation(final UUID subjectId, final Annotation annotation) {
		if (!annotationsMap.containsKey(subjectId)) return;

		annotationsMap.get(subjectId).remove(annotation);
	}

	public Annotation getAnnotation(final UUID subjectId, final UUID annotationId) {
		if (!annotationsMap.containsKey(subjectId)) return null;

		for (final Annotation annotation : annotationsMap.get(subjectId)) {
			if (annotation.getId().equals(annotationId)) return annotation;
		}
		return null;
	}

	public List<Annotation> getAnnotationsFor(final UUID subjectId) {
		if (!annotationsMap.containsKey(subjectId)) return new ArrayList<Annotation>();

		return new ArrayList<Annotation>(annotationsMap.get(subjectId));
	}

	public void setUserList(final Set<UserRepresentation> userList) {
		users = userList;
	}

	public void addUser(final UserRepresentation user) {
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

	public Checklist findChecklist(final UUID subjectId, final UUID checklistId) {
		for (final Checklist checklist : checklistMap.get(subjectId)) {
			if (checklist.getId().equals(checklistId)) return checklist;
		}
		return null;
	}

	public List<Checklist> findChecklistsFor(final UUID subjectId) {
		return new ArrayList<Checklist>(checklistMap.get(subjectId));
	}

	public boolean removeChecklist(final UUID subjectId, final Checklist checklist) {
		return checklistMap.remove(subjectId, checklist);
	}

	public void removeUser(final UserRepresentation user) {
		users.remove(user);
	}

	public List<UserRepresentation> getUsers() {
		return new ArrayList<UserRepresentation>(users);
	}

	public void addMetadata(final Metadata metadata) {
		final HasMetadata subject = metadata.getSubject();
		if (!hasMetadata(subject)) {
			final HashMultimap<MetadataType, Metadata> multimap = HashMultimap.create();
			metadataMap.put(subject, multimap);
		}
		metadataMap.get(subject).put(metadata.getMetadataType(), metadata);
	}

	public void removeMetadata(final Metadata metadata) {
		if (!hasMetadata(metadata.getSubject())) return;

		metadataMap.get(metadata.getSubject()).remove(metadata.getMetadataType(), metadata);
	}

	@SuppressWarnings("unchecked")
	public <T extends Metadata> List<T> getMetadataList(final HasMetadata subject, final MetadataType type) {
		if (!hasMetadata(subject)) return ImmutableList.of();

		return (List<T>) ImmutableList.copyOf(metadataMap.get(subject).get(type));
	}

	public boolean hasMetadata(final HasMetadata subject, final MetadataType type) {
		return !getMetadataList(subject, type).isEmpty();
	}

	@SuppressWarnings("unchecked")
	public <T extends Metadata> T findMetadata(final HasMetadata subject, final MetadataType type, final UUID metadataId) {
		if (metadataId == null) throw new IllegalArgumentException("metadataId can not be null");
		if (!hasMetadata(subject)) return null;

		for (final Metadata metadata : metadataMap.get(subject).get(type)) {
			if (metadata.getId().equals(metadataId)) return (T) metadata;
		}
		return null;
	}

	public boolean hasMetadata(final HasMetadata subject) {
		if (!metadataMap.containsKey(subject)) return false;
		return !metadataMap.get(subject).isEmpty();
	}

	public void addDescription(final Description description, final UUID subjectId) {
		if (!descriptionMap.containsKey(subjectId)) descriptionMap.put(subjectId, description);
		else {
			descriptionMap.remove(subjectId);
			descriptionMap.put(subjectId, description);
		}
	}

	public Description findDescriptionFor(final UUID subjectId) {
		return descriptionMap.get(subjectId);
	}

	public boolean removeDescriptionFor(final UUID subjectId) {
		return descriptionMap.remove(subjectId) != null;
	}

	@SuppressWarnings("unchecked")
	public <T extends Metadata> List<T> getMetadataList(final MetadataType metadataType) {
		final List<T> metadataList = new ArrayList<T>();

		for (final SetMultimap<MetadataType, Metadata> setMultimap : metadataMap.values()) {
			metadataList.addAll((Collection<T>) setMultimap.get(metadataType));
		}

		return metadataList;
	}

	public void addTag(final Tag tag) {
		tags.put(tag.getDescription().trim().toLowerCase(), tag);
	}

	public boolean hasTag(final String tagDescription) {
		return tags.containsKey(tagDescription.trim().toLowerCase());
	}

	public Tag removeTag(final Tag tag) {
		return removeTag(tag.getDescription());
	}

	private Tag removeTag(final String tagDescription) {
		return tags.remove(tagDescription.trim().toLowerCase());
	}

	public Tag getTag(final String tagDescription) {
		return tags.get(tagDescription);
	}

	public Tag getTag(final UUID tagId) {
		for (final Tag tag : tags.values()) {
			if (tag.getId().equals(tagId)) return tag;
		}
		return null;
	}

	public List<Tag> getTags() {
		return new ArrayList<Tag>(tags.values());
	}

	@Override
	public UUID getId() {
		return projectRepresentation.getId();
	}

	@Override
	public int hashCode() {
		return UUIDUtils.hashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return UUIDUtils.equals(this, obj);
	}

}
