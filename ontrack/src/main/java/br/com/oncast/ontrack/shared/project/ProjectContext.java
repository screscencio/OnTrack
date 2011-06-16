package br.com.oncast.ontrack.shared.project;

import br.com.oncast.ontrack.shared.release.Release;
import br.com.oncast.ontrack.shared.scope.Scope;
import br.com.oncast.ontrack.shared.util.uuid.UUID;

public class ProjectContext {

	private final Project project;

	public ProjectContext(final Project project) {
		this.project = project;
	}

	public String getReleaseDescriptionFor(final Release release) {
		if (release == null) return "";
		return release.getFullDescription();
	}

	public Release loadRelease(final String releaseDescription) {
		if (releaseDescription == null || releaseDescription.isEmpty()) return null;

		final String[] releaseDescriptionSegments = releaseDescription.split(Release.SEPARATOR);
		final String descriptionSegment = releaseDescriptionSegments[0];
		final String releaseLoadQuery = !descriptionSegment.equals(project.getProjectRelease().getDescription()) ? releaseDescription : releaseDescription
				.substring(descriptionSegment.length() + Release.SEPARATOR.length(), releaseDescription.length());

		return project.getProjectRelease().loadRelease(releaseLoadQuery);
	}

	// TODO Should this method throw an exception if nothing is found or should all 'users' of this method verify for null return?
	public Scope findScope(final UUID scopeId) {
		return project.getProjectScope().findScope(scopeId);
	}

	public Scope getProjectScope() {
		return project.getProjectScope();
	}

	public Release getProjectRelease() {
		return project.getProjectRelease();
	}

	// TODO Should this method throw an exception if nothing is found or should all 'users' of this method verify for null return?
	public Release findRelease(final UUID releaseId) {
		return project.getProjectRelease().findRelease(releaseId);
	}
}
