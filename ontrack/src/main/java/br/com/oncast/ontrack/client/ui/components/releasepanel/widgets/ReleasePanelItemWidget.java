package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.com.oncast.ontrack.shared.release.Release;
import br.com.oncast.ontrack.shared.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class ReleasePanelItemWidget extends Composite {

	private static ReleasePanelItemWidgetUiBinder uiBinder = GWT.create(ReleasePanelItemWidgetUiBinder.class);

	interface ReleasePanelItemWidgetUiBinder extends UiBinder<Widget, ReleasePanelItemWidget> {}

	@UiField
	protected Label header;

	@UiField
	protected VerticalPanel releaseContainer;

	@UiField
	protected VerticalPanel scopeContainer;

	private final List<ReleasePanelItemWidget> childs;

	private final List<Label> scopesList;

	private final Release release;

	public ReleasePanelItemWidget(final Release release) {
		initWidget(uiBinder.createAndBindUi(this));
		this.scopesList = new ArrayList<Label>();
		this.childs = new ArrayList<ReleasePanelItemWidget>();
		this.release = release;

		header.setText(release.getDescription());
		for (final Release childRelease : release.getChildReleases()) {
			createChildItem(childRelease);
		}
		for (final Scope scope : release.getScopeList()) {
			createScopeItem(scope);
		}
		reviewContainersVisibility();
	}

	public Release getRelease() {
		return release;
	}

	public String getHeader() {
		return header.getText();
	}

	public void updateChildReleases(final List<Release> childReleases) {
		for (final Release childRelease : childReleases) {
			final ReleasePanelItemWidget releaseWithDescription = getReleaseWithDescription(childRelease.getDescription());
			if (releaseWithDescription != null) {
				if (!childRelease.getChildReleases().isEmpty()) releaseWithDescription.updateChildReleases(childRelease.getChildReleases());
				releaseWithDescription.updateChildScopes(childRelease.getScopeList());
			}
			else createChildItem(childRelease);
		}
		reviewReleaseContainerVisibility();
	}

	public void updateChildScopes(final List<Scope> scopes) {

		final Iterator<Label> iterator = scopesList.iterator();
		while (iterator.hasNext()) {
			final Label scopeLabel = iterator.next();
			if (!containsWidgetInScopeList(scopes, scopeLabel)) {
				iterator.remove();
				scopeContainer.remove(scopeLabel);
			}
		}

		for (final Scope scope : scopes) {
			if (!containsScopeInWidgetList(scope.getDescription())) createScopeItem(scope);
		}

		reviewScopeContainerVisibility();
	}

	private boolean containsWidgetInScopeList(final List<Scope> scopes, final Label description) {
		for (final Scope scope : scopes) {
			if (scope.getDescription().equals(description.getText())) return true;
		}
		return false;
	}

	private boolean containsScopeInWidgetList(final String description) {
		for (final Label scopeLabel : scopesList) {
			if (scopeLabel.getText().equals(description)) return true;
		}
		return false;
	}

	private void createScopeItem(final Scope scope) {
		final Label labelScope = new Label(scope.getDescription());
		scopeContainer.add(labelScope);
		scopesList.add(labelScope);
	}

	private void createChildItem(final Release childRelease) {
		final ReleasePanelItemWidget child = new ReleasePanelItemWidget(childRelease);
		releaseContainer.add(child);
		childs.add(child);
	}

	private ReleasePanelItemWidget getReleaseWithDescription(final String description) {
		for (final ReleasePanelItemWidget childItem : childs) {
			if (childItem.getHeader().equals(description)) return childItem;
		}
		return null;
	}

	private void reviewContainersVisibility() {
		reviewReleaseContainerVisibility();
		reviewScopeContainerVisibility();
	}

	private void reviewScopeContainerVisibility() {
		if (scopeContainer.getWidgetCount() == 0) scopeContainer.setVisible(false);
		else scopeContainer.setVisible(true);
	}

	private void reviewReleaseContainerVisibility() {
		if (releaseContainer.getWidgetCount() == 0) releaseContainer.setVisible(false);
		else releaseContainer.setVisible(true);
	}
}
