package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import br.com.oncast.ontrack.client.ui.components.annotations.widgets.AnnotationTopic.AnnotationTopicStyle;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.menu.AnnotationMenuWidget;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.menu.ImpedimentWidget;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.menu.LikeWidget;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.menu.SinceWidget;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.menu.SolveImpedimentWidget;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.annotation.AnnotationType;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public enum AnnotationTypeItemsMapper {
	EMPTY(null) {
		@Override
		public void populateMenu(final AnnotationMenuWidget menu, final UUID subjectId, final Annotation annotation) {}
	},
	SIMPLE(AnnotationType.SIMPLE) {
		@Override
		public void populateMenu(final AnnotationMenuWidget menu, final UUID subjectId, final Annotation annotation) {
			menu.add(new ImpedimentWidget(subjectId, annotation));
			menu.add(new LikeWidget(subjectId, annotation));
			menu.add(new SinceWidget(annotation));
		}
	},
	COMMENT(AnnotationType.COMMENT) {
		@Override
		public void populateMenu(final AnnotationMenuWidget menu, final UUID subjectId, final Annotation annotation) {
			menu.add(new LikeWidget(subjectId, annotation));
			menu.add(new SinceWidget(annotation));
		}
	},
	OPEN_IMPEDIMENT(AnnotationType.OPEN_IMPEDIMENT) {
		@Override
		public void populateMenu(final AnnotationMenuWidget menu, final UUID subjectId, final Annotation annotation) {
			menu.add(new SolveImpedimentWidget(subjectId, annotation));
			menu.add(new LikeWidget(subjectId, annotation));
			menu.add(new SinceWidget(annotation));
		}

		@Override
		public String getContentStyle(final AnnotationTopicStyle style) {
			return style.openImpediment();
		}
	},
	SOLVED_IMPEDIMENT(AnnotationType.SOLVED_IMPEDIMENT) {
		@Override
		public void populateMenu(final AnnotationMenuWidget menu, final UUID subjectId, final Annotation annotation) {
			menu.add(new ImpedimentWidget(subjectId, annotation));
			menu.add(new LikeWidget(subjectId, annotation));
			menu.add(new SinceWidget(annotation));
		}

		@Override
		public String getContentStyle(final AnnotationTopicStyle style) {
			return style.solvedImpediment();
		}
	};

	private AnnotationType annotationType;

	private AnnotationTypeItemsMapper(final AnnotationType annotationType) {
		this.annotationType = annotationType;
	}

	public static AnnotationTypeItemsMapper get(final AnnotationType type) {
		for (final AnnotationTypeItemsMapper i : values()) {
			if (i.annotationType == type) return i;
		}
		return EMPTY;
	}

	public abstract void populateMenu(AnnotationMenuWidget menu, final UUID subjectId, final Annotation annotation);

	public String getContentStyle(final AnnotationTopicStyle style) {
		return style.simple();
	}

}