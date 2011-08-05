package br.com.oncast.ontrack.shared.model.release;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.deepEquality.IgnoredByDeepEquality;

import com.google.gwt.user.client.rpc.IsSerializable;

public class Release implements IsSerializable {

	@IgnoredByDeepEquality
	public static final String SEPARATOR = "/";

	@IgnoredByDeepEquality
	private UUID id;

	private String description;

	@IgnoredByDeepEquality
	private Release parent;

	private List<Release> childrenList;

	private List<Scope> scopeList;

	protected Release() {}

	public Release(final String description) {
		this.id = new UUID();
		this.description = description;

		childrenList = new ArrayList<Release>();
		scopeList = new ArrayList<Scope>();
	}

	public UUID getId() {
		return this.id;
	}

	public String getDescription() {
		return description;
	}

	public String getFullDescription() {
		if (isRoot() || parent.isRoot()) return description;
		return parent.getFullDescription() + SEPARATOR + description;
	}

	public boolean isRoot() {
		return parent == null;
	}

	public void addRelease(final Release release) {
		if (childrenList.contains(release)) return;
		childrenList.add(release);
		release.parent = this;
	}

	// TODO +++Test this
	public Release loadRelease(final String releaseDescription) {
		final String[] releaseDescriptionSegments = releaseDescription.split(SEPARATOR);

		final String descriptionSegment = releaseDescriptionSegments[0];

		Release childRelease = findDirectChildRelease(descriptionSegment);
		if (childRelease == null) childRelease = createNewSubRelease(descriptionSegment);
		if (releaseDescriptionSegments.length == 1) return childRelease;

		return childRelease.loadRelease(releaseDescription.substring(descriptionSegment.length() + SEPARATOR.length(), releaseDescription.length()));
	}

	private Release createNewSubRelease(final String descriptionSegment) {
		final Release newRelease = new Release(descriptionSegment);
		addRelease(newRelease);
		return newRelease;
	}

	private Release findDirectChildRelease(final String releaseDescription) {
		for (final Release release : childrenList)
			if (release.getDescription().toLowerCase().equals(releaseDescription.trim().toLowerCase())) return release;

		return null;
	}

	public List<Release> getChildReleases() {
		return childrenList;
	}

	// TODO Review bidirectional linkage between Scope and Release: This method should then be removed.
	public List<Scope> getScopeList() {
		return scopeList;
	}

	// TODO Review bidirectional linkage between Scope and Release: Analyze bringing this logic to this method.
	public void addScope(final Scope scope) {
		if (scopeList.contains(scope)) return;
		scopeList.add(scope);
	}

	// TODO Review bidirectional linkage between Scope and Release: Analyze bringing this logic to this method.
	public void removeScope(final Scope selectedScope) {
		scopeList.remove(selectedScope);
	}

	public float getEffortSum() {
		float effortSum = 0;

		for (final Release childRelease : childrenList)
			effortSum += childRelease.getEffortSum();

		for (final Scope scope : scopeList)
			effortSum += scope.getEffort().getInfered();

		return effortSum;
	}

	public float getAccomplishedEffortSum() {
		float accomplishedEffortSum = 0;

		for (final Release childRelease : childrenList)
			accomplishedEffortSum += childRelease.getAccomplishedEffortSum();

		for (final Scope scope : scopeList)
			accomplishedEffortSum += scope.getEffort().getAccomplishedEffort();

		return accomplishedEffortSum;
	}

	public boolean isDone() {
		if (scopeList.isEmpty() && childrenList.isEmpty()) return false;

		for (final Scope scope : scopeList)
			if (!scope.getProgress().isDone()) return false;

		for (final Release childRelease : childrenList)
			if (!childRelease.isDone()) return false;

		return true;
	}

	public Release findRelease(final UUID releaseId) {
		if (this.id.equals(releaseId)) return this;

		for (final Release release : childrenList) {
			final Release releaseLoaded = release.findRelease(releaseId);
			if (releaseLoaded != null) return releaseLoaded;
		}

		return null;
	}

	@Override
	public int hashCode() {
		return this.id.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof Release)) return false;
		return this.id.equals(((Release) obj).getId());
	}
}
