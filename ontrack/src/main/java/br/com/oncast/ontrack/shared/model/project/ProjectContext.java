package br.com.oncast.ontrack.shared.model.project;

import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.scope.exceptions.ScopeNotFoundException;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ProjectContext {

	private final Project project;

	public ProjectContext(final Project project) {
		this.project = project;
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

		final String[] releaseDescriptionSegments = releaseDescription.split(Release.SEPARATOR);
		final String descriptionSegment = releaseDescriptionSegments[0];
		final String releaseLoadQuery = !descriptionSegment.equals(project.getProjectRelease().getDescription()) ? releaseDescription : releaseDescription
				.substring(descriptionSegment.length() + Release.SEPARATOR.length(), releaseDescription.length());

		return project.getProjectRelease().findRelease(releaseLoadQuery);
	}

	public Scope getProjectScope() {
		return project.getProjectScope();
	}

	public Release getProjectRelease() {
		return project.getProjectRelease();
	}
}
