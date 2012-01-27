package br.com.oncast.ontrack.shared.model.scope;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.shared.model.effort.Effort;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.model.value.Value;
import br.com.oncast.ontrack.utils.deepEquality.IgnoredByDeepEquality;

public class Scope implements Serializable {

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

	// IMPORTANT The default constructor is used by GWT and by Mind map converter to construct new scopes. Do not remove this.
	protected Scope() {}

	public Scope(final String description) {
		this(description, new UUID());
	}

	public Scope(final String description, final UUID scopeId) {
		this.id = scopeId;
		this.description = description;
		this.effort = new Effort();
		this.value = new Value();
		this.progress = new Progress();

		childrenList = new ArrayList<Scope>();
	}

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
	 * Returns a copy of the list of child scopes. If you want to add or remove a scope from this scope, use {@link Scope#add(Scope)} and
	 * {@link Scope#remove(Scope)}. Do NOT manipulate this list directly.
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

	public Scope getChild(final int index) {
		return childrenList.get(index);
	}

	public int getChildIndex(final Scope scope) {
		return childrenList.indexOf(scope);
	}

	public void clearChildren() {
		this.childrenList.clear();
	}

	// TODO ++Think about bidirectional linkage (if not present in the release, then set it)
	public void setRelease(final Release release) {
		this.release = release;
	}

	public Release getRelease() {
		return release;
	}

	public Progress getProgress() {
		return progress;
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
		return id.hashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (!(obj instanceof Scope)) return false;

		return this.id.equals(((Scope) obj).getId());
	}

	@Override
	public String toString() {
		return description;
	}
}