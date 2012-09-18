package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import static br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.configPopup;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.components.releasepanel.events.ReleaseContainerStateChangeEvent;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.chart.ReleaseChart;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.chart.ReleaseChartDataProvider;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.HorizontalAlignment;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.VerticalAlignment;
import br.com.oncast.ontrack.client.ui.generalwidgets.CommandMenuItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.EditableLabel;
import br.com.oncast.ontrack.client.ui.generalwidgets.EditableLabelEditionHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetContainerListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.MouseCommandsMenu;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupCloseListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.TextAndImageCommandMenuItem;
import br.com.oncast.ontrack.client.ui.places.progress.ProgressPlace;
import br.com.oncast.ontrack.client.ui.settings.DefaultViewSettings;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.ReleaseEstimator;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

// TODO Refactor dividing visualization logic from business logic
public class ReleaseWidget extends Composite implements ModelWidget<Release> {

	private static final ReleaseWidgetMessages messages = GWT.create(ReleaseWidgetMessages.class);

	private static ReleasePanelItemWidgetUiBinder uiBinder = GWT.create(ReleasePanelItemWidgetUiBinder.class);

	interface ReleasePanelItemWidgetUiBinder extends UiBinder<Widget, ReleaseWidget> {}

	interface Style extends CssResource {
		String chartPanel();

		String headerClosed();
	}

	@UiField
	protected Resources resources;

	interface Resources extends ClientBundle {
		@Source("bg-expand-minus.png")
		ImageResource containerStateOpened();

		@Source("bg-expand-plus.png")
		ImageResource containerStateClosed();

		@Source("stats_0.png")
		ImageResource progress0();

		@Source("stats_1.png")
		ImageResource progress1();

		@Source("stats_2.png")
		ImageResource progress2();

		@Source("stats_3.png")
		ImageResource progress3();

		@Source("stats_4.png")
		ImageResource progress4();

		@Source("stats_5.png")
		ImageResource progress5();

		@Source("stats_6.png")
		ImageResource progress6();

		@Source("stats_7.png")
		ImageResource progress7();

		@Source("stats_8.png")
		ImageResource progress8();

		@Source("switch-kanban.png")
		ImageResource kanbanIcon();

		@Source("priority-expand.png")
		ImageResource menuIcon();

		@Source("bg-later.png")
		ImageResource laterImage();

		@Source("priority-decrease.png")
		ImageResource menuReleaseDecreasePriority();

		@Source("priority-increase.png")
		ImageResource menuReleaseIncreasePriority();

		@Source("priority-delete.png")
		ImageResource menuReleaseDelete();

		@Source("annotationIcon.png")
		ImageResource annotationIcon();
	}

	@UiField
	protected Style style;

	@UiField
	protected FocusPanel menuMouseOverArea;

	@UiField
	protected UIObject header;

	@UiField
	protected Image containerStateIcon;

	@UiField
	protected EditableLabel descriptionLabel;

	@UiField
	protected Image annotationIcon;

	@UiField
	protected Image progressIcon;

	@UiField
	protected Image kanbanLink;

	@UiField
	protected Image menuIcon;

	@UiField
	protected DivElement bodyContainer;

	@UiField
	protected ReleaseWidgetContainer releaseContainer;

	@UiField
	protected UIObject laterSeparator;

	@UiField
	protected ScopeWidgetContainer scopeContainer;

	@UiFactory
	protected ReleaseWidgetContainer createReleaseContainer() {
		return new ReleaseWidgetContainer(releaseWidgetFactory, containerUpdateListener);
	}

	@UiFactory
	protected ScopeWidgetContainer createScopeContainer() {
		return new ScopeWidgetContainer(scopeWidgetFactory, containerUpdateListener);
	}

	@UiFactory
	protected EditableLabel createEditableLabel() {
		return new EditableLabel(editionHandler);
	}

	private final EditableLabelEditionHandler editionHandler;

	private final ModelWidgetFactory<Release, ReleaseWidget> releaseWidgetFactory;

	private final ModelWidgetFactory<Scope, ScopeWidget> scopeWidgetFactory;

	private final ModelWidgetContainerListener containerUpdateListener;

	private boolean isContainerStateOpen = true;

	// IMPORTANT Used to refresh DOM only when needed.
	private String currentReleaseDescription;

	// IMPORTANT Used to refresh DOM only when needed.
	private float lastProgress;

	private final Release release;

	private final ReleasePanelWidgetInteractionHandler releasePanelInteractionHandler;

	private MouseCommandsMenu mouseCommandsMenu;

	private ReleaseChart chartPanel;

	private boolean isMenuOpen = false;

	public ReleaseWidget(final Release release, final ModelWidgetFactory<Release, ReleaseWidget> releaseWidgetFactory,
			final ModelWidgetFactory<Scope, ScopeWidget> scopeWidgetFactory,
			final ReleasePanelWidgetInteractionHandler releasePanelInteractionHandler) {
		this.release = release;

		this.releaseWidgetFactory = releaseWidgetFactory;
		this.scopeWidgetFactory = scopeWidgetFactory;
		this.releasePanelInteractionHandler = releasePanelInteractionHandler;
		this.editionHandler = new EditableLabelEditionHandler() {
			@Override
			public boolean onEditionRequest(final String newReleaseName) {
				releasePanelInteractionHandler.onReleaseRenameRequest(release, newReleaseName);
				return false;
			}
		};

		this.containerUpdateListener = createContainerUpdateListener();

		initWidget(uiBinder.createAndBindUi(this));
		setVisible(false);

		scopeContainer.setOwnerRelease(release);

		update();

		setContainerState(DefaultViewSettings.RELEASE_PANEL_CONTAINER_STATE, false);
		setVisible(true);
	}

	@UiHandler("menuMouseOverArea")
	protected void onMouseOver(final MouseOverEvent event) {
		menuIcon.setVisible(true);
	}

	@UiHandler("menuMouseOverArea")
	protected void onMouseOut(final MouseOutEvent event) {
		menuIcon.setVisible(isMenuOpen);
	}

	@UiHandler("menuIcon")
	protected void showMenu(final ClickEvent event) {
		configPopup().popup(getMouseActionMenu())
				.alignRight(menuIcon)
				.alignBelow(menuIcon)
				.onClose(new PopupCloseListener() {
					@Override
					public void onHasClosed() {
						menuIcon.setVisible(false);
						isMenuOpen = false;
					}
				})
				.pop();
		isMenuOpen = true;
	}

	@UiHandler("annotationIcon")
	protected void showAnnotationPanel(final ClickEvent event) {
		ClientServiceProvider.getInstance().getAnnotationService().showAnnotationsFor(release.getId());
	}

	@UiHandler("progressIcon")
	protected void showChartPanel(final ClickEvent event) {
		configPopup().popup(getChartPanel())
				.alignHorizontal(HorizontalAlignment.RIGHT, new AlignmentReference(progressIcon, HorizontalAlignment.CENTER))
				.alignVertical(VerticalAlignment.TOP, new AlignmentReference(progressIcon, VerticalAlignment.MIDDLE))
				.pop();
	}

	@UiHandler("kanbanLink")
	protected void onClick(final ClickEvent event) {
		final ClientServiceProvider provider = ClientServiceProvider.getInstance();
		final UUID projectId = provider.getProjectRepresentationProvider().getCurrent().getId();
		provider.getApplicationPlaceController().goTo(new ProgressPlace(projectId, release.getId()));
	}

	@UiHandler("containerStateIcon")
	protected void onContainerStateIconClicked(final ClickEvent event) {
		setContainerState(!isContainerStateOpen);
	}

	private MouseCommandsMenu getMouseActionMenu() {
		if (mouseCommandsMenu != null) return mouseCommandsMenu;

		final List<CommandMenuItem> itens = new ArrayList<CommandMenuItem>();
		itens.add(new TextAndImageCommandMenuItem(resources.menuReleaseIncreasePriority(), messages.increasePriority(), new Command() {

			@Override
			public void execute() {
				releasePanelInteractionHandler.onReleaseIncreasePriorityRequest(release);
			}
		}));
		itens.add(new TextAndImageCommandMenuItem(resources.menuReleaseDecreasePriority(), messages.decreasePriority(), new Command() {

			@Override
			public void execute() {
				releasePanelInteractionHandler.onReleaseDecreasePriorityRequest(release);
			}
		}));
		itens.add(new TextAndImageCommandMenuItem(resources.menuReleaseDelete(), messages.deleteRelease(), new Command() {

			@Override
			public void execute() {
				releasePanelInteractionHandler.onReleaseDeletionRequest(release);
			}
		}));
		mouseCommandsMenu = new MouseCommandsMenu(itens);
		return mouseCommandsMenu;
	}

	// TODO Is this necessary ?
	private ModelWidgetContainerListener createContainerUpdateListener() {
		return new ModelWidgetContainerListener() {
			@Override
			public void onUpdateComplete(final boolean hasChanged) {}
		};
	}

	private boolean updateScopeWidgets() {
		return scopeContainer.update(release.getScopeList());
	}

	private boolean updateChildReleaseWidgets() {
		kanbanLink.setVisible(release.hasDirectScopes());
		releaseContainer.setVisible(isContainerStateOpen && release.hasChildren());
		return releaseContainer.update(release.getChildren());
	}

	@Override
	public boolean update() {
		updateDescription();
		updateProgress();

		final boolean releaseUpdate = updateChildReleaseWidgets();
		final boolean scopeUpdate = updateScopeWidgets();

		laterSeparator.setVisible(release.hasDirectScopes() && release.hasChildren());

		return releaseUpdate || scopeUpdate;
	}

	private void updateDescription() {
		if (release.getDescription().equals(currentReleaseDescription)) return;
		currentReleaseDescription = release.getDescription();

		descriptionLabel.setValue(currentReleaseDescription);
	}

	private void updateProgress() {
		final float progress = getProgressPercentage();
		if (lastProgress == progress) return;
		lastProgress = progress;

		final float aux = 100F / 8F;
		if (progress == 0) progressIcon.setResource(resources.progress0());
		else if (progress <= 1 * aux) progressIcon.setResource(resources.progress1());
		else if (progress <= 2 * aux) progressIcon.setResource(resources.progress2());
		else if (progress <= 3 * aux) progressIcon.setResource(resources.progress3());
		else if (progress <= 4 * aux) progressIcon.setResource(resources.progress4());
		else if (progress <= 5 * aux) progressIcon.setResource(resources.progress5());
		else if (progress <= 6 * aux) progressIcon.setResource(resources.progress6());
		else if (progress < 100) progressIcon.setResource(resources.progress7());
		else progressIcon.setResource(resources.progress8());
	}

	private float getProgressPercentage() {
		final float effortSum = release.getEffortSum();
		if (effortSum == 0) return 0;

		final float concludedEffortSum = release.getAccomplishedEffortSum();
		final float percentage = 100 * concludedEffortSum / effortSum;
		return percentage;
	}

	public void setContainerState(final boolean shouldOpen) {
		setContainerState(shouldOpen, true);
	}

	public void setContainerState(final boolean shouldOpen, final boolean shouldFireEvent) {
		if (isContainerStateOpen == shouldOpen) return;

		containerStateIcon.setResource(shouldOpen ? resources.containerStateOpened() : resources.containerStateClosed());
		header.setStyleName(style.headerClosed(), !shouldOpen);

		final boolean shouldShowReleaseContainer = shouldOpen && release.hasChildren();

		scopeContainer.setVisible(shouldOpen);
		releaseContainer.setVisible(shouldShowReleaseContainer);
		laterSeparator.setVisible(shouldShowReleaseContainer && release.hasDirectScopes());

		isContainerStateOpen = shouldOpen;

		if (!shouldFireEvent) return;
		ClientServiceProvider.getInstance().getEventBus().fireEventFromSource(new ReleaseContainerStateChangeEvent(release, isContainerStateOpen), this);
	}

	private ReleaseChart getChartPanel() {
		if (chartPanel != null) return chartPanel;
		chartPanel = new ReleaseChart(new ReleaseChartDataProvider(release, getReleaseEstimator(), ClientServiceProvider.getInstance()
				.getActionExecutionService()));
		chartPanel.addStyleName(style.chartPanel());
		return chartPanel;
	}

	private ReleaseEstimator getReleaseEstimator() {
		// XXX Burnup: Locate the root Release in a more direct fashion.
		// TODO+++ [Performance] Use one instance per project.
		Release r = getRelease();
		while (r.getParent() != null)
			r = r.getParent();

		return new ReleaseEstimator(r);
	}

	public Release getRelease() {
		return release;
	}

	@Override
	public Release getModelObject() {
		return getRelease();
	}

	public ScopeWidgetContainer getScopeContainer() {
		return scopeContainer;
	}

	public ReleaseWidgetContainer getChildReleasesContainer() {
		return releaseContainer;
	}

	public void setHierarchicalContainerState(final boolean shouldOpen) {
		ReleaseWidget current = this;
		while (current != null && !current.release.isRoot()) {
			current.setContainerState(shouldOpen);
			current = getParentReleaseWidget(current);
		}
	}

	private ReleaseWidget getParentReleaseWidget(final ReleaseWidget widget) {
		Widget current = widget.getParent();
		while (current != null && !(current instanceof ReleaseWidget))
			current = current.getParent();
		return (ReleaseWidget) current;
	}
}
