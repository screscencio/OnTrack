package br.com.oncast.ontrack.shared.model.project;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.annotation.AnnotationType;
import br.com.oncast.ontrack.shared.model.annotation.exceptions.AnnotationNotFoundException;
import br.com.oncast.ontrack.shared.model.checklist.Checklist;
import br.com.oncast.ontrack.shared.model.checklist.exception.ChecklistNotFoundException;
import br.com.oncast.ontrack.shared.model.description.Description;
import br.com.oncast.ontrack.shared.model.description.exceptions.DescriptionNotFoundException;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.shared.model.file.exceptions.FileRepresentationNotFoundException;
import br.com.oncast.ontrack.shared.model.kanban.Kanban;
import br.com.oncast.ontrack.shared.model.kanban.KanbanColumn;
import br.com.oncast.ontrack.shared.model.kanban.KanbanFactory;
import br.com.oncast.ontrack.shared.model.kanban.exception.KanbanColumnNotFoundException;
import br.com.oncast.ontrack.shared.model.metadata.HasMetadata;
import br.com.oncast.ontrack.shared.model.metadata.HumanIdMetadata;
import br.com.oncast.ontrack.shared.model.metadata.Metadata;
import br.com.oncast.ontrack.shared.model.metadata.MetadataType;
import br.com.oncast.ontrack.shared.model.metadata.TagAssociationMetadata;
import br.com.oncast.ontrack.shared.model.metadata.exceptions.MetadataNotFoundException;
import br.com.oncast.ontrack.shared.model.prioritizationCriteria.FibonacciScale;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.progress.ProgressDefinitionManager;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.Release.Condition;
import br.com.oncast.ontrack.shared.model.release.ReleaseDescriptionParser;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.tag.Tag;
import br.com.oncast.ontrack.shared.model.tag.exception.TagNotFoundException;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.user.exceptions.UserNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.HasUUID;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.UUIDUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import static br.com.oncast.ontrack.shared.model.annotation.AnnotationType.OPEN_IMPEDIMENT;
import static br.com.oncast.ontrack.shared.model.annotation.AnnotationType.SOLVED_IMPEDIMENT;

public class ProjectContext implements HasUUID {

	private final Project project;

	public ProjectContext(final Project project) {
		this.project = project;
		ProgressDefinitionManager.getInstance().populate();
	}

	public ProjectRepresentation getProjectRepresentation() {
		return project.getProjectRepresentation();
	}

	public String getReleaseDescriptionFor(final Release release) {
		if (release == null) return "";
		return release.getFullDescription();
	}

	// TODO +++Cache results???!!!! so that it can be faster (eg. some actions with subactions call this multiple times)
	public Scope findScope(final UUID scopeId) throws ScopeNotFoundException {
		final Scope scope = project.getProjectScope().findScope(scopeId);
		if (scope == null) throw new ScopeNotFoundException("The scope referenced by id " + scopeId + " was not found.");
		return scope;
	}

	// TODO +++Cache results???!!!! so that it can be faster (eg. some actions with subactions call this multiple times)
	public Release findRelease(final UUID releaseId) throws ReleaseNotFoundException {
		final Release release = project.getProjectRelease().findRelease(releaseId);
		if (release == null) throw new ReleaseNotFoundException("The release referenced by id " + releaseId + " was not found.");
		return release;
	}

	// TODO +++Cache results???!!!! so that it can be faster (eg. some actions with subactions call this multiple times)
	public Release findRelease(final String releaseDescription) throws ReleaseNotFoundException {
		if (releaseDescription == null || releaseDescription.isEmpty()) return null;

		final String releaseLoadQuery = removeProjectReleaseDescription(releaseDescription);

		return project.getProjectRelease().findRelease(releaseLoadQuery);
	}

	public Scope getProjectScope() {
		return project.getProjectScope();
	}

	public Release getProjectRelease() {
		return project.getProjectRelease();
	}

	public List<Release> getDescendantReleases() {
		return project.getProjectRelease().getDescendantReleases();
	}

	public List<String> getProgressDefinitions(final Scope scope) {
		final Release release = scope.getRelease();
		if (release == null) return ProgressDefinitionManager.getInstance().getProgressDefinitions();

		return getProgressDefinitionsFromRelease(release);
	}

	private List<String> getProgressDefinitionsFromRelease(final Release release) {
		final List<String> progressDefinitions = new ArrayList<String>();
		for (final ProgressState state : ProgressState.values())
			progressDefinitions.add(state.getDescription());

		final Kanban kanban = getKanban(release);

		for (final KanbanColumn column : kanban.getColumns()) {
			final String description = column.getDescription();
			final String d = description.equals(Progress.DEFAULT_NOT_STARTED_NAME) ? ProgressState.NOT_STARTED.getDescription() : description;
			if (!progressDefinitions.contains(d)) progressDefinitions.add(d);
		}
		return progressDefinitions;
	}

	public List<String> getFibonacciScaleForEffort() {
		return FibonacciScale.getFibonacciScaleList();
	}

	private String removeProjectReleaseDescription(final String releaseDescription) {
		final ReleaseDescriptionParser parser = new ReleaseDescriptionParser(releaseDescription);
		final String releaseLoadQuery = !parser.getHeadRelease().equals(project.getProjectRelease().getDescription()) ? releaseDescription : parser.getTailReleases();
		return releaseLoadQuery;
	}

	public List<String> getFibonacciScaleForValue() {
		return FibonacciScale.getFibonacciScaleList();
	}

	public Kanban getKanban(final Release release) {
		final Kanban kanban;
		if (project.hasKanbanFor(release)) kanban = project.getKanban(release);
		else {
			kanban = KanbanFactory.create();
			project.setKanban(release, kanban);
		}
		if (!kanban.isLocked()) kanban.merge(getPreviousKanbanFrom(release));

		return kanban;
	}

	private Kanban getPreviousKanbanFrom(final Release release) {
		final Release previousRelease = release.getLatestPastRelease(new Condition() {
			@Override
			public boolean eval(final Release release) {
				return project.hasKanbanFor(release);
			}
		});
		return previousRelease == null ? KanbanFactory.create() : project.getKanban(previousRelease);
	}

	public Set<Release> getAllReleasesWithDirectScopes() {
		final HashSet<Release> releases = new HashSet<Release>();

		for (final Release release : getProjectRelease().getDescendantReleases()) {
			if (release.hasDirectScopes()) {
				releases.add(release);
			}
		}
		return releases;
	}

	public void addAnnotation(final UUID subjectId, final Annotation annotation) {
		project.addAnnotation(subjectId, annotation);
	}

	public void removeAnnotation(final UUID subjectId, final Annotation annotation) {
		project.removeAnnotation(subjectId, annotation);
	}

	public Annotation findAnnotation(final UUID subjectId, final UUID annotationId) throws AnnotationNotFoundException {
		final Annotation annotation = project.getAnnotation(subjectId, annotationId);
		if (annotation == null) throw new AnnotationNotFoundException("The Object with id '" + subjectId + "' does not have the annotations with id '" + annotationId + "'");

		return annotation;
	}

	public List<Annotation> findAnnotationsFor(final UUID subjectId) {
		return project.getAnnotationsFor(subjectId);
	}

	public List<Annotation> findImpedimentsFor(final UUID subjectId) {
		final List<Annotation> impediments = new ArrayList<Annotation>();
		for (final Annotation annotation : findAnnotationsFor(subjectId)) {
			final AnnotationType type = annotation.getType();
			if (type == OPEN_IMPEDIMENT || type == SOLVED_IMPEDIMENT) impediments.add(annotation);
		}
		return impediments;
	}

	public boolean hasAnnotationsFor(final UUID subjectId) {
		return !findAnnotationsFor(subjectId).isEmpty();
	}

	public boolean hasChecklistsFor(final UUID subjectId) {
		return !findChecklistsFor(subjectId).isEmpty();
	}

	public void addUser(final UserRepresentation user) {
		project.addUser(user);
	}

	public void addFileRepresentation(final FileRepresentation representation) {
		project.addFileRepresentation(representation);
	}

	public FileRepresentation findFileRepresentation(final UUID fileRepresentationId) throws FileRepresentationNotFoundException {
		final FileRepresentation representation = project.findFileRepresentation(fileRepresentationId);
		if (representation == null) throw new FileRepresentationNotFoundException("The file with id '" + fileRepresentationId + "' was not found");
		return representation;
	}

	public void addChecklist(final UUID subjectId, final Checklist checklist) {
		project.addChecklist(checklist, subjectId);
	}

	public void removeChecklist(final UUID subjectId, final Checklist checklist) {
		project.removeChecklist(subjectId, checklist);
	}

	public Checklist findChecklist(final UUID subjectId, final UUID checklistId) throws ChecklistNotFoundException {
		final Checklist checklist = project.findChecklist(subjectId, checklistId);
		if (checklist == null)
			throw new ChecklistNotFoundException("The checklist with id '" + checklistId.toString() + "' and associated with the subject with id '" + subjectId.toString() + "' was not found.");
		return checklist;
	}

	public List<Checklist> findChecklistsFor(final UUID subjectId) {
		return project.findChecklistsFor(subjectId);
	}

	public void removeUser(final UserRepresentation user) {
		project.removeUser(user);
	}

	public List<UserRepresentation> getUsers() {
		return project.getUsers();
	}

	public UserRepresentation findUser(final UUID userId) throws UserNotFoundException {
		final UserRepresentation user = project.getUser(userId);
		if (user == null) throw new UserNotFoundException("The user '" + userId.toString() + "' was not found.");

		return user;
	}

	public void addMetadata(final Metadata metadata) {
		project.addMetadata(metadata);
	}

	public void removeMetadata(final Metadata metadata) {
		project.removeMetadata(metadata);
	}

	public <T extends Metadata> List<T> getMetadataList(final HasMetadata subject, final MetadataType metadataType) {
		return project.getMetadataList(subject, metadataType);
	}

	public boolean hasMetadata(final HasMetadata subject, final MetadataType metadataType) {
		return project.hasMetadata(subject, metadataType);
	}

	public <T extends Metadata> T findMetadata(final HasMetadata subject, final MetadataType metadataType, final UUID metadataId) throws MetadataNotFoundException {
		final T metadata = project.findMetadata(subject, metadataType, metadataId);
		if (metadata == null) throw new MetadataNotFoundException("The metadata was not found");

		return metadata;
	}

	public void addDescription(final Description description, final UUID subjectId) {
		project.addDescription(description, subjectId);
	}

	public Description findDescriptionFor(final UUID subjectId) throws DescriptionNotFoundException {
		final Description description = project.findDescriptionFor(subjectId);
		if (description == null) throw new DescriptionNotFoundException("The Object with id '" + subjectId + "' does not have description associated.");
		return description;
	}

	public boolean removeDescriptionFor(final UUID subjectId) {
		return project.removeDescriptionFor(subjectId);
	}

	public <T extends Metadata> List<T> getAllMetadata(final MetadataType metadataType) {
		return project.getMetadataList(metadataType);
	}

	@Override
	public UUID getId() {
		return project.getId();
	}

	@Override
	public int hashCode() {
		return UUIDUtils.hashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return UUIDUtils.equals(this, obj);
	}

	public void addTag(final Tag tag) {
		project.addTag(tag);
	}

	public boolean hasTag(final String tagDescription) {
		return project.hasTag(tagDescription);
	}

	public void removeTag(final Tag tag) {
		project.removeTag(tag);
	}

	public Tag findTag(final String tagDescription) throws TagNotFoundException {
		final Tag tag = project.getTag(tagDescription);
		if (tag == null) throw new TagNotFoundException();
		return tag;
	}

	public Tag findTag(final UUID tagId) throws TagNotFoundException {
		final Tag tag = project.getTag(tagId);
		if (tag == null) throw new TagNotFoundException();
		return tag;
	}

	public List<Tag> getAllTags() {
		return project.getTags();
	}

	public boolean hasDescriptionFor(final UUID subjectId) {
		return project.hasDescription(subjectId);
	}

	public String getHumanId(final Scope scope) {
		final List<HumanIdMetadata> list = getMetadataList(scope, MetadataType.HUMAN_ID);
		return list.isEmpty() ? "" : list.get(0).getHumanId();
	}

	public void declareTimeSpent(final UUID scopeId, final UUID userId, final @Nullable Float timeSpent) {
		project.declareTimeSpent(scopeId, userId, timeSpent);
	}

	public Float getDeclaredTimeSpent(final UUID scopeId, final UUID userId) {
		return project.getDeclaredTimeSpent(scopeId, userId);
	}

	public ArrayList<Tag> getTagsFor(final Scope scope) {
		final ArrayList<Tag> tags = new ArrayList<Tag>();
		for (final TagAssociationMetadata metadata : ClientServices.getCurrentProjectContext().<TagAssociationMetadata> getMetadataList(scope, TagAssociationMetadata.getType())) {
			tags.add(metadata.getTag());
		}
		return tags;
	}

	public KanbanColumn findKanbanColumn(final UUID columnId) throws KanbanColumnNotFoundException {
		final KanbanColumn column = project.findKanbanColumn(columnId);
		if (column == null) throw new KanbanColumnNotFoundException();
		return column;
	}
}
