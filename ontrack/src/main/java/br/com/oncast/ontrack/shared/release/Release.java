package br.com.oncast.ontrack.shared.release;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.shared.scope.Scope;

// TODO Standardize this class methods with other tree structures like Scope. (eg.: getReleases -> getChildren)
public class Release {

	private static final String SEPARATOR = "/";

	private String description;
	private Release parent;
	private List<Scope> scopes;
	private List<Release> releases;

	public Release() {
		scopes = new ArrayList<Scope>();
		releases = new ArrayList<Release>();
	}

	public Release(final String description) {
		this.description = description;
	}

	public boolean isRoot() {
		return parent == null;
	}

	public String getDescription() {
		return description;
	}

	public String getFullDescription() {
		if (isRoot()) return description;
		return parent.getFullDescription() + SEPARATOR + description;
	}

	public void setDescription(final String description) {
		this.description = description;
	}

	public List<Scope> getScopes() {
		return scopes;
	}

	public void setScopes(final List<Scope> scopes) {
		this.scopes = scopes;
	}

	public List<Release> getReleases() {
		return releases;
	}

	public void setReleases(final List<Release> releases) {
		this.releases = releases;
	}

	public Release getParent() {
		return parent;
	}

	public void setParent(final Release parent) {
		this.parent = parent;
	}

	public void addScope(final Scope scope) {
		scopes.add(scope);
	}
}
