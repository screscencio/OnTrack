package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class AnnotationTopic extends Composite {

	private static AnnotationTopicUiBinder uiBinder = GWT.create(AnnotationTopicUiBinder.class);

	interface AnnotationTopicUiBinder extends UiBinder<Widget, AnnotationTopic> {}

	@UiField
	Label author;

	@UiField
	HTMLPanel message;

	public AnnotationTopic() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public AnnotationTopic(final String author, final String message) {
		this();
		this.author.setText(author);
		this.message.clear();
		DOM.setInnerHTML(this.message.getElement(), replaceNewLines(message));
	}

	private String replaceNewLines(final String text) {
		return text.replaceAll("\\n", "<br />");
	}

}
