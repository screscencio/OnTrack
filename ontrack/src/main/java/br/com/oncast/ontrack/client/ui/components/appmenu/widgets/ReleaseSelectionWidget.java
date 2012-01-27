package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.generalwidgets.CommandMenuItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.FiltrableCommandMenu;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;
import br.com.oncast.ontrack.client.ui.places.progress.ProgressPlace;
import br.com.oncast.ontrack.shared.model.release.Release;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;

public class ReleaseSelectionWidget extends Composite implements HasCloseHandlers<ReleaseSelectionWidget>, PopupAware {

	private static final ClientServiceProvider SERVICE_PROVIDER = ClientServiceProvider.getInstance();

	private static ReleaseSelectionWidgetUiBinder uiBinder = GWT.create(ReleaseSelectionWidgetUiBinder.class);

	interface ReleaseSelectionWidgetUiBinder extends UiBinder<Widget, ReleaseSelectionWidget> {}

	@UiField
	protected FiltrableCommandMenu releaseSelectionMenu;

	@UiField
	protected FlowPanel rootPanel;

	private final long projectId;

	@UiFactory
	protected FiltrableCommandMenu createReleaseSelectionCommandMenu() {
		return new FiltrableCommandMenu(null, 700, 400);
	}

	public ReleaseSelectionWidget() {
		initWidget(uiBinder.createAndBindUi(this));

		projectId = SERVICE_PROVIDER.getProjectRepresentationProvider().getCurrentProjectRepresentation().getId();
		registerCloseHandler();
	}

	private void registerCloseHandler() {
		releaseSelectionMenu.addCloseHandler(new CloseHandler<FiltrableCommandMenu>() {
			@Override
			public void onClose(final CloseEvent<FiltrableCommandMenu> event) {
				hide();
			}
		});
	}

	private void updateReleaseMenuItens(final Set<Release> releases) {
		releaseSelectionMenu.setItens(buildUpdateProjectCommandMenuItemList(releases));
	}

	private List<CommandMenuItem> buildUpdateProjectCommandMenuItemList(final Set<Release> releases) {
		final List<CommandMenuItem> releaseMenuItens = new ArrayList<CommandMenuItem>();

		for (final Release release : releases)
			releaseMenuItens.add(createReleasetMenuItem(release));

		return releaseMenuItens;
	}

	private CommandMenuItem createReleasetMenuItem(final Release release) {
		return new CommandMenuItem(release.getFullDescription(), new Command() {
			@Override
			public void execute() {
				openReleaseProgress(release);
			}
		});
	}

	private void openReleaseProgress(final Release release) {
		SERVICE_PROVIDER.getApplicationPlaceController().goTo(new ProgressPlace(projectId, release.getId()));
	}

	@Override
	public HandlerRegistration addCloseHandler(final CloseHandler<ReleaseSelectionWidget> handler) {
		return addHandler(handler, CloseEvent.getType());
	}

	@Override
	public void show() {
		updateReleaseMenuItens(getReleases());
		rootPanel.setVisible(true);
		releaseSelectionMenu.show();
		releaseSelectionMenu.selectFirstItem();
		releaseSelectionMenu.focus();
	}

	private Set<Release> getReleases() {
		return SERVICE_PROVIDER.getContextProviderService().getProjectContext(projectId).getAllReleasesWithOpenScopes();
	}

	@Override
	public void hide() {
		rootPanel.setVisible(false);
		releaseSelectionMenu.hide();
		CloseEvent.fire(this, this);
	}

	public void focus() {
		releaseSelectionMenu.focus();
	}
}
