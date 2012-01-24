package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import static br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.configPopup;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.client.ui.generalwidgets.CommandMenuItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.EditableLabel;
import br.com.oncast.ontrack.client.ui.generalwidgets.EditableLabelEditionHandler;
import br.com.oncast.ontrack.client.ui.generalwidgets.MouseCommandsMenu;
import br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.release.ReleaseEstimator;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
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
	protected EditableLabel descriptionLabel;

	@UiField
	protected Label progressLabel;

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

	@UiField
	protected Image menuLink;

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

	private boolean isContainerStateOpen;

	// IMPORTANT Used to refresh DOM only when needed.
	private String currentReleaseDescription;

	// IMPORTANT Used to refresh DOM only when needed.
	private String currentReleaseProgressDescription;

	private final Release release;

	private final ReleasePanelWidgetInteractionHandler releasePanelInteractionHandler;

	private MouseCommandsMenu mouseCommandsMenu;

	private ReleaseChart chartPanel;

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

		scopeContainer.setOwnerRelease(release);

		populateChildScopeWidgets();
		populateChildReleaseWidgets();

		updateDescription();
		updateProgress();
		setContainerState(true);

		configPopup().link(progressLabel).popup(getChartPanel()).alignRight(progressLabel).alignBelow(progressLabel);
		configPopup().link(menuLink).popup(getMouseActionMenu()).alignRight(menuLink).alignBelow(menuLink, 2);
	}

	@UiHandler("containerToogleClickableArea")
	protected void addToogleClickableAreaHandler(final ClickEvent event) {
		setContainerState(!isContainerStateOpen);
	}

	private MouseCommandsMenu getMouseActionMenu() {
		if (mouseCommandsMenu != null) return mouseCommandsMenu;

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
		mouseCommandsMenu = new MouseCommandsMenu(itens);
		return mouseCommandsMenu;
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

	@Override
	public boolean update() {
		updateDescription();
		updateProgress();

		final boolean releaseUpdate = releaseContainer.update(release.getChildren());
		final boolean scopeUpdate = scopeContainer.update(release.getScopeList());
		return releaseUpdate || scopeUpdate;
	}

	private void updateDescription() {
		if (release.getDescription().equals(currentReleaseDescription)) return;
		currentReleaseDescription = release.getDescription();

		descriptionLabel.setValue(currentReleaseDescription);
	}

	private void updateProgress() {
		final String newProgress = getProgressDescription();

		if (newProgress.equals(currentReleaseProgressDescription)) return;
		currentReleaseProgressDescription = newProgress;
		progressLabel.setText(currentReleaseProgressDescription);
	}

	private String getProgressDescription() {
		if (release.isDone()) return "100%";
		final float effortSum = release.getEffortSum();
		if (effortSum == 0) return "";

		final float concludedEffortSum = release.getAccomplishedEffortSum();
		final float percentage = 100 * concludedEffortSum / effortSum;
		return ClientDecimalFormat.roundFloat(percentage, 1) + "%";
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

	private ReleaseChart getChartPanel() {
		if (chartPanel != null) return chartPanel;
		chartPanel = new ReleaseChart(new ReleaseChartDataProvider(release, getReleaseEstimator()));
		chartPanel.setStyleName(style.chartPanel());
		return chartPanel;
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
