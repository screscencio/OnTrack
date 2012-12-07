package br.com.oncast.ontrack.client.ui.components.organization.widgets;

import java.util.HashSet;
import java.util.Set;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.ReleaseDetailWidget;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.chart.ReleaseChart;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.chart.ReleaseChartDataProvider;
import br.com.oncast.ontrack.client.ui.events.ReleaseSelectionEvent;
import br.com.oncast.ontrack.client.ui.events.ReleaseSelectionEventHandler;
import br.com.oncast.ontrack.client.ui.events.ScopeSelectionEvent;
import br.com.oncast.ontrack.client.ui.events.ScopeSelectionEventHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.AnimatedContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningPlace;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.ReleaseEstimator;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.UIObject;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

public class ProjectSummaryWidget extends Composite implements ModelWidget<ProjectContext> {

	private static final int MARGIN_LEFT = 10;

	private static final ProjectSummaryWidgetMessages messages = GWT.create(ProjectSummaryWidgetMessages.class);

	private static ProjectSummaryWidgetUiBinder uiBinder = GWT.create(ProjectSummaryWidgetUiBinder.class);

	interface ProjectSummaryWidgetUiBinder extends UiBinder<Widget, ProjectSummaryWidget> {}

	interface ProjectSummaryWidgetStyle extends CssResource {
		String containerStateOpen();

		String containerStateClosed();
	}

	@UiField
	ProjectSummaryWidgetStyle style;

	@UiField
	Label name;

	@UiField
	ReleaseDetailWidget releaseDetail;

	@UiField
	UIObject bodyContent;

	@UiField
	FocusPanel releaseContainer;

	@UiField(provided = true)
	ModelWidgetContainer<Release, ReleaseSummaryWidget> releases;

	@UiField
	Label scopesListTitle;

	@UiField(provided = true)
	ModelWidgetContainer<Scope, ScopeSummaryWidget> scopesList;

	@UiField(provided = true)
	protected ReleaseChart chart;

	@UiField
	FocusPanel containerStateToggleButton;

	private final ProjectContext project;

	private final Set<HandlerRegistration> selectionEventHandlers;

	private ReleaseSummaryWidget selectedWidget;

	private boolean hasStartedUp = false;

	public ProjectSummaryWidget(final ProjectContext project) {
		this.project = project;
		selectionEventHandlers = new HashSet<HandlerRegistration>();

		createReleasesContainer();
		createScopesList();
		chart = new ReleaseChart(false);

		initWidget(uiBinder.createAndBindUi(this));

		name.setText(project.getProjectRepresentation().getName());
		setContainerState(true);
	}

	private void setupSelectionEventHandlers() {
		if (!selectionEventHandlers.isEmpty()) return;

		final EventBus eventBus = ClientServiceProvider.getInstance().getEventBus();
		selectionEventHandlers.add(eventBus.addHandler(ReleaseSelectionEvent.getType(), new ReleaseSelectionEventHandler() {
			@Override
			public void onReleaseSelection(final ReleaseSelectionEvent event) {
				setSelected(event.getRelease());
			}
		}));

		selectionEventHandlers.add(eventBus.addHandler(ScopeSelectionEvent.getType(), new ScopeSelectionEventHandler() {
			@Override
			public void onScopeSelectionRequest(final ScopeSelectionEvent event) {
				final Scope s = event.getTargetScope();
				if (!s.getProgress().isDone()) return;

				chart.highlight(s.getProgress().getEndDay());
			}
		}));
	}

	private void removeReleaseSelectionEventHandler() {
		for (final HandlerRegistration reg : selectionEventHandlers) {
			reg.removeHandler();
		}
	}

	private void createReleasesContainer() {
		releases = new ModelWidgetContainer<Release, ReleaseSummaryWidget>(new ModelWidgetFactory<Release, ReleaseSummaryWidget>() {
			@Override
			public ReleaseSummaryWidget createWidget(final Release modelBean) {
				return new ReleaseSummaryWidget(modelBean);
			}
		}, new AnimatedContainer(new ReleaseEffortBasedHorizontalPanel(getProjectRelease())));
	}

	private void createScopesList() {
		scopesList = new ModelWidgetContainer<Scope, ScopeSummaryWidget>(new ModelWidgetFactory<Scope, ScopeSummaryWidget>() {
			@Override
			public ScopeSummaryWidget createWidget(final Scope modelBean) {
				return new ScopeSummaryWidget(modelBean);
			}
		});
	}

	@UiHandler("containerStateToggleButton")
	void onNameClicked(final ClickEvent e) {
		setContainerState(!getContainerState());
	}

	@UiHandler("releaseContainer")
	void onReleaseContainerClicked(final ClickEvent e) {
		if (selectedWidget != null) selectedWidget.setSelected(false);
		updateDetails(getProjectRelease());
	}

	@UiHandler("planningLink")
	void onPlanningLinkClicked(final ClickEvent e) {
		ClientServiceProvider.getInstance().getApplicationPlaceController().goTo(new PlanningPlace(project.getProjectRepresentation()));
	}

	@Override
	public boolean update() {
		name.setText(project.getProjectRepresentation().getName());
		releases.update(project.getProjectRelease().getChildren());
		return false;
	}

	private void updateDetails(final Release release) {
		releaseDetail.setSubject(release);

		scopesListTitle.setText(release.isLeaf() ? messages.scope() : messages.unplannedScope());
		scopesList.update(release.getScopeList());
		final ReleaseChartDataProvider dataProvider = new ReleaseChartDataProvider(release, new ReleaseEstimator(getProjectRelease()), ClientServiceProvider
				.getInstance()
				.getActionExecutionService());

		chart.setRelease(release, dataProvider);
		chart.updateData();
	}

	private void setSelected(final Release release) {
		if (selectedWidget != null && selectedWidget.getModelObject().equals(release)) return;

		if (selectedWidget != null) selectedWidget.setSelected(false);

		selectedWidget = getWidget(release);
		if (selectedWidget != null) selectedWidget.setSelected(true);

		updateDetails(release);
	}

	private void ensureSelectedWidgetIsVisible() {
		selectedWidget.setHierarchicalContainerState(true);
		final Widget widget = selectedWidget.asWidget();
		final Element containerElement = releaseContainer.getElement();

		final int menuLeft = containerElement.getScrollLeft();
		final int menuWidth = containerElement.getClientWidth();
		final int menuRight = menuLeft + menuWidth;

		final Element widgetElement = widget.getElement();
		final int itemLeft = getOffsetLeft(widgetElement, containerElement);
		final int itemWidth = widgetElement.getParentElement().getOffsetWidth();
		final int itemRight = itemLeft + itemWidth;

		if (itemLeft < menuLeft && itemRight > menuRight) return;

		if (itemLeft < menuLeft || itemWidth > menuWidth) containerElement.setScrollLeft(itemLeft - MARGIN_LEFT);
		else if (itemRight > menuRight) containerElement.setScrollLeft(itemLeft - MARGIN_LEFT);
	}

	private int getOffsetLeft(final Element widget, final Element scrollPanel) {
		final Element parent = widget.getOffsetParent();
		if (parent == null) return 0;

		if (parent == scrollPanel) return widget.getOffsetLeft();

		return getOffsetLeft(parent, scrollPanel) + widget.getOffsetLeft();
	}

	private ReleaseSummaryWidget getWidget(final Release release) {
		final ReleaseSummaryWidget w = releases.getWidgetFor(release);
		if (release.isRoot() || w != null) return w;

		final ReleaseSummaryWidget parentWidget = getWidget(release.getParent());
		return parentWidget == null ? null : parentWidget.getChildWidgetFor(release);
	}

	private void setContainerState(final boolean b) {
		bodyContent.setVisible(b);
		containerStateToggleButton.setStyleName(style.containerStateOpen(), b);
		containerStateToggleButton.setStyleName(style.containerStateClosed(), !b);

		if (b) {
			setupSelectionEventHandlers();
			setup();
		}
		else removeReleaseSelectionEventHandler();
	}

	private void setup() {
		if (hasStartedUp) return;

		update();
		setSelected(getCurrentRelease());
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				ensureSelectedWidgetIsVisible();
			}
		});
		chart.updateSize();
		hasStartedUp = true;
	}

	private boolean getContainerState() {
		return bodyContent.isVisible();
	}

	private Release getCurrentRelease() {
		for (final Release r : getProjectRelease().getAllReleasesInTemporalOrder()) {
			if (r.isDone()) continue;

			for (final Scope s : r.getScopeList()) {
				if (s.getProgress().isUnderWork()) return r;
			}
		}
		return getFirstNotCompleteRelease();
	}

	private Release getFirstNotCompleteRelease() {
		for (final Release r : getProjectRelease().getAllReleasesInTemporalOrder()) {
			if (!r.isDone()) return r;
		}
		return getProjectRelease();
	}

	private Release getProjectRelease() {
		return project.getProjectRelease();
	}

	@Override
	public ProjectContext getModelObject() {
		return project;
	}

}
