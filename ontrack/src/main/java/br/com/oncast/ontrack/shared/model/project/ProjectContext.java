package br.com.oncast.ontrack.shared.model.project;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.annotation.exceptions.AnnotationNotFoundException;
import br.com.oncast.ontrack.shared.model.effort.FibonacciScale;
import br.com.oncast.ontrack.shared.model.kanban.Kanban;
import br.com.oncast.ontrack.shared.model.kanban.KanbanColumn;
import br.com.oncast.ontrack.shared.model.kanban.KanbanFactory;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.progress.ProgressDefinitionManager;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.Release.Condition;
import br.com.oncast.ontrack.shared.model.release.ReleaseDescriptionParser;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.user.exceptions.UserNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ProjectContext {

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
		final Kanban kanban = getKanban(release);

		final List<String> progressDefinitions = new ArrayList<String>();
		for (final KanbanColumn column : kanban.getColumns()) {
			final String description = column.getDescription();
			progressDefinitions.add(description.equals(Progress.DEFAULT_NOT_STARTED_NAME) ? ProgressState.NOT_STARTED.getDescription() : description);
		}
		return progressDefinitions;
	}

	public List<String> getFibonacciScaleForEffort() {
		return FibonacciScale.getFibonacciScaleList();
	}

	private String removeProjectReleaseDescription(final String releaseDescription) {
		final ReleaseDescriptionParser parser = new ReleaseDescriptionParser(releaseDescription);
		final String releaseLoadQuery = !parser.getHeadRelease().equals(project.getProjectRelease().getDescription()) ? releaseDescription : parser
				.getTailReleases();
		return releaseLoadQuery;
	}

	public List<String> getFibonacciScaleForValue() {
		return FibonacciScale.getFibonacciScaleList();
	}

	public Kanban getKanban(final Release release) {
		final Kanban kanban;
		if (project.hasKanbanFor(release)) kanban = project.getKanban(release);
		else {
			kanban = KanbanFactory.createFor(release);
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
		return previousRelease == null ? KanbanFactory.createEmpty() : project.getKanban(previousRelease);
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

	public User findUser(final String userEmail) throws UserNotFoundException {
		final User user = project.getUser(userEmail);
		if (user == null) throw new UserNotFoundException("The user referenced " + userEmail + " was not found.");

		return user;
	}

	public void addAnnotation(final Annotation annotation, final UUID annotatedObjectId) {
		project.addAnnotation(annotation, annotatedObjectId);
	}

	public void removeAnnotation(final Annotation annotation, final UUID annotatedObjectId) {
		if (project.hasAnnotationsFor(annotatedObjectId)) project.removeAnnotation(annotation, annotatedObjectId);
	}

	public Annotation findAnnotation(final UUID annotationId, final UUID annotatedObjectId) throws AnnotationNotFoundException {
		if (!project.hasAnnotationsFor(annotatedObjectId)) throw new AnnotationNotFoundException("The object with id '" + annotatedObjectId
				+ "' has no annotations");

		final Annotation annotation = project.getAnnotation(annotationId, annotatedObjectId);
		if (annotation == null) throw new AnnotationNotFoundException("The Object with id '" + annotatedObjectId + "' does not have the annotations with id '"
				+ annotationId + "'");

		return annotation;
	}

	public List<Annotation> findAnnotationsFor(final UUID annotatedObjectId) {
		if (!project.hasAnnotationsFor(annotatedObjectId)) return new ArrayList<Annotation>();

		return project.getAnnotationsFor(annotatedObjectId);
	}

	public void addUser(final User user) {
		project.addUser(user);
	}

}
