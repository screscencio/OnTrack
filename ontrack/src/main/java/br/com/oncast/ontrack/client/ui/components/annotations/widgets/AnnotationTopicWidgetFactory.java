package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.annotations.AnnotationService;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

// FIXME Xiz Trocar o MenuBar e MenuItem para widgets específicos se for o caso
public class AnnotationTopicWidgetFactory implements ModelWidgetFactory<Annotation, AnnotationTopic> {

	private final UUID subjectId;

	public AnnotationTopicWidgetFactory(final UUID subjectId) {
		this.subjectId = subjectId;
	}

	@Override
	public AnnotationTopic createWidget(final Annotation modelBean) {

		return new AnnotationTopic(subjectId, modelBean);
	}

	private static AnnotationService getAnnotationService() {
		return ClientServiceProvider.getInstance().getAnnotationService();
	}

	// <g:FocusPanel ui:field="deprecate" styleName="{style.deprecate} {style.icon} {style.detail}" title="Deprecate"/>
	// <g:FocusPanel ui:field="like" styleName="{style.like} {style.icon} {style.detail}"/>
	// <g:Label ui:field="likeCount" styleName="{style.detail} {style.countLabel}"/>
	// <g:FocusPanel ui:field="comment" styleName="{style.commentIcon} {style.icon} {style.detail}"/>
	// <g:Label ui:field="commentsCount" styleName="{style.detail} {style.countLabel}"/>
	// <g:FocusPanel styleName="{style.clockIcon} {style.icon} {style.detail}"/>
	// <g:Label ui:field="date" styleName="{style.detail} {style.dateLabel}"/>

}
