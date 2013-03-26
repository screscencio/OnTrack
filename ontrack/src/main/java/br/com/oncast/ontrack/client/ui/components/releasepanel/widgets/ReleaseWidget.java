package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import static br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.HorizontalAlignment.RIGHT;
import static br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.configPopup;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.details.DetailService;
import br.com.oncast.ontrack.client.ui.components.releasepanel.events.ReleaseContainerStateChangeEvent;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.chart.ReleaseChartPopup;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.HorizontalAlignment;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.VerticalAlignment;
import br.com.oncast.ontrack.client.ui.generalwidgets.CommandMenuItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.EditableLabel;
import br.com.oncast.ontrack.client.ui.generalwidgets.EditableLabelEditionHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.MouseCommandsMenu;
import br.com.oncast.ontrack.client.ui.generalwidgets.TextAndImageCommandMenuItem;
import br.com.oncast.ontrack.client.ui.places.progress.ProgressPlace;
import br.com.oncast.ontrack.client.ui.places.report.ReportPlace;
import br.com.oncast.ontrack.client.ui.settings.DefaultViewSettings;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
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
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;

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

		@Source("bg-later.png")
		ImageResource laterImage();

	}

	@UiField
	protected Style style;

	@UiField
	protected FocusPanel menuMouseOverArea;

	@UiField
	protected UIObject header;

	@UiField
	protected SimplePanel containerStateIcon;

	@UiField
	protected EditableLabel descriptionLabel;

	@UiField
	protected FocusPanel detailLink;

	@UiField
	protected FocusPanel progressIcon;

	@UiField
	protected SimplePanel progressBar;

	@UiField
	protected FocusPanel menuIcon;

	@UiField
	protected DivElement bodyContainer;

	@UiField
	protected ReleaseWidgetContainer releaseContainer;

	@UiField
	protected UIObject laterSeparator;

	@UiField
	protected ScopeWidgetContainer scopeContainer;

	@UiField(provided = true)
	protected ReleaseInfoWidget infoWidget;

	@UiFactory
	protected ReleaseWidgetContainer createReleaseContainer() {
		return new ReleaseWidgetContainer(releaseWidgetFactory);
	}

	@UiFactory
	protected ScopeWidgetContainer createScopeContainer() {
		return new ScopeWidgetContainer(scopeWidgetFactory);
	}

	@UiFactory
	protected EditableLabel createEditableLabel() {
		return new EditableLabel(editionHandler);
	}

	private final EditableLabelEditionHandler editionHandler;

	private final ModelWidgetFactory<Release, ReleaseWidget> releaseWidgetFactory;

	private final ModelWidgetFactory<Scope, ReleaseScopeWidget> scopeWidgetFactory;

	private boolean isContainerStateOpen = true;

	// IMPORTANT Used to refresh DOM only when needed.
	private String currentReleaseDescription;

	// IMPORTANT Used to refresh DOM only when needed.
	private double lastProgress;

	private final Release release;

	private final ReleasePanelWidgetInteractionHandler releasePanelInteractionHandler;

	private MouseCommandsMenu mouseCommandsMenu;

	private ReleaseChartPopup chartPanel;

	private boolean kanbanSpecific;

	public ReleaseWidget(final Release release, final ModelWidgetFactory<Release, ReleaseWidget> releaseWidgetFactory,
			final ModelWidgetFactory<Scope, ReleaseScopeWidget> scopeWidgetFactory,
			final ReleasePanelWidgetInteractionHandler releasePanelInteractionHandler) {
		this(release, releaseWidgetFactory, scopeWidgetFactory, releasePanelInteractionHandler, false);
	}

	public ReleaseWidget(final Release release, final ModelWidgetFactory<Release, ReleaseWidget> releaseWidgetFactory,
			final ModelWidgetFactory<Scope, ReleaseScopeWidget> scopeWidgetFactory,
			final ReleasePanelWidgetInteractionHandler releasePanelInteractionHandler, final boolean kanbanSpecific) {
		this.release = release;
		this.infoWidget = new ReleaseInfoWidget(release);
		this.releaseWidgetFactory = releaseWidgetFactory;
		this.scopeWidgetFactory = scopeWidgetFactory;
		this.releasePanelInteractionHandler = releasePanelInteractionHandler;
		this.kanbanSpecific = kanbanSpecific;
		this.editionHandler = new EditableLabelEditionHandler() {
			@Override
			public boolean onEditionRequest(final String newReleaseName) {
				releasePanelInteractionHandler.onReleaseRenameRequest(release, newReleaseName);
				return false;
			}

			@Override
			public void onEditionExit(final boolean canceledEdition) {}

			@Override
			public void onEditionStart() {}

		};

		initWidget(uiBinder.createAndBindUi(this));
		setupDetails();
		setVisible(false);

		scopeContainer.setOwnerRelease(release);

		update();

		setContainerState(DefaultViewSettings.RELEASE_PANEL_CONTAINER_STATE, false);
		setVisible(true);

		ClientServiceProvider.getInstance().getEventBus().addHandler(ReleaseScopeListUpdateEvent.TYPE, new ReleaseScopeListUpdateEventHandler() {

			@Override
			public void onScopeListInteraction(final Release r) {
				if (release.equals(r)) infoWidget.show();
				else infoWidget.hide();
			}
		});
	}

	private void setupDetails() {
		final DetailService annotationService = ClientServiceProvider.getInstance().getAnnotationService();
		setHasDetails(annotationService.hasDetails(release.getId()));
	}

	@UiHandler("detailLink")
	protected void showAnnotationPanel(final ClickEvent event) {
		ClientServiceProvider.getInstance().getAnnotationService().showAnnotationsFor(release.getId());
		event.stopPropagation();
	}

	@UiHandler("progressIcon")
	protected void showChartPanel(final ClickEvent event) {
		configPopup().popup(getChartPanel())
				.alignHorizontal(HorizontalAlignment.RIGHT, new AlignmentReference(progressIcon, HorizontalAlignment.CENTER))
				.alignVertical(VerticalAlignment.TOP, new AlignmentReference(progressIcon, VerticalAlignment.MIDDLE))
				.pop();
		event.stopPropagation();
	}

	@UiHandler("menuIcon")
	protected void onClick(final ClickEvent event) {
		configPopup().popup(getMouseActionMenu())
				.alignHorizontal(RIGHT, new AlignmentReference(menuIcon, RIGHT))
				.alignVertical(VerticalAlignment.TOP, new AlignmentReference(menuIcon, VerticalAlignment.BOTTOM, 0))
				.pop();
		event.stopPropagation();
	}

	@UiHandler("containerStateIcon")
	protected void onContainerStateIconClicked(final ClickEvent event) {
		setContainerState(!isContainerStateOpen);
		event.stopPropagation();
	}

	private MouseCommandsMenu getMouseActionMenu() {
		if (mouseCommandsMenu != null) return mouseCommandsMenu;

		final List<CommandMenuItem> itens = new ArrayList<CommandMenuItem>();
		if (!kanbanSpecific) {
			itens.add(new TextAndImageCommandMenuItem("icon-columns", messages.kanban(), new Command() {
				@Override
				public void execute() {
					final ClientServiceProvider provider = ClientServiceProvider.getInstance();
					final UUID projectId = provider.getProjectRepresentationProvider().getCurrent().getId();
					provider.getApplicationPlaceController().goTo(new ProgressPlace(projectId, release.getId()));
				}
			}));
		}
		itens.add(new TextAndImageCommandMenuItem("icon-file-alt", messages.report(), new Command() {
			@Override
			public void execute() {
				final UUID project = ClientServiceProvider.getCurrentProjectContext().getId();
				ClientServiceProvider.getInstance().getApplicationPlaceController().open(new ReportPlace(project, release.getId()));
			}
		}));
		if (release.hasDirectScopes()) itens.add(new TextAndImageCommandMenuItem("icon-time", messages.timesheet(), new Command() {
			@Override
			public void execute() {
				ClientServiceProvider.getInstance().getTimesheetService().showTimesheetFor(release.getId());
			}
		}));
		if (!kanbanSpecific) {
			itens.add(new TextAndImageCommandMenuItem("icon-arrow-up", messages.increasePriority(), new Command() {

				@Override
				public void execute() {
					releasePanelInteractionHandler.onReleaseIncreasePriorityRequest(release);
				}
			}));
			itens.add(new TextAndImageCommandMenuItem("icon-arrow-down", messages.decreasePriority(), new Command() {

				@Override
				public void execute() {
					releasePanelInteractionHandler.onReleaseDecreasePriorityRequest(release);
				}
			}));
			itens.add(new TextAndImageCommandMenuItem("icon-trash", messages.deleteRelease(), new Command() {

				@Override
				public void execute() {
					releasePanelInteractionHandler.onReleaseDeletionRequest(release);
				}
			}));
		}
		mouseCommandsMenu = new MouseCommandsMenu(itens);
		return mouseCommandsMenu;
	}

	private boolean updateScopeWidgets() {
		return scopeContainer.update(release.getScopeList());
	}

	private boolean updateChildReleaseWidgets() {
		menuIcon.setVisible(release.hasDirectScopes() && !kanbanSpecific);
		releaseContainer.setVisible(isContainerStateOpen && release.hasChildren());
		return releaseContainer.update(release.getChildren());
	}

	@Override
	public boolean update() {
		updateDescription();
		updateProgress();
		infoWidget.update();

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
		final double progress = getProgressPercentage();
		if (lastProgress == progress) return;

		progressBar.getElement().getStyle().setWidth(progress, Unit.PCT);
		lastProgress = progress;
	}

	private double getProgressPercentage() {
		final float effortSum = release.getEffortSum();
		if (effortSum == 0) return 0;

		final float concludedEffortSum = release.getAccomplishedEffortSum();
		final float percentage = 100 * concludedEffortSum / effortSum;
		return Math.ceil(percentage);
	}

	public void setContainerState(final boolean shouldOpen) {
		setContainerState(shouldOpen, true);
	}

	public void setContainerState(final boolean shouldOpen, final boolean shouldFireEvent) {
		if (isContainerStateOpen == shouldOpen) return;

		if (shouldOpen) {
			containerStateIcon.setStyleName("icon-caret-right", false);
			containerStateIcon.setStyleName("icon-caret-down", true);
		}
		else {
			containerStateIcon.setStyleName("icon-caret-down", false);
			containerStateIcon.setStyleName("icon-caret-right", true);
		}

		header.setStyleName(style.headerClosed(), !shouldOpen);

		final boolean shouldShowReleaseContainer = shouldOpen && release.hasChildren();

		scopeContainer.setVisible(shouldOpen);
		infoWidget.setVisible(shouldOpen);
		releaseContainer.setVisible(shouldShowReleaseContainer);
		laterSeparator.setVisible(shouldShowReleaseContainer && release.hasDirectScopes());

		isContainerStateOpen = shouldOpen;

		if (!shouldFireEvent) return;
		ClientServiceProvider.getInstance().getEventBus().fireEventFromSource(new ReleaseContainerStateChangeEvent(release, isContainerStateOpen), this);
	}

	private ReleaseChartPopup getChartPanel() {
		if (chartPanel != null) return chartPanel;
		chartPanel = new ReleaseChartPopup(release);
		chartPanel.addStyleName(style.chartPanel());
		return chartPanel;
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

	public void setHasDetails(final boolean hasDetails) {
		detailLink.setStyleName("icon-plus-sign", true);
	}
}
