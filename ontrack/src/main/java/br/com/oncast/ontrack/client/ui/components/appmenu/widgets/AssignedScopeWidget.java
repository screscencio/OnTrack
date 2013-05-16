package br.com.oncast.ontrack.client.ui.components.appmenu.widgets;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ProgressIconUpdater;
import br.com.oncast.ontrack.client.ui.components.releasepanel.widgets.ProgressIconUpdaterStyle;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.PercentualBar;
import br.com.oncast.ontrack.client.ui.generalwidgets.scope.ScopeAssociatedTagsWidget;
import br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat;
import br.com.oncast.ontrack.shared.model.progress.Progress;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class AssignedScopeWidget extends Composite implements HasClickHandlers, ModelWidget<Scope> {

	private static AssignedScopeWidgetUiBinder uiBinder = GWT.create(AssignedScopeWidgetUiBinder.class);

	interface AssignedScopeWidgetUiBinder extends UiBinder<Widget, AssignedScopeWidget> {}

	interface AssignedScopeWidgetStyle extends ProgressIconUpdaterStyle {
		String story();
	}

	@UiField
	AssignedScopeWidgetStyle style;

	@UiField
	FocusPanel panel;

	@UiField
	SpanElement humanIdLabel;

	@UiField
	SpanElement descriptionLabel;

	@UiField
	Label effortLabel;

	@UiField
	FocusPanel progressIcon;

	@UiField(provided = true)
	ScopeAssociatedTagsWidget tags;

	@UiField
	PercentualBar percentualBar;

	private final Scope scope;

	public AssignedScopeWidget(final Scope scope) {
		this.scope = scope;
		tags = new ScopeAssociatedTagsWidget(scope);
		initWidget(uiBinder.createAndBindUi(this));

		update();
	}

	@Override
	public boolean update() {
		panel.setStyleName(style.story(), scope.hasRelease());
		updateTags();
		updateDueDate();
		return updateHumanId() | updateDescription() | updateProgress() | updateTitle() | updateValues();
	}

	private void updateDueDate() {
		final String color = ClientServices.get().colorProvider().getDueDateColor(scope).toCssRepresentation();
		panel.getElement().getStyle().setBackgroundColor(color);
	}

	private void updateTags() {
		tags.update();
	}

	private boolean updateTitle() {
		final String title = buildLineageRepresentationText();
		if (title.isEmpty() || title.equals(panel.getTitle())) return false;

		descriptionLabel.setTitle(title);
		return true;
	}

	private boolean updateValues() {
		final float inferedEffort = scope.getEffort().getInfered();
		final String effortStr = ClientDecimalFormat.roundFloat(inferedEffort, 1);
		effortLabel.setText(effortStr);
		effortLabel.setTitle(effortStr + " effort points");
		percentualBar.setPercentual(calculatePercentual(scope));

		return true;
	}

	private int calculatePercentual(final Scope scope) {
		if (scope.getProgress().isDone()) return 100;
		return (int) (scope.getEffort().getAccomplishedPercentual());
	}

	private String buildLineageRepresentationText() {
		if (scope.isRoot()) return "";

		final StringBuilder builder = new StringBuilder();
		Scope current = scope;
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
	 * @return if the humanId was updated.
	 */
	private boolean updateHumanId() {
		final String humanId = ClientServices.getCurrentProjectContext().getHumanId(scope);
		humanIdLabel.setInnerHTML(humanId);

		if (!humanId.isEmpty()) humanIdLabel.getStyle().clearVisibility();
		else {
			humanIdLabel.setInnerHTML(ClientServices.getCurrentProjectContext().getHumanId(scope.getStory()));
			humanIdLabel.getStyle().setVisibility(Visibility.HIDDEN);
		}
		return true;
	}

	/**
	 * @return if the description was updated.
	 */
	private boolean updateDescription() {
		descriptionLabel.setInnerHTML(scope.getDescription());
		return true;
	}

	private boolean updateProgress() {
		final Progress progress = scope.getProgress();
		final String description = progress.getDescription();
		final boolean hasOpenImpediments = ClientServices.get().details().hasOpenImpediment(scope.getId());

		final ProgressIconUpdater updater = ProgressIconUpdater.getUpdater(scope, hasOpenImpediments);
		progressIcon.setStyleName(updater.getStyle(style));
		progressIcon.setTitle(updater.getTitle(scope));
		if (!description.isEmpty()) updater.animate(panel);
		return true;
	}

	@Override
	public Scope getModelObject() {
		return scope;
	}

	@Override
	public HandlerRegistration addClickHandler(final ClickHandler handler) {
		return panel.addClickHandler(handler);
	}

}
