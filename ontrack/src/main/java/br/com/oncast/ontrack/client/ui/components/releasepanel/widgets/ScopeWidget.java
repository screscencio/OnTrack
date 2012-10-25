package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import static br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState.UNDER_WORK;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.factories.CommandMenuMessages;
import br.com.oncast.ontrack.client.ui.events.ScopeSelectionEvent;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.HorizontalAlignment;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.VerticalAlignment;
import br.com.oncast.ontrack.client.ui.generalwidgets.CommandMenuItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.CustomCommandMenuItemFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.FiltrableCommandMenu;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupCloseListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.SimpleCommandMenuItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.animation.BgColorAnimation;
import br.com.oncast.ontrack.client.ui.generalwidgets.utils.Color;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareProgressAction;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ScopeWidget extends Composite implements ModelWidget<Scope> {

	private static final CommandMenuMessages messages = GWT.create(CommandMenuMessages.class);

	private static ScopeWidgetUiBinder uiBinder = GWT.create(ScopeWidgetUiBinder.class);

	interface ScopeWidgetUiBinder extends UiBinder<Widget, ScopeWidget> {}

	interface ScopeWidgetStyle extends CssResource {
		String progressIconDone();

		String progressIconNotStarted();

		String progressIconUnderwork();

		String selected();

		String statusBarOpenImpediment();
	}

	@UiField
	ScopeWidgetStyle style;

	@UiField
	FocusPanel panel;

	@UiField
	// TODO use FastLabel
	Label descriptionLabel;

	@UiField
	FocusPanel draggableAnchor;

	@UiField
	FocusPanel progressIcon;

	@UiField
	FocusPanel statusBar;

	@UiField
	HorizontalPanel internalPanel;

	private final Scope scope;

	// IMPORTANT Used to refresh DOM only when needed.
	private String currentScopeDescription;

	// IMPORTANT Used to refresh DOM only when needed.
	private String currentScopeProgress;

	public ScopeWidget(final Scope scope) {
		initWidget(uiBinder.createAndBindUi(this));

		this.scope = scope;
		draggableAnchor.getElement().getStyle().setBackgroundColor(ClientServiceProvider.getInstance().getColorProviderService().getColorFor(scope));
		update();
		setHasOpenImpediments(ClientServiceProvider.getInstance().getAnnotationService().hasOpenImpediment(scope.getId()));
	}

	@Override
	public boolean update() {
		return updateDescription() | updateProgress() | updateTitle();
	}

	private boolean updateTitle() {
		final String title = buildLineageRepresentationText();
		if (title.isEmpty() || title.equals(panel.getTitle())) return false;

		descriptionLabel.setTitle(title);
		return true;
	}

	private String buildLineageRepresentationText() {
		if (scope.isRoot()) return "";

		final StringBuilder builder = new StringBuilder();
		Scope current = scope.getParent();
		while (!current.isRoot()) {
			builder.insert(0, current.getDescription());
			builder.insert(0, " > ");
			current = current.getParent();
		}
		builder.insert(0, current.getDescription());
		final String title = builder.toString();
		return title;
	}

	/**
	 * @return if the description was updated.
	 */
	private boolean updateDescription() {
		final String description = scope.getDescription();
		if (description.equals(currentScopeDescription)) return false;
		currentScopeDescription = description;

		descriptionLabel.setText(currentScopeDescription);

		return true;
	}

	// TODO +++ add another icon representation when there are Done and NotStarted children only (perhaps percentage).
	/**
	 * @return if the progress was updated.
	 */
	private boolean updateProgress() {
		final Progress progress = scope.getProgress();
		final String description = progress.getDescription();
		if (!description.isEmpty() && description.equals(currentScopeProgress)) return false;
		currentScopeProgress = description;

		progressIcon.setStyleName(style.progressIconDone(), progress.getState() == ProgressState.DONE);
		progressIcon.setStyleName(style.progressIconUnderwork(), progress.getState() == UNDER_WORK);
		progressIcon.setStyleName(style.progressIconNotStarted(), progress.getState() == ProgressState.NOT_STARTED);

		if (!description.isEmpty()) {
			final Color color = (progress.getState() == ProgressState.DONE) ? Color.GREEN : Color.BLUE;
			new BgColorAnimation(internalPanel, color).animate(800);
		}

		return true;
	}

	public Scope getScope() {
		return scope;
	}

	@Override
	public Scope getModelObject() {
		return getScope();
	}

	public Widget getDraggableAnchor() {
		return draggableAnchor;
	}

	@UiHandler("progressIcon")
	public void onProgressIconClick(final ClickEvent e) {
		fireScopeSelectionEvent(); // Showing the item that will be changed.
		e.stopPropagation();
		final List<CommandMenuItem> items = new ArrayList<CommandMenuItem>();
		final ProjectContext context = ClientServiceProvider.getInstance().getContextProviderService().getCurrentProjectContext();

		final String notStartedDescription = ProgressState.NOT_STARTED.getDescription();
		items.add(createItem("Not Started", notStartedDescription));
		for (final String progressDefinition : context.getProgressDefinitions(scope))
			if (!notStartedDescription.equals(progressDefinition)) items.add(createItem(progressDefinition,
					progressDefinition));

		final FiltrableCommandMenu commandsMenu = new FiltrableCommandMenu(getProgressCommandMenuItemFactory(), 200, 264);
		commandsMenu.setOrderedItems(items);
		// Scheduled because of selection event steels focus if not
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				PopupConfig.configPopup()
						.alignHorizontal(HorizontalAlignment.RIGHT, new AlignmentReference(progressIcon, HorizontalAlignment.RIGHT))
						.alignVertical(VerticalAlignment.TOP, new AlignmentReference(progressIcon, VerticalAlignment.BOTTOM))
						.popup(commandsMenu)
						.onClose(new PopupCloseListener() {
							@Override
							public void onHasClosed() {
								fireScopeSelectionEvent(); // Return focus to scopeTree;
							}
						})
						.pop();
			}
		});
	}

	public SimpleCommandMenuItem createItem(final String itemText, final String progressToDeclare) {
		return new SimpleCommandMenuItem(itemText, progressToDeclare, new Command() {

			@Override
			public void execute() {
				declareProgress(progressToDeclare);
			}
		});
	}

	private void declareProgress(final String progressDescription) {
		ClientServiceProvider.getInstance().getActionExecutionService()
				.onUserActionExecutionRequest(new ScopeDeclareProgressAction(scope.getId(), progressDescription));
	}

	private CustomCommandMenuItemFactory getProgressCommandMenuItemFactory() {
		return new CustomCommandMenuItemFactory() {

			@Override
			public String getNoItemText() {
				return null;
			}

			@Override
			public CommandMenuItem createCustomItem(final String inputText) {
				return new SimpleCommandMenuItem(messages.markAs(inputText), inputText, new Command() {
					@Override
					public void execute() {
						declareProgress(inputText);
					}
				});
			}
		};
	}

	@UiHandler("panel")
	public void onScopeWidgetClick(final ClickEvent e) {
		fireScopeSelectionEvent();
	}

	private void fireScopeSelectionEvent() {
		ClientServiceProvider.getInstance().getEventBus().fireEventFromSource(new ScopeSelectionEvent(scope), this);
	}

	public void setSelected(final boolean shouldSelect) {
		panel.setStyleName(style.selected(), shouldSelect);
	}

	public void setHasOpenImpediments(final boolean hasOpenImpediments) {
		statusBar.setStyleName(style.statusBarOpenImpediment(), hasOpenImpediments);
	}

}
