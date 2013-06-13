package br.com.oncast.ontrack.client.ui.generalwidgets.impediment;

import static br.com.oncast.ontrack.client.services.ClientServices.getCurrentProjectContext;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.com.oncast.ontrack.client.services.ClientServices;
import br.com.oncast.ontrack.client.services.actionExecution.ActionExecutionListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.IconTextBox;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetContainer;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.client.ui.generalwidgets.PopupConfig.PopupAware;
import br.com.oncast.ontrack.client.utils.keyboard.BrowserKeyCodes;
import br.com.oncast.ontrack.client.utils.ui.ElementUtils;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.AnnotationAction;
import br.com.oncast.ontrack.shared.model.action.AnnotationCreateAction;
import br.com.oncast.ontrack.shared.model.action.ImpedimentAction;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.annotation.AnnotationType;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.HasUUID;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.actionExecution.ActionExecutionContext;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.HasCloseHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class ImpedimentListWidget extends Composite implements PopupAware, HasCloseHandlers<ImpedimentListWidget>, ActionExecutionListener {

	private static ImpedimentListWidgetUiBinder uiBinder = GWT.create(ImpedimentListWidgetUiBinder.class);

	interface ImpedimentListWidgetUiBinder extends UiBinder<Widget, ImpedimentListWidget> {}

	@UiField
	IconTextBox newImpedimentDescription;

	@UiField
	DivElement noImpedimentsItem;

	@UiField(provided = true)
	ModelWidgetContainer<Annotation, ImpedimentMenuWidget> impedimentsContainer;

	private final UUID subjectId;

	public ImpedimentListWidget(final HasUUID subject) {
		this(subject.getId());
	}

	public ImpedimentListWidget(final UUID subjectId) {
		this.subjectId = subjectId;
		impedimentsContainer = createImpedimentsContainer(subjectId);
		initWidget(uiBinder.createAndBindUi(this));
		update();
	}

	@UiHandler("newImpedimentDescription")
	void onKeyUp(final KeyUpEvent event) {
		event.stopPropagation();
		event.preventDefault();

		if (event.getNativeKeyCode() == BrowserKeyCodes.KEY_ESCAPE) hide();

		final String description = newImpedimentDescription.getText().trim();
		if (event.getNativeKeyCode() != BrowserKeyCodes.KEY_ENTER || description.isEmpty()) return;

		ClientServices.get().actionExecution()
				.onUserActionExecutionRequest(new AnnotationCreateAction(subjectId, AnnotationType.OPEN_IMPEDIMENT, description));
		hide();
	}

	@UiHandler("newImpedimentDescription")
	void onKeyDown(final KeyDownEvent event) {
		event.stopPropagation();
	}

	private void update() {
		final List<Annotation> impediments = getCurrentProjectContext().findImpedimentsFor(subjectId);
		Collections.sort(impediments, new Comparator<Annotation>() {
			@Override
			public int compare(final Annotation o1, final Annotation o2) {
				return o1.getType().compareTo(o2.getType());
			}
		});
		ElementUtils.setVisible(noImpedimentsItem, impediments.isEmpty());
		impedimentsContainer.update(impediments);
	}

	public void setFocus(final boolean focused) {
		newImpedimentDescription.setFocus(focused);
	}

	private ModelWidgetContainer<Annotation, ImpedimentMenuWidget> createImpedimentsContainer(final UUID subjectId) {
		return new ModelWidgetContainer<Annotation, ImpedimentMenuWidget>(new ModelWidgetFactory<Annotation, ImpedimentMenuWidget>() {
			@Override
			public ImpedimentMenuWidget createWidget(final Annotation modelBean) {
				final ImpedimentMenuWidget widget = new ImpedimentMenuWidget(subjectId, modelBean);
				widget.addClickHandler(new ClickHandler() {
					@Override
					public void onClick(final ClickEvent event) {
						hide();
					}
				});
				return widget;
			}
		});
	}

	@Override
	public void show() {
		setFocus(true);
	}

	@Override
	public void hide() {
		if (!this.isVisible()) return;

		newImpedimentDescription.setText("");
		setFocus(false);
		CloseEvent.fire(this, this);
	}

	@Override
	public HandlerRegistration addCloseHandler(final CloseHandler<ImpedimentListWidget> handler) {
		return this.addHandler(handler, CloseEvent.getType());
	}

	@Override
	protected void onLoad() {
		ClientServices.get().actionExecution().addActionExecutionListener(this);
	}

	@Override
	protected void onUnload() {
		ClientServices.get().actionExecution().removeActionExecutionListener(this);
	}

	@Override
	public void onActionExecution(final ModelAction action, final ProjectContext context, final ActionContext actionContext,
			final ActionExecutionContext executionContext,
			final boolean isUserAction) {

		if ((action instanceof AnnotationAction || action instanceof ImpedimentAction) && action.getReferenceId().equals(subjectId)) update();

	}
}
