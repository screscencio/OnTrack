package br.com.oncast.ontrack.server.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.oncast.ontrack.shared.model.actions.ModelAction;
import br.com.oncast.ontrack.shared.model.actions.ReleaseCreateActionDefault;
import br.com.oncast.ontrack.shared.model.release.ReleaseDescriptionParser;

//TODO analyze if parsing is needed on both methods
public class ReleaseCreator {

	private Set<String> createdReleases = new HashSet<String>();

	public boolean releaseAlreadyCreated(final String releaseDescription) {

		final ReleaseDescriptionParser parser = new ReleaseDescriptionParser(releaseDescription);

		do {
			if (!createdReleases.contains(parser.getFullDescriptionOfHeadRelease())) return false;
		} while (parser.next());

		return true;
	}

	public List<? extends ModelAction> createNewReleaseHierarchy(final String releaseDescription) {
		final List<ModelAction> newActions = new ArrayList<ModelAction>();

		final ReleaseDescriptionParser parser = new ReleaseDescriptionParser(releaseDescription);

		do {
			if (!createdReleases.contains(parser.getFullDescriptionOfHeadRelease())) {
				newActions.add(new ReleaseCreateActionDefault(parser.getFullDescriptionOfHeadRelease()));
				createdReleases.add(parser.getFullDescriptionOfHeadRelease());
			}
		} while (parser.next());

		return newActions;
	}

}
