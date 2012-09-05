package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.oncast.ontrack.client.services.ClientServiceProvider;
import br.com.oncast.ontrack.client.services.annotations.AnnotationService;
import br.com.oncast.ontrack.client.ui.generalwidgets.ModelWidgetFactory;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.annotation.AnnotationType;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;

// FIXME Xiz Trocar o MenuBar e MenuItem para widgets específicos se for o caso
public class AnnotationTopicWidgetFactory implements ModelWidgetFactory<Annotation, AnnotationTopic> {

	private final UUID subjectId;

	public AnnotationTopicWidgetFactory(final UUID subjectId) {
		this.subjectId = subjectId;
	}

	@Override
	public AnnotationTopic createWidget(final Annotation modelBean) {

		// FIXME Xiz Pensar numa forma de iteração entre o AnnotationTopic e os itens do menu customizado
		return new AnnotationTopic(modelBean, subjectId, AnnotationTypeItemsMapper.get(modelBean.getType()).getMenuBar(subjectId, modelBean));
	}

	private static AnnotationService getAnnotationService() {
		return ClientServiceProvider.getInstance().getAnnotationService();
	}

	private enum ItemWidget {

		// FIXME Xiz Talvez o item DEPRECATE seja responsabilidade do AnnotationTopic, já que todos os tipos de anotações possuem estado Deprecado / Válido
		DEPRECATE("Deprecate") {
			@Override
			protected void doCommand(final UUID subjectId, final Annotation annotation) {
				getAnnotationService().deprecateAnnotation(subjectId, annotation.getId());
			}
		},

		COMMENTS("Comments") {
			@Override
			protected void doCommand(final UUID subjectId, final Annotation annotation) {}
		},

		CREATION_TIME("Creation Time") {
			@Override
			protected void doCommand(final UUID subjectId, final Annotation annotation) {}
		},

		MARK_AS_FIXED("Mark as Fixed") {
			@Override
			protected void doCommand(final UUID subjectId, final Annotation annotation) {

			}
		},
		MARK_AS_OPEN("Mark as Open") {
			@Override
			protected void doCommand(final UUID subjectId, final Annotation annotation) {

			}
		},
		LIKE("Like") {
			@Override
			protected void doCommand(final UUID subjectId, final Annotation annotation) {
				if (annotation.hasVoted(ClientServiceProvider.getInstance().getAuthenticationService().getCurrentUser()))
				getAnnotationService().removeVote(subjectId, annotation.getId());
				else
				getAnnotationService().addVote(subjectId, annotation.getId());
			}
		};

		private String body;

		private ItemWidget(final String text) {
			this.body = text;
		}

		// FIXME Xiz Nem todos os itens executam comandos ou reagem ao click, talvez cada item deva retornar um Widget próprio ou segundo alguma interface que
		// seja comum a todos;
		public MenuItem getWidget(final UUID subjectId, final Annotation annotation) {
			return new MenuItem(body, new Command() {
				@Override
				public void execute() {
					doCommand(subjectId, annotation);
				}
			});
		}

		protected abstract void doCommand(UUID subjectId, Annotation annotation);
	}

	private enum AnnotationTypeItemsMapper {
		EMPTY(null),
		SIMPLE(AnnotationType.SIMPLE, ItemWidget.DEPRECATE, ItemWidget.COMMENTS, ItemWidget.CREATION_TIME),
		COMMENT(AnnotationType.COMMENT, ItemWidget.DEPRECATE),
		OPEN_IMPEDIMENT(AnnotationType.OPEN_IMPEDIMENT, ItemWidget.DEPRECATE),
		SOLVED_IMPEDIMENT(AnnotationType.SOLVED_IMPEDIMENT, ItemWidget.DEPRECATE);

		private AnnotationType annotationType;
		private List<ItemWidget> items;

		private AnnotationTypeItemsMapper(final AnnotationType annotationType, final ItemWidget... itemWidgets) {
			this.annotationType = annotationType;
			items = new ArrayList<ItemWidget>(Arrays.asList(itemWidgets));
		}

		public static AnnotationTypeItemsMapper get(final AnnotationType type) {
			for (final AnnotationTypeItemsMapper i : values()) {
				if (i.annotationType == type) return i;
			}
			return EMPTY;
		}

		public MenuBar getMenuBar(final UUID subjectId, final Annotation annotation) {
			final MenuBar menuBar = new MenuBar();
			for (final ItemWidget i : items) {
				menuBar.addItem(i.getWidget(subjectId, annotation));
			}
			return menuBar;
		}

	}

	// <g:FocusPanel ui:field="deprecate" styleName="{style.deprecate} {style.icon} {style.detail}" title="Deprecate"/>
	// <g:FocusPanel ui:field="like" styleName="{style.like} {style.icon} {style.detail}"/>
	// <g:Label ui:field="likeCount" styleName="{style.detail} {style.countLabel}"/>
	// <g:FocusPanel ui:field="comment" styleName="{style.commentIcon} {style.icon} {style.detail}"/>
	// <g:Label ui:field="commentsCount" styleName="{style.detail} {style.countLabel}"/>
	// <g:FocusPanel styleName="{style.clockIcon} {style.icon} {style.detail}"/>
	// <g:Label ui:field="date" styleName="{style.detail} {style.dateLabel}"/>

}
