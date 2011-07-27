package br.com.oncast.ontrack.shared.model.scope;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.shared.model.effort.Effort;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.deepEquality.IgnoredByDeepEquality;

import com.google.gwt.user.client.rpc.IsSerializable;

// TODO +++Test this class
public class Scope implements IsSerializable {

	@IgnoredByDeepEquality
	private UUID id;

	@IgnoredByDeepEquality
	private Scope parent;

	@IgnoredByDeepEquality
	private Release release;

	private String description;

	private List<Scope> childrenList;

	private Effort effort;

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

	public List<Scope> getChildren() {
		return childrenList;
	}

	public int getChildCount() {
		return childrenList.size();
	}

	public Effort getEffort() {
		return effort;
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

	public void setRelease(final Release release) {
		this.release = release;
	}

	public Release getRelease() {
		return release;
	}

	public Progress getProgress() {
		return progress;
	}

	public void setProgress(final Progress progress) {
		this.progress = progress;
	}

	// TODO ++Should this method throw an exception if nothing is found or should all 'users' of this method verify for null return?
	public Scope findScope(final UUID scopeId) {
		if (this.id.equals(scopeId)) return this;

		for (final Scope childScope : childrenList) {
			final Scope scopeLoaded = childScope.findScope(scopeId);
			if (scopeLoaded != null) return scopeLoaded;
		}

		return null;
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

	public boolean isLeaf() {
		return childrenList.size() == 0;
	}

}