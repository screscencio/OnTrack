package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.ui.generalwidgets.CommandMenuItem;
import br.com.oncast.ontrack.client.ui.generalwidgets.FiltrableCommandMenu;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;
import br.com.oncast.ontrack.client.ui.generalwidgets.SimpleCommandMenuItem;
import br.com.oncast.ontrack.client.ui.places.progress.ProgressPlace;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

	private static final ClientServices SERVICE_PROVIDER = ClientServices.get();

	private static ReleaseSelectionWidgetUiBinder uiBinder = GWT.create(ReleaseSelectionWidgetUiBinder.class);

	interface ReleaseSelectionWidgetUiBinder extends UiBinder<Widget, ReleaseSelectionWidget> {}

	@UiField
	protected FiltrableCommandMenu releaseSelectionMenu;

	@UiField
	protected FlowPanel rootPanel;

	private final UUID projectId;

	@UiFactory
	protected FiltrableCommandMenu createReleaseSelectionCommandMenu() {
		return new FiltrableCommandMenu(null, 400, 215);
	}

	public ReleaseSelectionWidget() {
		initWidget(uiBinder.createAndBindUi(this));

		projectId = SERVICE_PROVIDER.projectRepresentationProvider().getCurrent().getId();
		updateReleaseMenuItens(getReleases());
	}

	private void updateReleaseMenuItens(final Set<Release> releases) {
		releaseSelectionMenu.setItems(buildUpdateProjectCommandMenuItemList(releases));
	}

	private List<CommandMenuItem> buildUpdateProjectCommandMenuItemList(final Set<Release> releases) {
		final List<CommandMenuItem> releaseMenuItens = new ArrayList<CommandMenuItem>();

		for (final Release release : releases)
			releaseMenuItens.add(createReleasetMenuItem(release));

		return releaseMenuItens;
	}

	private SimpleCommandMenuItem createReleasetMenuItem(final Release release) {
		return new SimpleCommandMenuItem(release.getFullDescription(), new Command() {
			@Override
			public void execute() {
				openReleaseProgress(release);
			}
		});
	}

	private void openReleaseProgress(final Release release) {
		SERVICE_PROVIDER.placeController().goTo(new ProgressPlace(projectId, release.getId()));
	}

	@Override
	public HandlerRegistration addCloseHandler(final CloseHandler<ReleaseSelectionWidget> handler) {
		return releaseSelectionMenu.addHandler(handler, CloseEvent.getType());
	}

	@Override
	public void show() {
		updateReleaseMenuItens(getReleases());
		releaseSelectionMenu.show();
	}

	private Set<Release> getReleases() {
		return SERVICE_PROVIDER.contextProvider().getProjectContext(projectId).getAllLeafReleases();
	}

	@Override
	public void hide() {
		releaseSelectionMenu.hide();
	}

	public void focus() {
		releaseSelectionMenu.focus();
	}
}
