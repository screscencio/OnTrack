package br.com.oncast.ontrack.client.ui.generalwidgets.details;

import br.com.oncast.ontrack.client.ui.components.ScopeWidget;
import br.com.oncast.ontrack.client.ui.components.members.DraggableMemberWidget;
import br.com.oncast.ontrack.client.ui.generalwidgets.progress.ProgressIcon;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.SpanElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Widget;

public class TaskWidget extends Composite implements ScopeWidget {

	private static TaskWidgetUiBinder uiBinder = GWT.create(TaskWidgetUiBinder.class);

	interface TaskWidgetUiBinder extends UiBinder<Widget, TaskWidget> {}

	interface TaskWidgetStyle extends CssResource {
		String targetHighlight();
	}

	public TaskWidget() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	@UiField
	TaskWidgetStyle style;

	@UiField
	HTMLPanel container;

	@UiField
	SpanElement descriptionLabel;

	@UiField(provided = true)
	ProgressIcon progressIcon;

	private Scope task;

	private boolean targetHighlight = false;

	private TaskWidgetClickListener listener;

	public TaskWidget(final Scope scope, final TaskWidgetClickListener clickListener) {
		this.task = scope;
		this.listener = clickListener;
		progressIcon = new ProgressIcon(task);
		initWidget(uiBinder.createAndBindUi(this));
		update();
	}

	@UiHandler("focusPanel")
	void onClick(final ClickEvent event) {
		listener.onClick(this);
	}

	@Override
	public boolean update() {
		descriptionLabel.setInnerText(task.getDescription());
		progressIcon.update();
		return false;
	}

	@Override
	public Scope getModelObject() {
		return task;
	}

	@Override
	public void setTargetHighlight(final boolean highlighted) {
		container.setStyleName(style.targetHighlight(), highlighted);
		targetHighlight = highlighted;
	}

	@Override
	public void addAssociatedUsers(final DraggableMemberWidget draggable) {}

	@Override
	public boolean isTargetHighlight() {
		return targetHighlight;
	}

	public String getDescription() {
		return task.getDescription();
	}

	public interface TaskWidgetClickListener {
		void onClick(TaskWidget taskWidget);
	}

}
