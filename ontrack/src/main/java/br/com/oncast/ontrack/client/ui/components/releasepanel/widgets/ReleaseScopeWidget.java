package br.com.oncast.ontrack.client.ui.components.releasepanel.widgets;

import java.util.ArrayList;
import java.util.List;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.components.ScopeWidget;
import br.com.oncast.ontrack.client.ui.components.members.DraggableMemberWidget;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.factories.CommandMenuMessages;
import br.com.oncast.ontrack.client.ui.events.ScopeSelectionEvent;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.HorizontalAlignment;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.VerticalAlignment;
import br.com.oncast.ontrack.client.ui.generalwidgets.CommandMenuItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.CustomCommandMenuItemFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.FiltrableCommandMenu;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.PercentualBar;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupCloseListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.SimpleCommandMenuItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.animation.BgColorAnimation;
import br.com.oncast.ontrack.client.ui.generalwidgets.dnd.DragAndDropManager;
import br.com.oncast.ontrack.client.ui.generalwidgets.scope.ScopeAssociatedMembersWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.utils.Color;
import br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareProgressAction;
import br.com.oncast.ontrack.shared.model.effort.Effort;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.DoubleClickEvent;
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

public class ReleaseScopeWidget extends Composite implements ScopeWidget, ModelWidget<Scope> {

	private static final ClientServiceProvider SERVICE_PROVIDER = ClientServiceProvider.getInstance();

	private static final CommandMenuMessages messages = GWT.create(CommandMenuMessages.class);

	private static ReleaseScopeWidgetUiBinder uiBinder = GWT.create(ReleaseScopeWidgetUiBinder.class);

	interface ReleaseScopeWidgetUiBinder extends UiBinder<Widget, ReleaseScopeWidget> {}

	interface ReleaseScopeWidgetStyle extends CssResource {
		String progressIconDone();

		String progressIconNotStarted();

		String progressIconUnderwork();

		String selected();

		String statusBarOpenImpediment();
	}

	@UiField
	ReleaseScopeWidgetStyle style;

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

	@UiField
	PercentualBar percentualBar;

	@UiField
	// TODO use FastLabel
	Label effortLabel;

	@UiField(provided = true)
	ScopeAssociatedMembersWidget associatedUsers;

	private final Scope scope;

	// IMPORTANT Used to refresh DOM only when needed.
	private String currentScopeDescription;

	// IMPORTANT Used to refresh DOM only when needed.
	private String currentScopeProgress;

	private final boolean releaseSpecific;

	private boolean selected = false;
	private boolean highlighted = false;

	public ReleaseScopeWidget(final Scope scope) {
		this(scope, false, null);
	}

	public ReleaseScopeWidget(final Scope scope, final boolean releaseSpecific, final DragAndDropManager userDragAndDropMananger) {
		associatedUsers = createAssociatedUsersListWidget(scope, userDragAndDropMananger);
		initWidget(uiBinder.createAndBindUi(this));

		this.scope = scope;
		this.releaseSpecific = releaseSpecific;
		effortLabel.setVisible(releaseSpecific);
		update();
		setHasOpenImpediments(SERVICE_PROVIDER.getAnnotationService().hasOpenImpediment(scope.getId()));
	}

	@Override
	public boolean update() {
		updateAssociatedUsers();
		return updateDescription() | updateProgress() | updateTitle() | updateValues();
	}

	private void updateAssociatedUsers() {
		associatedUsers.update();
	}

	private boolean updateTitle() {
		final String title = buildLineageRepresentationText();
		if (title.isEmpty() || title.equals(panel.getTitle())) return false;

		descriptionLabel.setTitle(title);
		return true;
	}

	private boolean updateValues() {
		if (!releaseSpecific) return false;

		final Effort effort = scope.getEffort();
		final float inferedEffort = effort.getInfered();
		final String effortStr = ClientDecimalFormat.roundFloat(inferedEffort, 1);
		effortLabel.setText(effortStr);
		effortLabel.setTitle(effortStr + " effort points");
		percentualBar.setPercentual((int) (effort.getAccomplishedPercentual()));

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

		progressIcon.setStyleName(style.progressIconDone(), progress.isDone());
		progressIcon.setStyleName(style.progressIconUnderwork(), progress.isUnderWork());
		progressIcon.setStyleName(style.progressIconNotStarted(), progress.getState() == ProgressState.NOT_STARTED);

		if (!description.isEmpty() && progress.getState() != ProgressState.NOT_STARTED) {
			final Color color = (progress.getState() == ProgressState.DONE) ? Color.GREEN : Color.GRAY;
			new BgColorAnimation(internalPanel, color).animate();
		}

		if (releaseSpecific) {
			final Style s = draggableAnchor.getElement().getStyle();

			if (progress.getState() != ProgressState.NOT_STARTED) {
				s.setBackgroundColor(SERVICE_PROVIDER.getColorProviderService().getColorFor(scope).toCssRepresentation());
			}
			else s.clearBackgroundColor();
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
		final ProjectContext context = ClientServiceProvider.getCurrentProjectContext();

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
		SERVICE_PROVIDER.getActionExecutionService()
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

	@UiHandler("panel")
	public void onScopeWidgetDoubleClick(final DoubleClickEvent e) {
		SERVICE_PROVIDER.getAnnotationService().showAnnotationsFor(scope.getId());
	}

	private void fireScopeSelectionEvent() {
		SERVICE_PROVIDER.getEventBus().fireEventFromSource(new ScopeSelectionEvent(scope), this);
	}

	@Override
	public void setHighlighted(final boolean shouldHighlight) {
		highlighted = shouldHighlight;
		updateSelectionANdHighlightStyle();
	}

	@Override
	public boolean isHighlighted() {
		return highlighted;
	}

	public void setSelected(final boolean shouldSelect) {
		selected = shouldSelect;
		updateSelectionANdHighlightStyle();
	}

	public boolean isSelected() {
		return selected;
	}

	private void updateSelectionANdHighlightStyle() {
		panel.setStyleName(style.selected(), selected || highlighted);
	}

	public void setHasOpenImpediments(final boolean hasOpenImpediments) {
		statusBar.setStyleName(style.statusBarOpenImpediment(), hasOpenImpediments);

		final Color color = hasOpenImpediments ? Color.RED : Color.GRAY;
		new BgColorAnimation(internalPanel, color).animate();
	}

	@Override
	public void addAssociatedUsers(final DraggableMemberWidget widget) {
		associatedUsers.add(widget);
	}

	private ScopeAssociatedMembersWidget createAssociatedUsersListWidget(final Scope scope, final DragAndDropManager userDragAndDropMananger) {
		return new ScopeAssociatedMembersWidget(scope, userDragAndDropMananger, 2);
	}
}
