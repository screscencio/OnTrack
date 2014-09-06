package br.com.oncast.ontrack.client.ui.generalwidgets.progress;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ProgressIconUpdater;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ProgressIconUpdaterStyle;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.SpacerCommandMenuItem;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.factories.CommandMenuMessages;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.HorizontalAlignment;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentReference.VerticalAlignment;
import br.com.oncast.ontrack.client.ui.generalwidgets.CommandMenuItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.CustomCommandMenuItemFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.FiltrableCommandMenu;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig;
import br.com.oncast.ontrack.client.ui.generalwidgets.SimpleCommandMenuItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.TextAndImageCommandMenuItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.impediment.ImpedimentListWidget;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareProgressAction;
import br.com.oncast.ontrack.shared.model.progress.Progress.ProgressState;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;

public class ProgressIcon extends Composite {

	private static ProgressIconUiBinder uiBinder = GWT.create(ProgressIconUiBinder.class);

	private static final CommandMenuMessages MESSAGES = GWT.create(CommandMenuMessages.class);

	interface ProgressIconUiBinder extends UiBinder<Widget, ProgressIcon> {}

	public ProgressIcon() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	ProgressIconUpdaterStyle style;

	@UiField
	FocusPanel progressIcon;

	private boolean hasOpenImpediments;

	private Scope task;

	public ProgressIcon(final Scope task) {
		this.task = task;
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void update() {
		hasOpenImpediments = ClientServices.get().details().hasOpenImpediment(task.getId());

		final ProgressIconUpdater updater = ProgressIconUpdater.getUpdater(task, hasOpenImpediments);
		progressIcon.setStyleName(updater.getStyle(style));
		progressIcon.setTitle(updater.getTitle(task));
	}

	@UiHandler("progressIcon")
	public void onProgressIconClick(final ClickEvent e) {
		e.stopPropagation();
		showPopup(hasOpenImpediments ? new ImpedimentListWidget(task) : createProgressMenu());
	}

	private Widget createProgressMenu() {
		final List<CommandMenuItem> items = new ArrayList<CommandMenuItem>();
		final ProjectContext context = ClientServices.getCurrentProjectContext();

		for (final String progressDefinition : context.getProgressDefinitions(task))
			items.add(createItem(ProgressState.getLabelForDescription(progressDefinition), progressDefinition));
		items.add(new SpacerCommandMenuItem());
		items.add(new TextAndImageCommandMenuItem("icon-flag", MESSAGES.impediments(), new Command() {

			@Override
			public void execute() {
				showPopup(new ImpedimentListWidget(task));
			}
		}));

		final FiltrableCommandMenu commandsMenu = new FiltrableCommandMenu(getProgressCommandMenuItemFactory(), 200, 264);
		commandsMenu.setOrderedItems(items);
		return commandsMenu;
	}

	private CustomCommandMenuItemFactory getProgressCommandMenuItemFactory() {
		return new CustomCommandMenuItemFactory() {

			@Override
			public String getNoItemText() {
				return null;
			}

			@Override
			public CommandMenuItem createCustomItem(final String inputText) {
				return new SimpleCommandMenuItem(MESSAGES.markAs(inputText), inputText, new Command() {
					@Override
					public void execute() {
						declareProgress(inputText);
					}
				});
			}

			@Override
			public boolean shouldPrioritizeCustomItem() {
				return false;
			}
		};
	}

	private void showPopup(final Widget finalPopupWidget) {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				PopupConfig.configPopup().alignHorizontal(HorizontalAlignment.RIGHT, new AlignmentReference(progressIcon, HorizontalAlignment.RIGHT))
						.alignVertical(VerticalAlignment.TOP, new AlignmentReference(progressIcon, VerticalAlignment.BOTTOM)).popup(finalPopupWidget).pop();
			}
		});
	}

	private SimpleCommandMenuItem createItem(final String itemText, final String progressToDeclare) {
		return new SimpleCommandMenuItem(itemText, progressToDeclare, new Command() {

			@Override
			public void execute() {
				declareProgress(progressToDeclare);
			}
		});
	}

	private void declareProgress(final String progressDescription) {
		ClientServices.get().actionExecution().onUserActionExecutionRequest(new ScopeDeclareProgressAction(task.getId(), progressDescription));
	}

}
