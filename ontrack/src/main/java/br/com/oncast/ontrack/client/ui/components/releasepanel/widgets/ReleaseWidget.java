package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import static br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.configPopup;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.client.ui.generalwidgets.ChartPanel;
import br.com.oncast.ontrack.client.ui.generalwidgets.CommandMenuItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.MouseCommandsMenu;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupOpenListener;
import br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.ReleaseEstimator;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

// TODO Refactor dividing visualization logic from business logic
public class ReleaseWidget extends Composite implements ModelWidget<Release> {

	private static ReleasePanelItemWidgetUiBinder uiBinder = GWT.create(ReleasePanelItemWidgetUiBinder.class);

	interface ReleasePanelItemWidgetUiBinder extends UiBinder<Widget, ReleaseWidget> {}

	interface Style extends CssResource {
		String headerContainerStateImageOpened();

		String headerContainerStateImageClosed();

		String chartPanel();
	}

	@UiField
	protected Style style;

	@UiField
	protected Label descriptionLabel;

	@UiField
	protected Label progressLabel;

	@UiField
	protected MouseCommandsMenu mouseActionsMenu;

	@UiField
	protected ReleaseWidgetContainer releaseContainer;

	@UiField
	protected ScopeWidgetContainer scopeContainer;

	@UiField
	protected DivElement bodyContainer;

	@UiField
	protected Image containerStateImage;

	@UiField
	protected FocusPanel containerToogleClickableArea;

	@UiFactory
	protected ReleaseWidgetContainer createReleaseContainer() {
		return new ReleaseWidgetContainer(releaseWidgetFactory, containerUpdateListener);
	}

	@UiFactory
	protected ScopeWidgetContainer createScopeContainer() {
		return new ScopeWidgetContainer(scopeWidgetFactory, containerUpdateListener);
	}

	@UiFactory
	protected MouseCommandsMenu createMouseActionMenu() {
		final List<CommandMenuItem> itens = new ArrayList<CommandMenuItem>();
		itens.add(new CommandMenuItem("Increase priority", new Command() {

			@Override
			public void execute() {
				releasePanelInteractionHandler.onReleaseIncreasePriorityRequest(release);
			}
		}));
		itens.add(new CommandMenuItem("Decrease priority", new Command() {

			@Override
			public void execute() {
				releasePanelInteractionHandler.onReleaseDecreasePriorityRequest(release);
			}
		}));
		itens.add(new CommandMenuItem("Delete Release", new Command() {

			@Override
			public void execute() {
				releasePanelInteractionHandler.onReleaseDeletionRequest(release);
			}
		}));
		return new MouseCommandsMenu(itens);
	}

	private final ModelWidgetFactory<Release, ReleaseWidget> releaseWidgetFactory;

	private final ModelWidgetFactory<Scope, ScopeWidget> scopeWidgetFactory;

	private final ModelWidgetContainerListener containerUpdateListener;

	private boolean isContainerStateOpen;

	// IMPORTANT Used to refresh DOM only when needed.
	private String currentReleaseDescription;

	// IMPORTANT Used to refresh DOM only when needed.
	private String currentReleaseProgressDescription;

	private final Release release;

	private final ReleasePanelWidgetInteractionHandler releasePanelInteractionHandler;

	private ChartPanel chartPanel;

	public ReleaseWidget(final Release release, final ModelWidgetFactory<Release, ReleaseWidget> releaseWidgetFactory,
			final ModelWidgetFactory<Scope, ScopeWidget> scopeWidgetFactory,
			final ReleasePanelWidgetInteractionHandler releasePanelInteractionHandler) {
		this.release = release;

		this.releaseWidgetFactory = releaseWidgetFactory;
		this.scopeWidgetFactory = scopeWidgetFactory;
		this.releasePanelInteractionHandler = releasePanelInteractionHandler;

		this.containerUpdateListener = createContainerUpdateListener();

		initWidget(uiBinder.createAndBindUi(this));

		scopeContainer.setOwnerRelease(release);

		populateChildScopeWidgets();
		populateChildReleaseWidgets();

		addToogleClickableAreaHandler();

		updateDescription();
		updateProgress();
		setContainerState(true);

		configPopup().link(progressLabel).popup(getChartPanel()).alignRight(progressLabel).alignBelow(progressLabel).onOpen(new PopupOpenListener() {
			@Override
			public void onWillOpen() {
				updateBurnUpChart();
			}
		});

	}

	private ChartPanel getChartPanel() {
		if (chartPanel != null) return chartPanel;
		chartPanel = new ChartPanel();
		chartPanel.setStyleName(style.chartPanel());
		return chartPanel;
	}

	private ModelWidgetContainerListener createContainerUpdateListener() {
		return new ModelWidgetContainerListener() {

			@Override
			public void onUpdateComplete(final boolean hasChanged) {
				if (!hasChanged) return;

				setContainerState(true);
			}
		};
	}

	private void populateChildReleaseWidgets() {
		for (final Scope scope : release.getScopeList())
			scopeContainer.createChildModelWidget(scope);
	}

	private void populateChildScopeWidgets() {
		for (final Release childRelease : release.getChildren())
			releaseContainer.createChildModelWidget(childRelease);
	}

	private void addToogleClickableAreaHandler() {
		containerToogleClickableArea.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(final ClickEvent event) {
				setContainerState(!isContainerStateOpen);
			}
		});
	}

	@Override
	public boolean update() {
		updateDescription();
		updateProgress();

		return releaseContainer.update(release.getChildren()) | scopeContainer.update(release.getScopeList());
	}

	private void updateDescription() {
		if (release.getDescription().equals(currentReleaseDescription)) return;
		currentReleaseDescription = release.getDescription();

		descriptionLabel.setText(currentReleaseDescription);
	}

	private void updateProgress() {
		final String newProgress = getProcessDescription();

		if (newProgress.equals(currentReleaseProgressDescription)) return;
		currentReleaseProgressDescription = newProgress;

		updateReleaseChartPanel(newProgress);
	}

	private String getProcessDescription() {
		if (release.isDone()) return "100%";
		final float effortSum = release.getEffortSum();
		if (effortSum == 0) return "";

		final float concludedEffortSum = release.getAccomplishedEffortSum();
		final float percentage = 100 * concludedEffortSum / effortSum;
		return ClientDecimalFormat.roundFloat(percentage, 1) + "%";
	}

	private void updateReleaseChartPanel(final String newProgress) {
		if (newProgress.isEmpty()) getChartPanel().hide();
		progressLabel.setText(currentReleaseProgressDescription);
	}

	public void setContainerState(final boolean shouldOpen) {
		if (shouldOpen) {
			containerStateImage.getElement().removeClassName(style.headerContainerStateImageClosed());
			containerStateImage.getElement().addClassName(style.headerContainerStateImageOpened());
		}
		else {
			containerStateImage.getElement().removeClassName(style.headerContainerStateImageOpened());
			containerStateImage.getElement().addClassName(style.headerContainerStateImageClosed());
		}

		scopeContainer.setVisible(shouldOpen);
		releaseContainer.setVisible(releaseContainer.getWidgetCount() != 0 && shouldOpen);

		isContainerStateOpen = shouldOpen;
	}

	private void updateBurnUpChart() {
		final ReleaseChartDataProvider dataProvider = new ReleaseChartDataProvider(release, getReleaseEstimator());

		getChartPanel().setMaxValue(dataProvider.getEffortSum())
				.setXAxisLineValues(dataProvider.getReleaseDays())
				.setYAxisLineValues(dataProvider.getAccomplishedEffortsByDate())
				.setIdealEndDay(dataProvider.getEstimatedEndDay());
	}

	private ReleaseEstimator getReleaseEstimator() {
		// XXX Burnup: Locate the root Release in a more direct fashion.

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
}
