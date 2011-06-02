package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.com.oncast.ontrack.shared.release.Release;
import br.com.oncast.ontrack.shared.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

// TODO Refactor dividing visualization logic from business logic
public class ReleasePanelItemWidget extends Composite {

	private static ReleasePanelItemWidgetUiBinder uiBinder = GWT.create(ReleasePanelItemWidgetUiBinder.class);

	interface ReleasePanelItemWidgetUiBinder extends UiBinder<Widget, ReleasePanelItemWidget> {}

	public interface Style extends CssResource {
		String invisibleBodyContainer();

		String headerContainerStateImageOpened();

		String headerContainerStateImageClosed();
	}

	private final List<ReleasePanelItemWidget> childs;

	private final List<ScopeWidget> scopesList;

	private final Release release;

	@UiField
	protected Style style;

	@UiField
	protected Label header;

	@UiField
	protected VerticalPanel releaseContainer;

	@UiField
	protected VerticalPanel scopeContainer;

	@UiField
	protected DivElement bodyContainer;

	@UiField
	protected Image containerStateImage;

	private HandlerRegistration containerStateImageClickHandlerRegistration;

	private boolean isContainerStateOpen;

	private boolean isBodyContainerActive;

	private final ReleaseWidgetFactory releaseWidgetFactory;

	public ReleasePanelItemWidget(final Release release, final ReleaseWidgetFactory releaseWidgetFactory) {
		this.releaseWidgetFactory = releaseWidgetFactory;
		initWidget(uiBinder.createAndBindUi(this));
		this.scopesList = new ArrayList<ScopeWidget>();
		this.childs = new ArrayList<ReleasePanelItemWidget>();
		this.release = release;

		header.setText(release.getDescription());

		for (final Release childRelease : release.getChildReleases())
			createChildItem(childRelease);

		for (final Scope scope : release.getScopeList())
			createScopeItem(scope);

		reviewContainersVisibility();
	}

	public Release getRelease() {
		return release;
	}

	public String getHeader() {
		return header.getText();
	}

	protected Style getStyle() {
		return style;
	}

	// TODO Review this method's algorithm
	protected void updateChildReleases(final List<Release> childReleases) {
		for (final Release childRelease : childReleases) {
			final ReleasePanelItemWidget releaseWithDescription = getReleaseWithDescription(childRelease.getDescription());
			if (releaseWithDescription != null) {
				if (!childRelease.getChildReleases().isEmpty()) releaseWithDescription.updateChildReleases(childRelease.getChildReleases());
				releaseWithDescription.updateChildScopes(childRelease.getScopeList());
			}
			else createChildItem(childRelease);
		}

		reviewContainersVisibility();
	}

	// TODO Review this method's algorithm
	protected void updateChildScopes(final List<Scope> scopes) {

		final Iterator<ScopeWidget> iterator = scopesList.iterator();
		while (iterator.hasNext()) {
			final ScopeWidget scopeWidget = iterator.next();
			if (!containsWidgetInScopeList(scopes, scopeWidget)) {
				iterator.remove();
				scopeContainer.remove(scopeWidget);
			}
		}

		for (final Scope scope : scopes)
			if (!containsScopeInWidgetList(scope)) createScopeItem(scope);

		reviewContainersVisibility();
	}

	// TODO Refactor this so that the label text is not used for this comparasion, instead an instance shoudl be used itself
	private boolean containsWidgetInScopeList(final List<Scope> scopes, final ScopeWidget scopeWidget) {
		for (final Scope scope : scopes)
			if (scope.equals(scopeWidget.getScope())) return true;

		return false;
	}

	// TODO Refactor this so that the label text is not used for this comparasion, instead an instance shoudl be used itself
	private boolean containsScopeInWidgetList(final Scope scope) {
		for (final ScopeWidget scopeWidget : scopesList) {
			if (scopeWidget.getScope().equals(scope)) return true;
		}
		return false;
	}

	// TODO Refactor this so that the label text is not used for this comparasion, instead an instance shoudl be used itself
	private ReleasePanelItemWidget getReleaseWithDescription(final String description) {
		for (final ReleasePanelItemWidget childItem : getChilds())
			if (childItem.getHeader().equals(description)) return childItem;

		return null;
	}

	// TODO Refactor this so that an expecialist object is created instead of an label
	private void createScopeItem(final Scope scope) {
		final ScopeWidget scopeWidget = new ScopeWidget(scope);
		scopeContainer.add(scopeWidget);
		scopesList.add(scopeWidget);
		setContainerState(true);
	}

	private void createChildItem(final Release childRelease) {
		final ReleasePanelItemWidget child = releaseWidgetFactory.createReleaseWidget(childRelease);
		releaseContainer.add(child);
		getChilds().add(child);
		setContainerState(true);
	}

	private void reviewContainersVisibility() {
		reviewBodyContainerState();
		reviewReleaseContainerVisibility();
		reviewScopeContainerVisibility();
	}

	private void reviewBodyContainerState() {
		final boolean shouldBodyContainerBeActive = (scopeContainer.getWidgetCount() != 0 || releaseContainer.getWidgetCount() != 0);
		if (isBodyContainerActive == shouldBodyContainerBeActive) return;

		if (shouldBodyContainerBeActive) {
			containerStateImageClickHandlerRegistration = containerStateImage.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(final ClickEvent event) {
					toogleContainerState();
				}
			});
			setContainerState(true);
		}
		else {
			containerStateImage.getElement().removeClassName(getStyle().headerContainerStateImageClosed());
			containerStateImage.getElement().removeClassName(getStyle().headerContainerStateImageOpened());
			if (containerStateImageClickHandlerRegistration != null) containerStateImageClickHandlerRegistration.removeHandler();
		}

		isBodyContainerActive = shouldBodyContainerBeActive;
	}

	private void reviewScopeContainerVisibility() {
		scopeContainer.setVisible(scopeContainer.getWidgetCount() != 0);
	}

	private void reviewReleaseContainerVisibility() {
		releaseContainer.setVisible(releaseContainer.getWidgetCount() != 0);
	}

	protected void toogleContainerState() {
		setContainerState(!isContainerStateOpen);
	}

	private void setContainerState(final boolean shouldOpen) {
		if (isContainerStateOpen == shouldOpen) return;

		if (shouldOpen) {
			bodyContainer.removeClassName(getStyle().invisibleBodyContainer());
			containerStateImage.getElement().removeClassName(getStyle().headerContainerStateImageClosed());
			containerStateImage.getElement().addClassName(getStyle().headerContainerStateImageOpened());
		}
		else {
			bodyContainer.addClassName(getStyle().invisibleBodyContainer());
			containerStateImage.getElement().removeClassName(getStyle().headerContainerStateImageOpened());
			containerStateImage.getElement().addClassName(getStyle().headerContainerStateImageClosed());
		}
		isContainerStateOpen = shouldOpen;
	}

	protected List<ReleasePanelItemWidget> getChilds() {
		return childs;
	}

	// TODO Review equals for Scope and Release after they have a persistence strategy. Are they using id? Are they verifying their child?
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof ReleasePanelItemWidget)) return false;
		final ReleasePanelItemWidget other = (ReleasePanelItemWidget) obj;

		if (!release.equals(other.getRelease())) return false;

		if (childs.size() != other.getChilds().size()) return false;
		if (!childs.equals(other.getChilds())) return false;

		return scopesList.equals(other.getScopeList());
	}

	public List<ScopeWidget> getScopeList() {
		return scopesList;
	}
}
