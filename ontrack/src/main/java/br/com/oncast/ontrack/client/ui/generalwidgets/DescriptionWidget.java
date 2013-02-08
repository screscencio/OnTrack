package br.com.oncast.ontrack.client.ui.generalwidgets;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionService;
import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.shared.model.action.DescriptionCreateAction;
import br.com.oncast.ontrack.shared.model.description.Description;
import br.com.oncast.ontrack.shared.model.description.exceptions.DescriptionNotFoundException;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class DescriptionWidget extends Composite {

	private static final ClientServiceProvider SERVICE_PROVIDER = ClientServiceProvider.getInstance();
	private static final ContextProviderService CONTEXT_PROVIDER_SERVICE = SERVICE_PROVIDER.getContextProviderService();
	private static final DescriptionWidgetMessages MESSAGES = GWT.create(DescriptionWidgetMessages.class);

	private static DescriptionWidgetUiBinder uiBinder = GWT.create(DescriptionWidgetUiBinder.class);

	interface DescriptionWidgetUiBinder extends UiBinder<Widget, DescriptionWidget> {}

	@UiField
	Label label;

	@UiField(provided = true)
	DescriptionRichTextLabel descriptionLabel;

	private final Release release;

	private Scope scope;

	public DescriptionWidget(final Release release) {
		this.release = release;
		descriptionLabel = new DescriptionRichTextLabel(new EditableLabelEditionHandler() {

			@Override
			public boolean onEditionRequest(final String text) {
				final ActionExecutionService actionExecutionService = SERVICE_PROVIDER.getActionExecutionService();

				actionExecutionService.onUserActionExecutionRequest(new DescriptionCreateAction(getCurrentId(), text));

				return true;
			}
		});
		initWidget(uiBinder.createAndBindUi(this));
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
		label.setText(MESSAGES.descriptionOf(getCurrentTitle()));
		descriptionLabel.setText("");
		final Description description = CONTEXT_PROVIDER_SERVICE.getCurrent().findDescriptionFor(getCurrentId());
		descriptionLabel.setText(description.getDescription());
	}

}
