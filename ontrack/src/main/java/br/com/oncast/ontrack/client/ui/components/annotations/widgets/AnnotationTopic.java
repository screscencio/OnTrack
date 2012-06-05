package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import java.math.BigInteger;
import java.security.MessageDigest;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.client.utils.date.HumanDateFormatter;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SafeUri;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class AnnotationTopic extends Composite implements ModelWidget<Annotation> {

	private static AnnotationTopicUiBinder uiBinder = GWT.create(AnnotationTopicUiBinder.class);

	interface AnnotationTopicUiBinder extends UiBinder<Widget, AnnotationTopic> {}

	interface AnnotationTopicStyle extends CssResource {
		public String likeActive();
	}

	@UiField
	AnnotationTopicStyle style;

	@UiField
	Image author;

	@UiField
	HTMLPanel container;

	@UiField
	Label date;

	@UiField
	Label likeCount;

	@UiField
	FocusPanel likeIcon;

	private Annotation annotation;

	private UUID subjectId;

	public AnnotationTopic() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public AnnotationTopic(final Annotation annotation, final UUID subjectId) {
		this.annotation = annotation;
		this.subjectId = subjectId;

		initWidget(uiBinder.createAndBindUi(this));

		final String email = annotation.getAuthor().getEmail();
		this.container.clear();
		this.author.setUrl(getGravatarImageUrl(email));
		this.author.setTitle(email);
		container.add(author);

		for (final String line : annotation.getMessage().split("\\n")) {
			container.add(new Label(line));
		}

		update();
	}

	@UiHandler("likeIcon")
	protected void onClick(final ClickEvent e) {
		ClientServiceProvider.getInstance().getAnnotationService().toggleVote(annotation.getId(), subjectId);
	}

	private String getDateText(final Annotation annotation) {
		return HumanDateFormatter.getRelativeText(annotation.getDate()) + " (" + HumanDateFormatter.getDifferenceText(annotation.getDate()) + ")";
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

	@Override
	public boolean update() {
		updateLike();
		updateDate();
		return false;
	}

	private void updateDate() {
		this.date.setText(getDateText(annotation));
		this.date.setTitle(HumanDateFormatter.getAbsoluteText(annotation.getDate()));
	}

	private void updateLike() {
		final boolean hasLiked = hasLiked();
		this.likeIcon.setStyleName(style.likeActive(), hasLiked);
		this.likeIcon.setTitle(hasLiked ? "Remove like" : "Like");

		this.likeCount.setText("" + annotation.getVoteCount());
	}

	private boolean hasLiked() {
		return annotation.hasVoted(getCurrentUser().getEmail());
	}

	private User getCurrentUser() {
		return ClientServiceProvider.getInstance().getAuthenticationService().getCurrentUser();
	}

	@Override
	public Annotation getModelObject() {
		return annotation;
	}

}
