package br.com.oncast.ontrack.client.ui.generalwidgets.impediment;

import static br.com.oncast.ontrack.shared.model.annotation.AnnotationType.OPEN_IMPEDIMENT;
import static br.com.oncast.ontrack.shared.model.annotation.AnnotationType.SOLVED_IMPEDIMENT;

import java.util.Date;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.ui.components.user.UserWidget;
import br.com.oncast.ontrack.client.ui.components.user.UserWidget.UserUpdateListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.client.utils.date.HumanDateFormatter;
import br.com.oncast.ontrack.shared.model.action.ImpedimentAction;
import br.com.oncast.ontrack.shared.model.action.ImpedimentCreateAction;
import br.com.oncast.ontrack.shared.model.action.ImpedimentSolveAction;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Widget;

public class ImpedimentMenuWidget extends Composite implements HasClickHandlers, ModelWidget<Annotation>, UserUpdateListener {

	private static ImpedimentMenuWidgetUiBinder uiBinder = GWT.create(ImpedimentMenuWidgetUiBinder.class);

	interface ImpedimentMenuWidgetUiBinder extends UiBinder<Widget, ImpedimentMenuWidget> {}

	interface ImpedimentMenuWidgetStyle extends CssResource {
		String deprecatedMessage();

		String deprecatedButton();
	}

	@UiField
	ImpedimentMenuWidgetStyle style;

	@UiField(provided = true)
	UserWidget userWidget;

	@UiField
	InlineHTML message;

	@UiField
	FocusPanel check;

	@UiField
	SpanElement author;

	@UiField
	SpanElement timestamp;

	private final UUID subjectId;

	private final Annotation impediment;

	public ImpedimentMenuWidget(final UUID subjectId, final Annotation impediment) {
		this.subjectId = subjectId;
		this.impediment = impediment;
		userWidget = new UserWidget(impediment.getAuthor()).setUpdateListener(this);
		initWidget(uiBinder.createAndBindUi(this));
		update();
	}

	@UiHandler("check")
	void onCheckClick(final ClickEvent e) {
		if (impediment.isDeprecated()) return;
		final ImpedimentAction action = impediment.getType() == OPEN_IMPEDIMENT ? new ImpedimentSolveAction(subjectId, impediment.getId()) : new ImpedimentCreateAction(subjectId, impediment.getId());

		ClientServices.get().actionExecution().onUserActionExecutionRequest(action);
	}

	@Override
	public boolean update() {
		message.setText(impediment.getMessage());
		final boolean isDeprecated = impediment.isDeprecated();
		message.setStyleName(style.deprecatedMessage(), isDeprecated);
		final Date t = impediment.getLastOcuurenceOf(impediment.getType());
		timestamp.setInnerText(HumanDateFormatter.get().formatDateRelativeToNow(t));
		check.setStyleName("icon-check-empty", !isDeprecated && impediment.getType() == OPEN_IMPEDIMENT);
		check.setStyleName("icon-check", !isDeprecated && impediment.getType() == SOLVED_IMPEDIMENT);
		check.setStyleName("icon-ban", isDeprecated);
		check.setStyleName(style.deprecatedButton(), isDeprecated);
		return false;
	}

	@Override
	public Annotation getModelObject() {
		return impediment;
	}

	@Override
	public void onUserUpdate(final User user) {
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {
			@Override
			public void execute() {
				if (author != null) author.setInnerText(user.getName());
			}
		});
	}

	@Override
	public HandlerRegistration addClickHandler(final ClickHandler handler) {
		return check.addClickHandler(handler);
	}

}
