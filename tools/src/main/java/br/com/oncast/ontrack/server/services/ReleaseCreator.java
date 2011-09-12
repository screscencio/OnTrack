package br.com.oncast.ontrack.server.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.actions.ReleaseCreateActionDefault;
import br.com.oncast.ontrack.shared.model.release.Release;

public class ReleaseCreator {

	private Set<String> createdReleases = new HashSet<String>();

	public boolean releaseAlreadyCreated(final String releaseDescription) {
		final String[] releaseLevels = releaseDescription.split(Release.SEPARATOR);

		for (int i = 0; i < releaseLevels.length; i++) {
			if (!createdReleases.contains(getReleaseDescriptionUntilLevel(releaseLevels, i))) return false;
		}
		return true;
	}

	public List<? extends ModelAction> createNewReleaseHierarchy(final String releaseDescription) {
		final List<ModelAction> newActions = new ArrayList<ModelAction>();

		final String[] releaseLevels = releaseDescription.split(Release.SEPARATOR);

		for (int level = 0; level < releaseLevels.length; level++) {
			final String releaseLevelDescription = getReleaseDescriptionUntilLevel(releaseLevels, level);
			if (!createdReleases.contains(releaseLevelDescription)) {
				newActions.add(new ReleaseCreateActionDefault(releaseLevelDescription));
				createdReleases.add(releaseLevelDescription);
			}
		}

		return newActions;
	}

	private String getReleaseDescriptionUntilLevel(final String[] releaseLevels, final int index) {
		final StringBuilder query = new StringBuilder();
		query.append(releaseLevels[0]);

		for (int i = 1; i <= index; i++) {
			query.append(Release.SEPARATOR);
			query.append(releaseLevels[i]);
		}
		return query.toString();
	}
}
