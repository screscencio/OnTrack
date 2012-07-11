package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.annotations.AnnotationService;
import br.com.oncast.ontrack.client.services.user.PortableContactJsonObject;
import br.com.oncast.ontrack.client.services.user.UserDataService.LoadProfileCallback;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.AnnotationsWidget.UpdateListener;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidget;
import br.com.oncast.ontrack.client.utils.date.HumanDateFormatter;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.shared.model.user.User;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.RepeatingCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiFactory;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class AnnotationTopic extends Composite implements ModelWidget<Annotation> {

	private static final int TIME_REFRESH_INTERVAL = 3000;

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
	AnnotationsWidget commentsPanel;

	@UiField
	Label date;

	@UiField
	FocusPanel remove;

	@UiField
	Label likeCount;

	@UiField
	FocusPanel like;

	@UiField
	Label commentsCount;

	@UiField
	FocusPanel comment;

	private Annotation annotation;

	private UUID subjectId;

	private boolean shouldRefresh = true;

	@UiFactory
	protected AnnotationsWidget createCommentsPainel() {
		return AnnotationsWidget.forComments();
	}

	public AnnotationTopic() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public AnnotationTopic(final Annotation annotation, final UUID subjectId, final boolean enableComments) {
		this.annotation = annotation;
		this.subjectId = subjectId;

		initWidget(uiBinder.createAndBindUi(this));

		setupDeleteButton();
		setupContent();
		setupCommentsPanel(enableComments);
		update();
	}

	private void setupDeleteButton() {
		remove.setVisible(annotation.getAuthor().equals(getCurrentUser()));
	}

	@UiHandler("remove")
	protected void onDeleteClicked(final ClickEvent e) {
		getAnnotationService().deleteAnnotation(subjectId, annotation.getId());
	}

	@UiHandler("like")
	protected void onLikeClicked(final ClickEvent e) {
		getAnnotationService().toggleVote(subjectId, annotation.getId());
	}

	@Override
	protected void onLoad() {
		shouldRefresh = true;
		Scheduler.get().scheduleFixedDelay(new RepeatingCommand() {

			@Override
			public boolean execute() {
				updateTime();
				return shouldRefresh;
			}
		}, TIME_REFRESH_INTERVAL);
	}

	@Override
	protected void onUnload() {
		shouldRefresh = false;
	}

	private void setupContent() {
		final String email = this.annotation.getAuthor().getEmail();
		this.container.clear();
		this.author.setUrl(ClientServiceProvider.getInstance().getUserDataService().getAvatarUrl(email));

		ClientServiceProvider.getInstance().getUserDataService().loadProfile(email, new LoadProfileCallback() {
			@Override
			public void onProfileLoaded(final PortableContactJsonObject profile) {
				author.setTitle(profile.getPreferedUsername());
			}

			@Override
			public void onProfileUnavailable(final Throwable cause) {
				author.setTitle(email);
			}
		});

		container.add(author);
		final FileRepresentation attachmentFile = annotation.getAttachmentFile();
		if (attachmentFile != null) {
			final AttachmentFileWidget attachedFileWidget = new AttachmentFileWidget(attachmentFile);
			container.add(attachedFileWidget);
		}

		for (final String line : this.annotation.getMessage().split("\\n")) {
			container.add(new HTMLPanel(SimpleHtmlSanitizer.sanitizeHtml(line)));
		}
	}

	private void setupCommentsPanel(final boolean enableComments) {
		if (enableComments) {
			commentsPanel.setSubjectId(annotation.getId());
			comment.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(final ClickEvent event) {
					commentsPanel.setVisible(!commentsPanel.isVisible());
					commentsPanel.setFocus(true);
				}
			});
			commentsPanel.setUpdateListener(new UpdateListener() {
				@Override
				public void onChanged() {
					updateComment();
				}
			});
			commentsPanel.setVisible(commentsPanel.getWidgetCount() > 0);
		}
		else {
			comment.setVisible(false);
			commentsCount.setVisible(false);
			commentsPanel.setVisible(false);
		}
	}

	private String getDateTimeText(final Annotation annotation) {
		return HumanDateFormatter.getRelativeDate(annotation.getDate()) + " (" + HumanDateFormatter.getDifferenceDate(annotation.getDate()) + ")";
	}

	@Override
	public boolean update() {
		updateLike();
		updateComment();
		updateTime();
		return false;
	}

	private void updateComment() {
		final String previousCount = this.commentsCount.getText();
		final String currentCount = "" + commentsPanel.getWidgetCount();
		this.commentsCount.setText(currentCount);
		if (currentCount.compareTo(previousCount) > 0 && !currentCount.equals("0")) commentsPanel.setVisible(true);

		this.comment.setTitle(commentsPanel.isVisible() ? "Hide Comments" : "Show Comments");
	}

	public void updateTime() {
		this.date.setText(getDateTimeText(annotation));
		this.date.setTitle(HumanDateFormatter.getAbsoluteText(annotation.getDate()));
	}

	private void updateLike() {
		final boolean hasLiked = hasLiked();
		this.like.setStyleName(style.likeActive(), hasLiked);
		this.like.setTitle(hasLiked ? "Remove like" : "Like");

		this.likeCount.setText("" + annotation.getVoteCount());
	}

	private boolean hasLiked() {
		return annotation.hasVoted(getCurrentUser().getEmail());
	}

	private AnnotationService getAnnotationService() {
		return ClientServiceProvider.getInstance().getAnnotationService();
	}

	private User getCurrentUser() {
		return ClientServiceProvider.getInstance().getAuthenticationService().getCurrentUser();
	}

	@Override
	public Annotation getModelObject() {
		return annotation;
	}

}
