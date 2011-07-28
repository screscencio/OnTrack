package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

// TODO Refactor dividing visualization logic from business logic
public class ReleaseWidget extends Composite implements ModelWidget<Release> {

	private static ReleasePanelItemWidgetUiBinder uiBinder = GWT.create(ReleasePanelItemWidgetUiBinder.class);

	interface ReleasePanelItemWidgetUiBinder extends UiBinder<Widget, ReleaseWidget> {}

	interface Style extends CssResource {
		String invisibleBodyContainer();

		String headerContainerStateImageOpened();

		String headerContainerStateImageClosed();
	}

	private final Release release;

	// Do not refresh the DOM if these variables don't change.
	private String currentReleaseDescription;
	private String currentProgressPercentage;

	@UiField
	protected Style style;

	@UiField
	protected Label descriptionLabel;

	@UiField
	protected Label progressLabel;

	@UiField
	protected ReleaseWidgetContainer releaseContainer;

	@UiField
	protected ScopeWidgetContainer scopeContainer;

	private final ModelWidgetFactory<Release, ReleaseWidget> releaseWidgetFactory;

	private final ModelWidgetContainerListener containerUpdateListener;

	@UiField
	protected DivElement bodyContainer;

	@UiField
	protected Image containerStateImage;

	private HandlerRegistration containerStateImageClickHandlerRegistration;

	private boolean isContainerStateOpen;

	private boolean isBodyContainerActive;

	@UiFactory
	protected ReleaseWidgetContainer createReleaseContainer() {
		return new ReleaseWidgetContainer(releaseWidgetFactory, containerUpdateListener);
	}

	@UiFactory
	protected ScopeWidgetContainer createScopeContainer() {
		return new ScopeWidgetContainer(ScopeWidgetFactory.getInstance(), containerUpdateListener);
	}

	public ReleaseWidget(final Release release, final ModelWidgetFactory<Release, ReleaseWidget> releaseWidgetFactory) {
		this.releaseWidgetFactory = releaseWidgetFactory;
		this.release = release;
		this.containerUpdateListener = new ModelWidgetContainerListener() {

			@Override
			public void onUpdateComplete(final boolean hasChanged, final boolean hasNewWidgets) {
				if (!hasChanged) return;

				reviewContainersState();
				if (hasNewWidgets) setContainerState(true);
			}
		};

		initWidget(uiBinder.createAndBindUi(this));

		for (final Release childRelease : release.getChildReleases())
			releaseContainer.createChildModelWidget(childRelease);

		for (final Scope scope : release.getScopeList())
			scopeContainer.createChildModelWidget(scope);

		updateDescription();
		updateProgress();
		reviewContainersState();
	}

	@Override
	public void update() {
		updateDescription();
		updateProgress();
		releaseContainer.update(release.getChildReleases());
		scopeContainer.update(release.getScopeList());
	}

	private void updateDescription() {
		if (release.getDescription().equals(currentReleaseDescription)) return;
		currentReleaseDescription = release.getDescription();
		descriptionLabel.setText(currentReleaseDescription);
	}

	private void updateProgress() {
		final String progress = ClientDecimalFormat.roundFloat(release.getProgressPercentage(), 1);
		if (progress.equals(currentProgressPercentage)) return;

		currentProgressPercentage = progress;
		progressLabel.setText(currentProgressPercentage + "%");
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

	public void setContainerState(final boolean shouldOpen) {
		if (!isBodyContainerActive) return;
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

	public Release getRelease() {
		return release;
	}

	protected Style getStyle() {
		return style;
	}

	@Override
	public Release getModelObject() {
		return getRelease();
	}
}
