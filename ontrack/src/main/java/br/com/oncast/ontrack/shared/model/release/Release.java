package br.com.oncast.ontrack.shared.model.release;

import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.release.exceptions.ReleaseNotFoundException;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.HasUUID;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.UUIDUtils;
import br.com.oncast.ontrack.shared.utils.WorkingDay;
import br.com.oncast.ontrack.shared.utils.WorkingDayFactory;
import br.com.oncast.ontrack.utils.deepEquality.IgnoredByDeepEquality;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Release implements Serializable, HasUUID {

	private static final long serialVersionUID = 1L;

	@IgnoredByDeepEquality
	public static final String SEPARATOR = "/";

	@IgnoredByDeepEquality
	private UUID id;

	private String description;

	@IgnoredByDeepEquality
	private Release parent;

	private List<Release> childrenList;

	private List<Scope> scopeList;

	@IgnoredByDeepEquality
	private WorkingDay declaredStartDay;

	@IgnoredByDeepEquality
	private WorkingDay declaredEndDay;

	@IgnoredByDeepEquality
	private Float declaredEstimatedVelocity;

	// IMPORTANT The default constructor is used by GWT to construct new releases. Do not remove this.
	protected Release() {}

	public Release(final String description, final UUID id) {
		this.description = description;
		this.id = id;

		childrenList = new ArrayList<Release>();
		scopeList = new ArrayList<Scope>();
	}

	@Override
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

	public boolean isDescendantOf(final Release actionRelease) {
		return actionRelease.getDescendantReleases().contains(this);
	}

	public boolean isLeaf() {
		return childrenList.isEmpty();
	}

	public Release getParent() {
		return parent;
	}

	/**
	 * Returns a copy of the list of children releases. If you want to add or remove a child release, use {@link Release#addChild(Release)} and {@link Release#removeChild(Release)}. Don't manipulate
	 * this list directly.
	 */
	public List<Release> getChildren() {
		return new ArrayList<Release>(childrenList);
	}

	public Release getChild(final int index) {
		return childrenList.get(index);
	}

	public int getChildIndex(final Release release) {
		return childrenList.indexOf(release);
	}

	public int getScopeIndex(final Scope scope) {
		return scopeList.indexOf(scope);
	}

	/**
	 * Appends a release to the end of the children list. If the added release already has an association with other release, this association is removed before the new association is made.
	 */
	public void addChild(final Release release) {
		addChild(-1, release);
	}

	/**
	 * Inserts a release at the specified position into the children list. If the added release already has an association with other release, this association is removed before the new association is
	 * made.
	 * 
	 * If the index position is negative the scope will be added to the end of the list.
	 */
	public void addChild(final int index, final Release release) {
		if (childrenList.contains(release)) return;
		if (release.parent != null) release.parent.removeChild(release);

		if (index < 0 || index >= childrenList.size()) childrenList.add(release);
		else childrenList.add(index, release);

		release.parent = this;
	}

	/**
	 * Removes a release from the children list. This method does the bidirectional dissociation between a parent release and its child.
	 */
	public void removeChild(final Release release) {
		childrenList.remove(release);
		release.parent = null;
	}

	public Release findRelease(final String releaseDescription) throws ReleaseNotFoundException {
		final ReleaseDescriptionParser parser = new ReleaseDescriptionParser(releaseDescription);

		final Release childRelease = findDirectChildRelease(parser.getHeadRelease());
		if (childRelease == null) throw new ReleaseNotFoundException("Could not find the release with description '" + releaseDescription + "'.");

		final String tailReleases = parser.getTailReleases();
		if (!parser.hasNext()) return childRelease;

		return childRelease.findRelease(tailReleases);
	}

	private Release findDirectChildRelease(final String releaseDescription) {
		final String description = releaseDescription.trim().toLowerCase();
		if (description.isEmpty()) return null;
		for (final Release child : childrenList) {
			if (child.getDescription().toLowerCase().equals(description)) return child;
		}
		return null;
	}

	public Release findRelease(final UUID releaseId) {
		if (this.id.equals(releaseId)) return this;

		for (final Release release : childrenList) {
			final Release releaseLoaded = release.findRelease(releaseId);
			if (releaseLoaded != null) return releaseLoaded;
		}

		return null;
	}

	public List<Release> getDescendantReleases() {
		final List<Release> descendatReleases = new ArrayList<Release>();
		for (final Release childRelease : childrenList) {
			descendatReleases.add(childRelease);
			descendatReleases.addAll(childRelease.getDescendantReleases());
		}
		return descendatReleases;
	}

	private List<Release> getDescendantReleasesInTemporalOrder() {
		final List<Release> descendatReleases = new ArrayList<Release>();
		for (final Release childRelease : childrenList) {
			descendatReleases.addAll(childRelease.getDescendantReleasesInTemporalOrder());
			descendatReleases.add(childRelease);
		}
		return descendatReleases;
	}

	public List<Release> getAllReleasesInTemporalOrder() {
		final Release root = getRootRelease();

		final List<Release> descendantReleases = root.getDescendantReleasesInTemporalOrder();
		descendantReleases.add(root);
		return descendantReleases;
	}

	public List<Scope> getAllStoriesInTemporalOrderIncludingDescendantReleases() {
		final List<Scope> scopes = new ArrayList<Scope>();
		for (final Release release : getAllReleasesInTemporalOrder()) {
			scopes.addAll(release.getScopeList());
		}
		return scopes;
	}

	/**
	 * Returns a copy of the list of associated scopes. If you want to add or remove a scope from this release, use {@link Release#addScope(Scope, int)} and {@link Release#removeScope(Scope)}. Do NOT
	 * manipulate this list directly.
	 */
	public List<Scope> getScopeList() {
		return new ArrayList<Scope>(scopeList);
	}

	public List<Scope> getAllScopesIncludingDescendantReleases() {
		final List<Scope> scopes = new ArrayList<Scope>();
		scopes.addAll(scopeList);
		for (final Release release : childrenList) {
			scopes.addAll(release.getAllScopesIncludingDescendantReleases());
		}
		return scopes;
	}

	/**
	 * Adds a scope to the scope list. If the added scope already has an association with other release, the association is not removed before the new association is made.
	 */
	// TODO Remove this method. Use the method below.
	public void addScope(final Scope scope) {
		addScope(scope, -1);
	}

	/**
	 * Adds a scope to the scope list in a specific index. If the added scope already has an association with other release, the association is not removed before the new association is made.
	 * 
	 * If the index position is negative the scope will be added to the end of the list.
	 */
	public void addScope(final Scope scope, final int scopeIndex) {
		if (scopeList.contains(scope) && scopeList.indexOf(scope) == scopeIndex) return;

		// TODO Remove this 'defensive programming' conditional statement.
		if (scope.getRelease() != null && !scope.getRelease().equals(this)) throw new RuntimeException("The scope should not have any release set to be added to this release");

		scopeList.remove(scope);
		if (scopeIndex < 0 || scopeIndex >= scopeList.size()) scopeList.add(scope);
		else scopeList.add(scopeIndex, scope);

		scope.setRelease(this);
	}

	/**
	 * Removes a scope from the scope list. This method does the bidirectional dissociation between release and scope.
	 */
	public int removeScope(final Scope scope) {
		final int index = scopeList.indexOf(scope);
		scopeList.remove(scope);
		scope.setRelease(null);
		return index;
	}

	/**
	 * Removes all scopes from the scope list, doing the bidirectional dissociation between release and each scope in the list.
	 */
	public void removeAllScopes() {
		for (final Scope scope : scopeList) {
			scope.setRelease(null);
		}
		scopeList.clear();
	}

	public boolean containsScope(final Scope scope) {
		return scopeList.contains(scope);
	}

	public void setDescription(final String newReleaseDescription) {
		if (newReleaseDescription == null || newReleaseDescription.isEmpty() || newReleaseDescription.contains(ReleaseDescriptionParser.SEPARATOR))
			throw new RuntimeException("An invalid description was given.");
		description = newReleaseDescription;
	}

	public WorkingDay getStartDay() {
		return hasDeclaredStartDay() ? declaredStartDay.copy() : getInferedStartDay();
	}

	public WorkingDay getInferedStartDay() {
		WorkingDay startDay = null;
		for (final Scope scope : getAllScopesIncludingDescendantReleases()) {
			final WorkingDay scopeStartDay = scope.getProgress().getStartDay();
			if (startDay == null || scopeStartDay != null && scopeStartDay.isBefore(startDay)) {
				startDay = scopeStartDay;
			}
		}
		return startDay;
	}

	public WorkingDay getEndDay() {
		return hasDeclaredEndDay() ? declaredEndDay.copy() : getInferedEndDay();
	}

	public WorkingDay getInferedEndDay() {
		if (!isDone()) return null;

		WorkingDay endDay = null;
		for (final Scope scope : getAllScopesIncludingDescendantReleases()) {
			final WorkingDay scopeEndDay = getScopeEndDay(scope);

			if (endDay == null || (scopeEndDay != null && scopeEndDay.isAfter(endDay))) {
				endDay = scopeEndDay;
			}
		}
		return endDay;
	}

	private WorkingDay getScopeEndDay(final Scope scope) {
		final WorkingDay scopeEndDay = scope.getProgress().getEndDay();
		if (scope.isLeaf()) return scopeEndDay;

		WorkingDay effortAccomplishedDay = null;
		float effortLeft = scope.getEffort().getInfered();

		for (final Scope child : scope.getChildren()) {
			final float accomplished = child.getEffort().getAccomplished();
			if (accomplished == 0) continue;

			final WorkingDay d = (child.isLeaf()) ? child.getProgress().getEndDay() : getScopeEndDay(child);
			if (d != null && d.isAfter(effortAccomplishedDay)) effortAccomplishedDay = d;

			if ((effortLeft -= accomplished) <= 0) break;
		}

		return WorkingDay.getEarliest(scopeEndDay, effortAccomplishedDay);
	}

	public Float getValueSum() {
		float valueSum = 0;

		for (final Scope scope : getAllScopesIncludingDescendantReleases())
			valueSum += scope.getValue().getInfered();

		return valueSum;
	}

	public float getEffortSum() {
		float effortSum = 0;

		for (final Scope scope : getAllScopesIncludingDescendantReleases())
			effortSum += scope.getEffort().getInfered();

		return effortSum;
	}

	public float getAccomplishedEffortSum() {
		float accomplishedEffortSum = 0;

		for (final Scope scope : getAllScopesIncludingDescendantReleases())
			accomplishedEffortSum += scope.getEffort().getAccomplished();

		return accomplishedEffortSum;
	}

	public float getAccomplishedValueSum() {
		float accomplishedValueSum = 0;

		for (final Scope scope : getAllScopesIncludingDescendantReleases())
			accomplishedValueSum += scope.getValue().getAccomplished();

		return accomplishedValueSum;
	}

	public Float getActualSpeed() {
		final WorkingDay endDay = WorkingDay.getEarliest(getInferedEndDay(), WorkingDayFactory.create());
		final WorkingDay startDay = getStartDay();

		if (endDay.isBefore(startDay)) return null;

		return getAccomplishedEffortSum() / startDay.countTo(endDay);
	}

	public boolean isDone() {
		if (scopeList.isEmpty() && childrenList.isEmpty()) return false;

		for (final Scope scope : scopeList)
			if (!scope.getProgress().isDone()) return false;

		for (final Release childRelease : childrenList)
			if (!childRelease.isDone()) return false;

		return true;
	}

	/**
	 * Finds the latest past release that satisfies the given condition
	 * 
	 * @param condition
	 *            that tests if the release is the right one
	 * @return the latest release that satisfies the given condition or null if any past release satisfies the condition
	 */
	public Release getLatestPastRelease(final Condition condition) {
		final List<Release> allReleases = getAllReleasesInTemporalOrder();
		final int myIndex = allReleases.indexOf(this);

		for (int i = myIndex - 1; i >= 0; i--) {
			final Release pastRelease = allReleases.get(i);
			if (condition.eval(pastRelease)) return pastRelease;
		}

		return null;
	}

	/**
	 * Finds the first future release that satisfies the given condition
	 * 
	 * @param condition
	 *            that tests if the release is the right one
	 * @return the first release that satisfies the given condition or null if any future release satisfies the condition
	 */
	public Release getFirstFutureRelease(final Condition condition) {
		final List<Release> allReleases = getAllReleasesInTemporalOrder();
		final int myIndex = allReleases.indexOf(this);

		for (int i = myIndex + 1; i < allReleases.size(); i++) {
			final Release futureRelease = allReleases.get(i);
			if (condition.eval(futureRelease)) return futureRelease;
		}

		return null;
	}

	public Release getRootRelease() {
		Release current = this;
		while (!current.isRoot())
			current = current.getParent();
		return current;
	}

	public interface Condition {
		public boolean eval(Release release);
	}

	@Override
	public int hashCode() {
		return UUIDUtils.hashCode(this);
	}

	@Override
	public boolean equals(final Object obj) {
		return UUIDUtils.equals(this, obj);
	}

	@Override
	public String toString() {
		return getFullDescription();
	}

	public boolean hasDirectScopes() {
		return !scopeList.isEmpty();
	}

	public boolean hasChildren() {
		return !childrenList.isEmpty();
	}

	public void declareStartDay(final WorkingDay workingDay) {
		declaredStartDay = workingDay;
	}

	public void declareEndDay(final WorkingDay workingDay) {
		declaredEndDay = workingDay;
	}

	public boolean hasDeclaredStartDay() {
		return declaredStartDay != null;
	}

	public boolean hasDeclaredEndDay() {
		return declaredEndDay != null;
	}

	public void declareEstimatedVelocity(final Float declaredVelocity) {
		this.declaredEstimatedVelocity = declaredVelocity;
	}

	public boolean hasDeclaredEstimatedSpeed() {
		return declaredEstimatedVelocity != null;
	}

	public Float getEstimatedSpeed() {
		return declaredEstimatedVelocity;
	}

	public List<Scope> getTasks() {
		final List<Scope> tasks = new ArrayList<Scope>();
		for (final Scope story : scopeList) {
			tasks.addAll(story.getAllLeafs());
		}
		return tasks;
	}

	public List<Release> getAncestors() {
		final List<Release> ancestors = new ArrayList<Release>();

		Release parent = getParent();
		while (!parent.isRoot()) {
			ancestors.add(parent);
			parent = parent.getParent();
		}
		return ancestors;
	}

	public Long getAverageCycleTime() {
		return getAverage(getCycleTimeExtractor());
	}

	public Long getAverageLeadTime() {
		return getAverage(getLeadTimeExtractor());
	}

	public Long getCycleTimeDeviant() {
		return getDeviant(getCycleTimeExtractor(), getAverageCycleTime());
	}

	public Long getLeadTimeDeviant() {
		return getDeviant(getLeadTimeExtractor(), getAverageLeadTime());
	}

	private Extractor getCycleTimeExtractor() {
		return new Extractor() {
			@Override
			public Long getValue(final Progress progress) {
				return progress.getCycleTime();
			}
		};
	}

	private Extractor getLeadTimeExtractor() {
		return new Extractor() {
			@Override
			public Long getValue(final Progress progress) {
				return progress.getLeadTime();
			}
		};
	}

	private Long getDeviant(final Extractor extractor, final Long average) {
		Long deviant = 0L;
		int scopeCount = 0;
		for (final Scope scope : getAllScopesIncludingDescendantReleases()) {
			if (!scope.getProgress().isDone()) continue;
			scopeCount++;
			final Long variance = extractor.getValue(scope.getProgress()) - average;
			deviant += variance * variance;
		}

		if (scopeCount <= 1) return null;
		return (long) Math.sqrt(deviant / (scopeCount - 1));
	}

	private Long getAverage(final Extractor extractor) {
		Long average = 0L;
		int scopeCount = 0;
		for (final Scope scope : getAllScopesIncludingDescendantReleases()) {
			scopeCount++;
			average += extractor.getValue(scope.getProgress());
		}

		if (scopeCount == 0) return null;
		return average / scopeCount;
	}

	private interface Extractor {
		Long getValue(Progress progress);
	}
}