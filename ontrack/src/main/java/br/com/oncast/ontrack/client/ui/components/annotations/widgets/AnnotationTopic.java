package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import java.math.BigInteger;
import java.security.MessageDigest;

import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class AnnotationTopic extends Composite implements ModelWidget<Annotation> {

	private static AnnotationTopicUiBinder uiBinder = GWT.create(AnnotationTopicUiBinder.class);

	interface AnnotationTopicUiBinder extends UiBinder<Widget, AnnotationTopic> {}

	@UiField
	Image author;

	@UiField
	HTMLPanel message;

	private Annotation annotation;

	public AnnotationTopic() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public AnnotationTopic(final Annotation annotation) {
		this.annotation = annotation;

		initWidget(uiBinder.createAndBindUi(this));

		final String email = annotation.getAuthor().getEmail();
		this.author.setUrl(getGravatarImageUrl(email));
		this.author.setTitle(email);
		this.message.clear();
		this.message.getElement().setInnerHTML(replaceNewLines(annotation.getMessage()));
	}

	private SafeUri getGravatarImageUrl(final String email) {
		return new SafeUri() {
			@Override
			public String asString() {
				try {
					final BigInteger hash = new BigInteger(1, MessageDigest.getInstance("MD5").digest(email.trim().toLowerCase().getBytes()));
					final String md5 = hash.toString(16);
					return new String("http://www.gravatar.com/avatar/" + md5 + "?s=40&d=mm");
				}
				catch (final Exception e) {
					return "";
				}
			}
		};
	}

	private String replaceNewLines(final String text) {
		return text.replaceAll("\\n", "<br />");
	}

	@Override
	public boolean update() {
		return false;
	}

	@Override
	public Annotation getModelObject() {
		return annotation;
	}

}
