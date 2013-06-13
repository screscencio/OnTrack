package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import static br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.HorizontalAlignment.RIGHT;
import static br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.configPopup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.services.details.DetailService;
import br.com.oncast.ontrack.client.ui.components.releasepanel.events.ReleaseContainerStateChangeEvent;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.chart.ReleaseChartPopup;
import br.com.oncast.ontrack.client.ui.components.scope.ScopeCardWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.HorizontalAlignment;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.VerticalAlignment;
import br.com.oncast.ontrack.client.ui.generalwidgets.CommandMenuItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.EditableLabel;
import br.com.oncast.ontrack.client.ui.generalwidgets.EditableLabelEditionHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.MouseCommandsMenu;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig;
import br.com.oncast.ontrack.client.ui.generalwidgets.TextAndImageCommandMenuItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.impediment.ImpedimentListWidget;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningPlace;
import br.com.oncast.ontrack.client.ui.places.progress.ProgressPlace;
import br.com.oncast.ontrack.client.ui.places.report.ReportPlace;
import br.com.oncast.ontrack.client.ui.settings.DefaultViewSettings;
import br.com.oncast.ontrack.client.utils.date.HumanDateFormatter;
import br.com.oncast.ontrack.client.utils.date.TimeDifferenceFormat;
import br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat;
import br.com.oncast.ontrack.client.utils.ui.ElementUtils;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseDeclareEndDayAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseDeclareEstimatedVelocityAction;
import br.com.oncast.ontrack.shared.model.action.ReleaseDeclareStartDayAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecutionContext;
import br.com.oncast.ontrack.shared.utils.WorkingDay;
import br.com.oncast.ontrack.shared.utils.WorkingDayFactory;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.Place;
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
import com.google.gwt.user.client.ui.HTMLPanel;
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

		String impeded();
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
	protected FocusPanel progressIcon;

	@UiField
	protected HTMLPanel progressBar;

	@UiField
	SpanElement progressLabelUncomplete;

	@UiField
	SpanElement progressLabelComplete;

	@UiField
	protected FocusPanel navigationIcon;

	@UiField
	protected FocusPanel menuIcon;

	@UiField
	DivElement detailsContainer;

	@UiField
	SpanElement valueLabel;

	@UiField
	SpanElement effortLabel;

	@UiField
	DivElement periodContainer;

	@UiField
	SpanElement periodLabel;

	@UiField
	SpanElement durationLabel;

	@UiField
	FocusPanel hasDetailsIndicatorContainer;

	@UiField(provided = true)
	EditableLabel speedLabel;

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

	private final ModelWidgetFactory<Scope, ScopeCardWidget> scopeWidgetFactory;

	private boolean isContainerStateOpen = true;

	// IMPORTANT Used to refresh DOM only when needed.
	private String currentReleaseDescription;

	// IMPORTANT Used to refresh DOM only when needed.
	private float lastProgress;

	private final Release release;

	private final ReleasePanelWidgetInteractionHandler releasePanelInteractionHandler;

	private MouseCommandsMenu mouseCommandsMenu;

	private ReleaseChartPopup chartPanel;

	private boolean kanbanSpecific;

	private ActionExecutionListener actionExecutionListener;

	public ReleaseWidget(final Release release, final ModelWidgetFactory<Release, ReleaseWidget> releaseWidgetFactory,
			final ModelWidgetFactory<Scope, ScopeCardWidget> scopeWidgetFactory,
			final ReleasePanelWidgetInteractionHandler releasePanelInteractionHandler) {
		this(release, releaseWidgetFactory, scopeWidgetFactory, releasePanelInteractionHandler, false);
	}

	public ReleaseWidget(final Release release, final ModelWidgetFactory<Release, ReleaseWidget> releaseWidgetFactory,
			final ModelWidgetFactory<Scope, ScopeCardWidget> scopeWidgetFactory,
			final ReleasePanelWidgetInteractionHandler releasePanelInteractionHandler, final boolean kanbanSpecific) {
		this.release = release;
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

		speedLabel = new EditableLabel(new EditableLabelEditionHandler() {
			@Override
			public void onEditionStart() {}

			@Override
			public boolean onEditionRequest(final String text) {
				try {
					final float speed = Float.valueOf(text.replace(',', '.'));
					ClientServices.get().actionExecution().onUserActionExecutionRequest(new ReleaseDeclareEstimatedVelocityAction(release.getId(), speed));
					return true;
				}
				catch (final Exception e) {
					ClientServices.get().alerting().showWarning(messages.shouldBeAValidNumber());
					return false;
				}
			}

			@Override
			public void onEditionExit(final boolean canceledEdition) {}
		});

		initWidget(uiBinder.createAndBindUi(this));
		setVisible(false);

		scopeContainer.setOwnerRelease(release);

		update();

		setupNavigationIcon(release, kanbanSpecific);
		setContainerState(DefaultViewSettings.RELEASE_PANEL_CONTAINER_STATE, false);
		setVisible(true);
	}

	@Override
	protected void onLoad() {
		ClientServices.get().actionExecution().addActionExecutionListener(getActionExecutionListener());
	}

	@Override
	protected void onUnload() {
		if (actionExecutionListener == null) return;
		ClientServices.get().actionExecution().removeActionExecutionListener(actionExecutionListener);
	}

	private void setupNavigationIcon(final Release release, final boolean kanbanSpecific) {
		if (kanbanSpecific) {
			navigationIcon.addStyleName("icon-sitemap");
			navigationIcon.setTitle(messages.goToPlanning());
			navigationIcon.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(final ClickEvent event) {
					goToPlanning();
				}
			});
		}
		else if (release.isLeaf()) {
			navigationIcon.addStyleName("icon-columns");
			navigationIcon.setTitle(messages.goToKanban());
			navigationIcon.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(final ClickEvent event) {
					goToProgress();
				}
			});
		}
		else navigationIcon.removeFromParent();
	}

	private void popImpediments(final UIObject alignmentWidget) {
		PopupConfig.configPopup().popup(new ImpedimentListWidget(release))
				.alignHorizontal(HorizontalAlignment.RIGHT, new AlignmentReference(alignmentWidget, HorizontalAlignment.RIGHT))
				.alignVertical(VerticalAlignment.TOP, new AlignmentReference(alignmentWidget, VerticalAlignment.BOTTOM))
				.pop();
	}

	@UiHandler("progressIcon")
	protected void showChartPanel(final ClickEvent event) {
		popChartPanel();
		event.stopPropagation();
	}

	@UiHandler("hasDetailsIndicatorContainer")
	protected void showDetails(final ClickEvent event) {
		ClientServices.get().details().showDetailsFor(release.getId());
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
		itens.add(new TextAndImageCommandMenuItem("icon-info-sign", messages.details(), new Command() {
			@Override
			public void execute() {
				getDetailsService().showDetailsFor(release.getId());
			}
		}));
		itens.add(new TextAndImageCommandMenuItem("icon-bar-chart", messages.burnUp(), new Command() {
			@Override
			public void execute() {
				popChartPanel();
			}
		}));
		itens.add(new TextAndImageCommandMenuItem("icon-file-alt", messages.report(), new Command() {
			@Override
			public void execute() {
				final UUID project = ClientServices.getCurrentProjectContext().getId();
				ClientServices.get().placeController().open(new ReportPlace(project, release.getId()));
			}
		}));
		if (release.hasDirectScopes()) {
			itens.add(new TextAndImageCommandMenuItem("icon-time", messages.timesheet(), new Command() {
				@Override
				public void execute() {
					ClientServices.get().getTimesheetService().showTimesheetFor(release.getId());
				}
			}));
		}
		if (!kanbanSpecific) {
			itens.add(new SpacerCommandMenuItem());
			itens.add(new TextAndImageCommandMenuItem("icon-flag", messages.impediments(), new Command() {
				@Override
				public void execute() {
					popImpediments(header);
				}
			}));
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
		releaseContainer.setVisible(isContainerStateOpen && release.hasChildren());
		return releaseContainer.update(release.getChildren());
	}

	@Override
	public boolean update() {
		updateDescription();
		updateProgress();

		updateDetails();

		final boolean releaseUpdate = updateChildReleaseWidgets();
		final boolean scopeUpdate = updateScopeWidgets();

		laterSeparator.setVisible(isContainerStateOpen && release.hasDirectScopes() && release.hasChildren());

		ElementUtils.setVisible(detailsContainer, isContainerStateOpen && release.isLeaf());

		return releaseUpdate || scopeUpdate;
	}

	private void updateDetails() {
		updatePriorizationCriteria(effortLabel, "#", release.getEffortSum(), release.getAccomplishedEffortSum());
		updatePriorizationCriteria(valueLabel, "$", release.getValueSum(), release.getAccomplishedValueSum());

		ElementUtils.setVisible(periodContainer, release.getStartDay() != null);
		periodLabel.setInnerText(format(release.getStartDay()) + " - " + format(ClientServices.get().releaseEstimator().get().getEstimatedEndDayFor(release)));

		updateDuration();

		header.setStyleName(style.impeded(), getDetailsService().hasOpenImpediment(release.getId()));
		hasDetailsIndicatorContainer.setVisible(getDetailsService().hasDetails(release.getId()));
	}

	private void updatePriorizationCriteria(final SpanElement label, final String symbol, final float total, final float accomplished) {
		ElementUtils.setVisible(label, total > 0);
		label.setInnerText(symbol + " " + format(accomplished, total));
	}

	private void updateDuration() {
		final float effortSum = release.getEffortSum();
		final float estimatedVelocity = ClientServices.get().releaseEstimator().get().getEstimatedSpeed(release);

		speedLabel.setValue(round(estimatedVelocity, 1));

		ElementUtils.setVisible(durationLabel, effortSum >= 1);
		if (effortSum < 1) return;

		final int days = (int) Math.ceil(effortSum / estimatedVelocity);
		final Date date = new Date();
		final WorkingDay workingDay = WorkingDayFactory.create();
		workingDay.add(days);
		final TimeDifferenceFormat format = HumanDateFormatter.get().setDecimalDigits(1).getTimeDifferenceFormat(date, workingDay.getJavaDate());
		durationLabel.setInnerText(format.toString());
	}

	private void updateDescription() {
		if (release.getDescription().equals(currentReleaseDescription)) return;
		currentReleaseDescription = release.getDescription();

		descriptionLabel.setValue(currentReleaseDescription);
	}

	private void updateProgress() {
		final float progress = getProgressPercentage();
		if (lastProgress == progress) return;

		progressBar.getElement().getStyle().setWidth(progress, Unit.PCT);
		final String progressText = (progress < 1 ? "" : round(progress)) + "%";
		progressIcon.setVisible(progress > 0);
		progressLabelUncomplete.setInnerText(progressText);
		progressLabelComplete.setInnerText(progressText);
		lastProgress = progress;
	}

	private String format(final WorkingDay startDay) {
		if (startDay == null) return "";
		return startDay.getDayAndMonthString();
	}

	private String format(final float accomplished, final float total) {
		if (accomplished < 1) return round(total, 1);
		return round(accomplished) + "/" + round(total);
	}

	private String round(final float number) {
		return round(number, 0);
	}

	private String round(final float number, final int decimalDigits) {
		return ClientDecimalFormat.roundAndRemoveUnnecessaryRightZeros(number, decimalDigits);
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

		containerStateIcon.setStyleName("icon-caret-right", !shouldOpen);
		containerStateIcon.setStyleName("icon-caret-down", shouldOpen);

		header.setStyleName(style.headerClosed(), !shouldOpen);

		final boolean shouldShowReleaseContainer = shouldOpen && release.hasChildren();

		scopeContainer.setVisible(shouldOpen);
		ElementUtils.setVisible(detailsContainer, shouldOpen);
		releaseContainer.setVisible(shouldShowReleaseContainer);
		laterSeparator.setVisible(shouldShowReleaseContainer && release.hasDirectScopes());

		isContainerStateOpen = shouldOpen;

		if (!shouldFireEvent) return;
		ClientServices.get().eventBus().fireEventFromSource(new ReleaseContainerStateChangeEvent(release, isContainerStateOpen), this);
	}

	private void popChartPanel() {
		configPopup().popup(getChartPanel())
				.alignHorizontal(HorizontalAlignment.RIGHT, new AlignmentReference(header, HorizontalAlignment.RIGHT))
				.alignVertical(VerticalAlignment.TOP, new AlignmentReference(header, VerticalAlignment.BOTTOM))
				.pop();
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

	private void goToPlanning() {
		goTo(new PlanningPlace(getProjectId()));
	}

	private void goToProgress() {
		goTo(new ProgressPlace(getProjectId(), release.getId()));
	}

	private void goTo(final Place place) {
		ClientServices.get().placeController().goTo(place);
	}

	private UUID getProjectId() {
		return ClientServices.get().projectRepresentationProvider().getCurrent().getId();
	}

	private DetailService getDetailsService() {
		return ClientServices.get().details();
	}

	private ActionExecutionListener getActionExecutionListener() {
		if (actionExecutionListener == null) actionExecutionListener = new ActionExecutionListener() {
			@Override
			public void onActionExecution(final ModelAction action, final ProjectContext context, final ActionContext actionContext,
					final ActionExecutionContext executionContext,
					final boolean isUserAction) {
				if (action instanceof ReleaseDeclareStartDayAction || action instanceof ReleaseDeclareEndDayAction
						&& action.getReferenceId().equals(release.getId())) update();
			}
		};
		return actionExecutionListener;
	}

}
