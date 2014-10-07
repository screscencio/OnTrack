package br.com.oncast.ontrack.shared.model.scope;

import br.com.oncast.ontrack.shared.model.metadata.HasMetadata;
import br.com.oncast.ontrack.shared.model.prioritizationCriteria.Effort;
import br.com.oncast.ontrack.shared.model.prioritizationCriteria.Value;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.user.UserRepresentation;
import br.com.oncast.ontrack.shared.model.uuid.HasUUID;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.utils.UUIDUtils;
import br.com.oncast.ontrack.utils.deepEquality.IgnoredByDeepEquality;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.user.datepicker.client.CalendarUtil;

public class Scope implements Serializable, HasMetadata, HasUUID {

	private static final long serialVersionUID = 1L;

	@IgnoredByDeepEquality
	private UUID id;

	@IgnoredByDeepEquality
	private Scope parent;

	@IgnoredByDeepEquality
	private Release release;

	private String description;

	private List<Scope> childrenList;

	private Effort effort;

	private Value value;

	private Progress progress;

	private Date dueDate;

	// IMPORTANT The default constructor is used by GWT and by Mind map converter to construct new scopes. Do not remove this.
	protected Scope() {}

	public Scope(final String description, final UserRepresentation author, final Date timestamp) {
		this(description, new UUID(), author, timestamp);
	}

	public Scope(final String description, final UUID scopeId, final UserRepresentation author, final Date timestamp) {
		this.id = scopeId;
		this.description = description;
		this.effort = new Effort();
		this.value = new Value();
		this.progress = new Progress(author, timestamp);

		childrenList = new ArrayList<Scope>();
	}

	public Scope cloneWithoutRelationship() {
		final Scope clone = new Scope();
		clone.id = id;
		clone.description = description;
		clone.effort = effort;
		clone.value = value;
		clone.progress = progress;

		return clone;
	}

	@Override
	public UUID getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	/**
	 * Returns a copy of the list of child scopes. If you want to add or remove a scope from this scope, use {@link Scope#add(Scope)} and {@link Scope#remove(Scope)}. Do NOT manipulate this list
	 * directly.
	 */
	public List<Scope> getChildren() {
		return new ArrayList<Scope>(childrenList);
	}

	public int getChildCount() {
		return childrenList.size();
	}

	public Effort getEffort() {
		return effort;
	}

	public Value getValue() {
		return value;
	}

	public Scope getStory() {
		Scope story = this;
		while (!story.isRoot() && !story.hasRelease())
			story = story.getParent();
		return story;
	}

	public Scope add(final Scope scope) {
		childrenList.add(scope);
		scope.parent = this;

		return this;
	}

	public void add(final int beforeIndex, final Scope scope) {
		childrenList.add(beforeIndex, scope);
		scope.parent = this;
	}

	public void remove(final Scope scope) {
		childrenList.remove(scope);
		scope.parent = null;
	}

	public Scope getParent() {
		return parent;
	}

	public boolean isRoot() {
		return parent == null;
	}

	public boolean isStory() {
		return getRelease() != null;
	}

	public Scope getChild(final int index) {
		return childrenList.get(index);
	}

	public int getChildIndex(final Scope scope) {
		return childrenList.indexOf(scope);
	}

	public void clearChildren() {
		this.childrenList.clear();
	}

	/**
	 * @precondition The release should contain the scope
	 * @param release
	 */
	public void setRelease(final Release release) {
		if (release != null && !release.containsScope(this)) throw new IllegalArgumentException("The Release should contain the scope");
		this.release = release;
	}

	public Release getRelease() {
		return release;
	}

	public boolean hasRelease() {
		return release != null;
	}

	public Progress getProgress() {
		return progress;
	}

	public UserRepresentation getOwner() {
		return progress.getInitialStateAuthor();
	}

	public Scope findScope(final UUID scopeId) {
		if (this.id.equals(scopeId)) return this;

		for (final Scope childScope : childrenList) {
			final Scope scopeLoaded = childScope.findScope(scopeId);
			if (scopeLoaded != null) return scopeLoaded;
		}

		return null;
	}

	public boolean isLeaf() {
		return childrenList.size() == 0;
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
		return description;
	}

	public List<Scope> getAllDescendantScopes() {
		final ArrayList<Scope> l = new ArrayList<Scope>();
		for (final Scope scope : childrenList) {
			l.add(scope);
			l.addAll(scope.getAllDescendantScopes());
		}
		return l;
	}

	public List<Scope> getAllLeafs() {
		final ArrayList<Scope> l = new ArrayList<Scope>();
		if (this.isLeaf()) l.add(this);
		for (final Scope scope : childrenList) {
			l.addAll(scope.getAllLeafs());
		}
		return l;
	}

	public Scope getRootScope() {
		Scope current = this;
		while (!current.isRoot())
			current = current.getParent();
		return current;
	}

	public void setDueDate(final Date dueDate) {
		this.dueDate = dueDate;
	}

	public Date getDueDate() {
		return CalendarUtil.copyDate(dueDate);
	}

	public boolean hasDueDate() {
		return dueDate != null;
	}

	public List<Scope> getAllAncestors() {
		final List<Scope> ancestors = new ArrayList<Scope>();
		if (this.isRoot()) return ancestors;

		Scope current = this.getParent();
		while (!current.isRoot()) {
			ancestors.add(current);
			current = current.getParent();
		}
		return ancestors;
	}

	public Float getDeclaredEffort() {
		return effort.getDeclared();
	}

	public Float getDeclaredValue() {
		return value.getDeclared();
	}
}