package br.com.oncast.ontrack.client.ui.generalwidgets.impediment;

import static br.com.oncast.ontrack.shared.model.annotation.AnnotationType.OPEN_IMPEDIMENT;

import java.util.Date;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.components.user.UserWidget;
import br.com.oncast.ontrack.client.ui.components.user.UserWidget.UserUpdateListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.client.utils.date.HumanDateFormatter;
import br.com.oncast.ontrack.shared.model.action.ImpedimentAction;
import br.com.oncast.ontrack.shared.model.action.ImpedimentCreateAction;
import br.com.oncast.ontrack.shared.model.action.ImpedimentSolveAction;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.HasUUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.InlineHTML;
import com.google.gwt.user.client.ui.Widget;

public class ImpedimentMenuWidget extends Composite implements ModelWidget<Annotation>, UserUpdateListener {

	private static ImpedimentMenuWidgetUiBinder uiBinder = GWT.create(ImpedimentMenuWidgetUiBinder.class);

	interface ImpedimentMenuWidgetUiBinder extends UiBinder<Widget, ImpedimentMenuWidget> {}

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

	private final HasUUID subject;

	private final Annotation impediment;

	public ImpedimentMenuWidget(final HasUUID subject, final Annotation impediment) {
		this.subject = subject;
		this.impediment = impediment;
		userWidget = new UserWidget(impediment.getAuthor(), this);
		initWidget(uiBinder.createAndBindUi(this));
		update();
	}

	@UiHandler("check")
	void onCheckClick(final ClickEvent e) {
		final ImpedimentAction action =
				impediment.getType() == OPEN_IMPEDIMENT ?
						new ImpedimentSolveAction(subject.getId(), impediment.getId()) :
						new ImpedimentCreateAction(subject.getId(), impediment.getId());

		ClientServiceProvider.getInstance().getActionExecutionService().onUserActionExecutionRequest(action);
	}

	@Override
	public boolean update() {
		message.setText(impediment.getMessage());
		final Date t = impediment.getLastOcuurenceOf(impediment.getType());
		timestamp.setInnerText(HumanDateFormatter.getRelativeDate(t));
		check.setStyleName("icon-check-empty", impediment.getType() == OPEN_IMPEDIMENT);
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

}
