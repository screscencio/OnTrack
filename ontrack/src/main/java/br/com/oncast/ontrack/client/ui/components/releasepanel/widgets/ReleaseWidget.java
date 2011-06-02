package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import java.util.LinkedHashMap;
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
public class ReleaseWidget extends Composite {

	private static ReleasePanelItemWidgetUiBinder uiBinder = GWT.create(ReleasePanelItemWidgetUiBinder.class);

	interface ReleasePanelItemWidgetUiBinder extends UiBinder<Widget, ReleaseWidget> {}

	interface Style extends CssResource {
		String invisibleBodyContainer();

		String headerContainerStateImageOpened();

		String headerContainerStateImageClosed();
	}

	private final LinkedHashMap<Release, ReleaseWidget> releaseWidgetsMap;

	private final LinkedHashMap<Scope, ScopeWidget> scopeWidgetsMap;

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

	public ReleaseWidget(final Release release, final ReleaseWidgetFactory releaseWidgetFactory) {
		this.releaseWidgetFactory = releaseWidgetFactory;
		initWidget(uiBinder.createAndBindUi(this));
		this.scopeWidgetsMap = new LinkedHashMap<Scope, ScopeWidget>();
		this.releaseWidgetsMap = new LinkedHashMap<Release, ReleaseWidget>();
		this.release = release;

		header.setText(release.getDescription());

		for (final Release childRelease : release.getChildReleases())
			createChildReleaseWidget(childRelease);

		for (final Scope scope : release.getScopeList())
			createChildScopeWidget(scope);

		reviewContainersState();
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

	protected void update() {
		updateChildReleases();
		updateChildScopes();
		reviewContainersState();
	}

	private void updateChildReleases() {
		final List<Release> releases = release.getChildReleases();
		for (int i = 0; i < releases.size(); i++) {
			final Release release = releases.get(i);

			final ReleaseWidget releaseWidget = releaseWidgetsMap.get(release);
			if (releaseWidget == null) {
				createChildReleaseWidgetAt(release, i);
				continue;
			}

			if (releaseContainer.getWidgetIndex(releaseWidget) != i) {
				releaseContainer.remove(releaseWidget);
				releaseContainer.insert(releaseWidget, i);
			}

			releaseWidget.update();
		}
	}

	private void updateChildScopes() {
		final List<Scope> scopeList = release.getScopeList();
		for (int i = 0; i < scopeList.size(); i++) {
			final Scope scope = scopeList.get(i);

			final ScopeWidget scopeWidget = scopeWidgetsMap.get(scope);
			if (scopeWidget == null) {
				createChildScopeWidgetAt(scope, i);
				continue;
			}

			if (scopeContainer.getWidgetIndex(scopeWidget) != i) {
				scopeContainer.remove(scopeWidget);
				scopeContainer.insert(scopeWidget, i);
			}

			scopeWidget.update();
		}
		for (int i = scopeList.size(); i < scopeContainer.getWidgetCount(); i++) {
			final ScopeWidget scopeWidget = (ScopeWidget) scopeContainer.getWidget(i);
			scopeContainer.remove(i);
			scopeWidgetsMap.remove(scopeWidget.getScope());
		}
	}

	private ReleaseWidget createChildReleaseWidget(final Release release) {
		return createChildReleaseWidgetAt(release, releaseContainer.getWidgetCount());
	}

	private ReleaseWidget createChildReleaseWidgetAt(final Release release, final int index) {
		final ReleaseWidget childItem = releaseWidgetFactory.createReleaseWidget(release);
		releaseContainer.insert(childItem, index);
		releaseWidgetsMap.put(release, childItem);
		setContainerState(true);
		return childItem;
	}

	private void createChildScopeWidget(final Scope scope) {
		createChildScopeWidgetAt(scope, scopeContainer.getWidgetCount());
	}

	private ScopeWidget createChildScopeWidgetAt(final Scope scope, final int index) {
		final ScopeWidget scopeWidget = new ScopeWidget(scope);
		scopeContainer.insert(scopeWidget, index);
		scopeWidgetsMap.put(scope, scopeWidget);
		setContainerState(true);
		return scopeWidget;
	}

	private void reviewContainersState() {
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
					setContainerState(!isContainerStateOpen);
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

	private void reviewScopeContainerVisibility() {
		scopeContainer.setVisible(scopeContainer.getWidgetCount() != 0);
	}

	private void reviewReleaseContainerVisibility() {
		releaseContainer.setVisible(releaseContainer.getWidgetCount() != 0);
	}

	// TODO Review equals for Scope and Release after they have a persistence strategy. Are they using id? Are they verifying their child?
	@Override
	public boolean equals(final Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof ReleaseWidget)) return false;
		final ReleaseWidget other = (ReleaseWidget) obj;

		if (!release.equals(other.getRelease())) return false;
		if (!releaseWidgetsMap.equals(other.releaseWidgetsMap)) return false;

		return scopeWidgetsMap.equals(other.scopeWidgetsMap);
	}
}
