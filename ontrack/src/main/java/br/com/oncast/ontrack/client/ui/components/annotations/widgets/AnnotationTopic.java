package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class AnnotationTopic extends Composite implements ModelWidget<Annotation> {

	private static AnnotationTopicUiBinder uiBinder = GWT.create(AnnotationTopicUiBinder.class);

	interface AnnotationTopicUiBinder extends UiBinder<Widget, AnnotationTopic> {}

	@UiField
	Label author;

	@UiField
	HTMLPanel message;

	private Annotation annotation;

	public AnnotationTopic() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public AnnotationTopic(final Annotation annotation) {
		this.annotation = annotation;

		initWidget(uiBinder.createAndBindUi(this));
	}

	private String replaceNewLines(final String text) {
		return text.replaceAll("\\n", "<br />");
	}

	@Override
	public boolean update() {
		this.author.setText(annotation.getAuthor().getEmail());
		this.message.clear();
		DOM.setInnerHTML(this.message.getElement(), replaceNewLines(annotation.getMessage()));
		return true;
	}

	@Override
	public Annotation getModelObject() {
		return annotation;
	}

}
