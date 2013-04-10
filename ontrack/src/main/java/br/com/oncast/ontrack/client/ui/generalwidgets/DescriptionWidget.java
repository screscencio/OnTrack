package br.com.oncast.ontrack.client.ui.generalwidgets;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.shared.model.description.Description;
import br.com.oncast.ontrack.shared.model.description.exceptions.DescriptionNotFoundException;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class DescriptionWidget extends Composite {

	private static final ClientServiceProvider SERVICE_PROVIDER = ClientServiceProvider.get();
	private static final ContextProviderService CONTEXT_PROVIDER_SERVICE = SERVICE_PROVIDER.contextProvider();

	private static DescriptionWidgetUiBinder uiBinder = GWT.create(DescriptionWidgetUiBinder.class);

	interface DescriptionWidgetUiBinder extends UiBinder<Widget, DescriptionWidget> {}

	@UiField(provided = true)
	DescriptionRichTextLabel descriptionLabel;

	private final Release release;

	private Scope scope;

	public DescriptionWidget(final Release release) {
		this.release = release;
		descriptionLabel = new DescriptionRichTextLabel(new EditableLabelEditionHandler() {
			@Override
			public boolean onEditionRequest(final String text) {
				SERVICE_PROVIDER.details().updateDescription(getCurrentId(), text);
				return true;
			}

			@Override
			public void onEditionExit(final boolean canceledEdition) {}

			@Override
			public void onEditionStart() {}

		});
		initWidget(uiBinder.createAndBindUi(this));
		update();
	}

	protected String getCurrentTitle() {
		return (scope == null) ? release.getDescription() : scope.getDescription();
	}

	protected UUID getCurrentId() {
		return (scope == null) ? release.getId() : scope.getId();
	}

	private void setCurrentId(final Scope scope) {
		this.scope = scope;
		update();
	}

	public void setSelected(final Scope scope) {
		setCurrentId(scope);
	}

	private void update() {
		try {
			final Description description = getDescription();
			descriptionLabel.setText(description.getDescription());
		}
		catch (final DescriptionNotFoundException e) {
			descriptionLabel.setText("");
		}
	}

	private Description getDescription() throws DescriptionNotFoundException {
		return CONTEXT_PROVIDER_SERVICE.getCurrent().findDescriptionFor(getCurrentId());
	}

}
