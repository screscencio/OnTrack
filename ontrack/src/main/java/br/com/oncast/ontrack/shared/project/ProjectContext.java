package br.com.oncast.ontrack.shared.project;

import br.com.oncast.ontrack.shared.release.Release;

public class ProjectContext {

	private final Project project;

	public ProjectContext(final Project project) {
		this.project = project;
	}

	public String getReleaseDescription(final Release release) {
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
}
