package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.context.ProjectListChangeListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.DefaultTextedTextBox;
import br.com.oncast.ontrack.client.ui.generalwidgets.FilterEngine;
import br.com.oncast.ontrack.client.ui.generalwidgets.FilterEngine.FilterResultListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;
import br.com.oncast.ontrack.client.ui.places.planning.PlanningPlace;
import br.com.oncast.ontrack.client.ui.places.projectCreation.ProjectCreationPlace;
import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;
import br.com.oncast.ontrack.shared.model.project.ProjectRepresentation;
import br.com.oncast.ontrack.shared.services.url.URLBuilder;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

// TODO++++ Use FiltrableCommandMenu with custom Style
public class ProjectMenuWidget extends Composite implements HasCloseHandlers<ProjectMenuWidget>, PopupAware, ProjectListChangeListener,
		FilterResultListener<MenuBoxItem> {

	private static ProjectMenuWidgetUiBinder uiBinder = GWT.create(ProjectMenuWidgetUiBinder.class);

	interface ProjectMenuWidgetUiBinder extends UiBinder<Widget, ProjectMenuWidget> {}

	@UiField
	Anchor exportMapLink;

	@UiField
	DefaultTextedTextBox projectFilterTextBox;

	@UiField
	MenuBox results;

	private FilterEngine<MenuBoxItem> engine;

	private static final Set<Integer> NAVIGATION_KEYS = new HashSet<Integer>();
	static {
		NAVIGATION_KEYS.add(BrowserKeyCodes.KEY_DOWN);
		NAVIGATION_KEYS.add(BrowserKeyCodes.KEY_UP);
	}

	public ProjectMenuWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@Override
	protected void onLoad() {
		super.onLoad();
		ClientServiceProvider.getInstance().getProjectRepresentationProvider().registerProjectListChangeListener(this);
	}

	@Override
	protected void onUnload() {
		super.onUnload();
		ClientServiceProvider.getInstance().getProjectRepresentationProvider().unregisterProjectListChangeListener(this);
	}

	@UiHandler("projectFilterTextBox")
	protected void onKeyDown(final KeyDownEvent event) {
		if (!NAVIGATION_KEYS.contains(event.getNativeKeyCode())) return;
		event.preventDefault();

		if (event.getNativeKeyCode() == BrowserKeyCodes.KEY_DOWN) results.selectDown();
		else results.selectUp();
	}

	@UiHandler("projectFilterTextBox")
	protected void onKeyUp(final KeyUpEvent event) {
		if (NAVIGATION_KEYS.contains(event.getNativeKeyCode())) return;
		event.stopPropagation();
		if (event.getNativeKeyCode() == BrowserKeyCodes.KEY_ESCAPE) hide();
		else if (event.getNativeKeyCode() == BrowserKeyCodes.KEY_ENTER) {
			changeProject();
		}
		else filter();
	}

	private void filter() {
		final String text = projectFilterTextBox.getText();
		engine.filterMenuItens(text);
	}

	private void changeProject() {
		final MenuBoxItem selectedItem = results.getSelectedItem();
		if (selectedItem != null) {
			hide();
			selectedItem.executeCommand();
		}
	}

	@Override
	public void show() {
		setResultsVisiblity(false);
		this.setVisible(true);

		projectFilterTextBox.setFocus(true);
		projectFilterTextBox.setCursorPos(0);
	}

	@Override
	public void hide() {
		if (!this.isVisible()) return;
		this.setVisible(false);

		CloseEvent.fire(this, this);
	}

	@Override
	public HandlerRegistration addCloseHandler(final CloseHandler<ProjectMenuWidget> handler) {
		return this.addHandler(handler, CloseEvent.getType());
	}

	@UiHandler("exportMapLink")
	protected void onExportMapLinkClick(final ClickEvent event) {
		exportData();
	}

	private void exportData() {
		final String url = URLBuilder.buildMindMapExportURL(ClientServiceProvider.getInstance().getProjectRepresentationProvider().getCurrent().getId());
		exportMapLink.setHref(url);
		hide();
	}

	@Override
	public void onProjectListChanged(final Set<ProjectRepresentation> projectRepresentations) {
		this.engine = new FilterEngine<MenuBoxItem>(createProjectList(projectRepresentations), this);
	}

	private List<MenuBoxItem> createProjectList(final Set<ProjectRepresentation> projectRepresentations) {
		final ArrayList<MenuBoxItem> itens = new ArrayList<MenuBoxItem>();
		for (final ProjectRepresentation project : projectRepresentations) {
			itens.add(createItem(project));
		}
		return itens;
	}

	@Override
	public void onProjectListAvailabilityChange(final boolean availability) {
		projectFilterTextBox.setText(availability ? "" : "Loading Projects list...");
		projectFilterTextBox.setEnabled(availability);
	}

	@Override
	public void onFilterActivationChanged(final boolean isActive) {
		setResultsVisiblity(isActive);
	}

	private void setResultsVisiblity(final boolean isActive) {
		results.setVisible(isActive);
	}

	@Override
	public void onUpdateItens(final List<MenuBoxItem> filteredItens, final boolean shouldAddCustomItem, final String filterText) {
		results.clear();

		if (shouldAddCustomItem) addCustomItem(filterText);

		for (final MenuBoxItem item : filteredItens) {
			results.add(item);
		}

		results.selectItem(shouldAddCustomItem ? 1 : 0);
	}

	private void addCustomItem(final String filterText) {
		results.add(createCustomItem("Create '" + filterText + "'", filterText));
	}

	private MenuBoxItem createItem(final ProjectRepresentation project) {
		return new MenuBoxItem(project.getName(), new Command() {
			@Override
			public void execute() {
				ClientServiceProvider.getInstance().getApplicationPlaceController().goTo(new PlanningPlace(project));
				hide();
			}
		});
	}

	private MenuBoxItem createCustomItem(final String text, final String value) {
		return new MenuBoxItem(text, value, new Command() {
			@Override
			public void execute() {
				final ProjectCreationPlace projectCreationPlace = new ProjectCreationPlace(value);
				hide();
				ClientServiceProvider.getInstance().getApplicationPlaceController().goTo(projectCreationPlace);
			}
		});
	}
}
