package br.com.oncast.ontrack.client.ui.generalwidgets;

import br.com.oncast.ontrack.client.ui.components.annotations.widgets.ChecklistsContainerWidget;
import br.com.oncast.ontrack.shared.model.description.exceptions.DescriptionNotFoundException;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class CheckListWidget extends Composite {

	private static CheckListWidgetUiBinder uiBinder = GWT.create(CheckListWidgetUiBinder.class);

	interface CheckListWidgetUiBinder extends UiBinder<Widget, CheckListWidget> {}

	@UiField
	protected ChecklistsContainerWidget container;

	private final Release release;
	private Scope scope;

	public CheckListWidget(final Release release) {
		this.release = release;
		initWidget(uiBinder.createAndBindUi(this));
		container.setSubjectId(release.getId());
		try {
			update();
		}
		catch (final DescriptionNotFoundException e) {}
	}

	protected String getCurrentTitle() {
		return (scope == null) ? release.getDescription() : scope.getDescription();
	}

	protected UUID getCurrentId() {
		return (scope == null) ? release.getId() : scope.getId();
	}

	private void setCurrentId(final Scope scope) throws DescriptionNotFoundException {
		this.scope = scope;
		update();
	}

	public void setSelected(final Scope scope) {
		try {
			setCurrentId(scope);
		}
		catch (final DescriptionNotFoundException e) {}
	}

	private void update() throws DescriptionNotFoundException {
		container.setSubjectId(getCurrentId());
	}

}
