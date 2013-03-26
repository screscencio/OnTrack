package br.com.oncast.ontrack.client.ui.places.progress.details;

import br.com.oncast.ontrack.client.ui.generalwidgets.CheckListWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.DescriptionWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.TasksManagementWidget;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.event.logical.shared.BeforeSelectionEvent;
import com.google.gwt.event.logical.shared.BeforeSelectionHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

public class ProgressDetailPanel extends Composite {

	private static ProgressDetailPanelUiBinder uiBinder = GWT.create(ProgressDetailPanelUiBinder.class);

	interface ProgressDetailPanelStyle extends CssResource {
		String tabHeaderLabelDisabled();

		String tasksTabHeader();
	}

	@UiField
	protected ProgressDetailPanelStyle style;

	@UiField
	protected TabLayoutPanel container;

	@UiField(provided = true)
	protected DescriptionWidget descriptionWidget;

	@UiField(provided = true)
	protected CheckListWidget checklistWidget;

	@UiField(provided = true)
	protected TasksManagementWidget tasksWidget;

	private Scope scope;

	interface ProgressDetailPanelUiBinder extends UiBinder<Widget, ProgressDetailPanel> {}

	public ProgressDetailPanel(final Release release) {
		initializeComponents(release);

		initWidget(uiBinder.createAndBindUi(this));
		container.selectTab(0);

		getTasksHeader().addClassName(style.tabHeaderLabelDisabled());

		container.addBeforeSelectionHandler(new BeforeSelectionHandler<Integer>() {
			@Override
			public void onBeforeSelection(final BeforeSelectionEvent<Integer> event) {
				if (event.getItem() == 2 && (scope == null || !scope.isStory())) {
					event.cancel();
				}
			}
		});

		container.addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(final SelectionEvent<Integer> event) {
				if (event.getSelectedItem() == 2) tasksWidget.setFocus(true);
			}
		});
	}

	private Element getTasksHeader() {
		final NodeList<Element> elements = container.getElement().getElementsByTagName("span");
		for (int i = 0; i < elements.getLength(); i++) {
			final Element e = elements.getItem(i);
			if (e.getClassName().contains(style.tasksTabHeader())) { return e; }
		}
		return null;
	}

	public void setSelected(final Scope scope) {
		this.scope = scope;
		if (scope == null || !scope.isStory()) {
			getTasksHeader().addClassName(style.tabHeaderLabelDisabled());
			if (container.getSelectedIndex() == 2) container.selectTab(0);
		}
		else getTasksHeader().removeClassName(style.tabHeaderLabelDisabled());

		descriptionWidget.setSelected(scope);
		checklistWidget.setSelected(scope);
		tasksWidget.setSelected(scope);
	}

	private void initializeComponents(final Release release) {
		descriptionWidget = new DescriptionWidget(release);
		checklistWidget = new CheckListWidget(release);
		tasksWidget = new TasksManagementWidget();
	}

	public void update() {
		tasksWidget.update();
	}

}
