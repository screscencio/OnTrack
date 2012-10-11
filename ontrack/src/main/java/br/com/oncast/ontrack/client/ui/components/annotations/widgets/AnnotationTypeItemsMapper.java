package br.com.oncast.ontrack.client.ui.components.annotations.widgets;

import br.com.oncast.ontrack.client.ui.components.annotations.widgets.AnnotationTopic.AnnotationTopicStyle;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.menu.AnnotationMenuItem;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.menu.AnnotationMenuWidget;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.menu.CustomDuration;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.menu.ImpedimentAnnotationMenuItem;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.menu.SinceAnnotationMenuItem;
import br.com.oncast.ontrack.client.ui.components.annotations.widgets.menu.SolveImpedimentAnnotationMenuItem;
import br.com.oncast.ontrack.client.utils.date.HumanDateFormatter;
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
			menu.add(new ImpedimentAnnotationMenuItem(subjectId, annotation));
		}
	},
	OPEN_IMPEDIMENT(AnnotationType.OPEN_IMPEDIMENT) {
		@Override
		public void populateMenu(final AnnotationMenuWidget menu, final UUID subjectId, final Annotation annotation) {
			menu.add(new SolveImpedimentAnnotationMenuItem(subjectId, annotation));
		}

		@Override
		public String getContentStyle(final AnnotationTopicStyle style) {
			return style.openImpediment();
		}

		@Override
		public AnnotationMenuItem getSinceWidget(final Annotation annotation) {
			return new SinceAnnotationMenuItem(annotation, new CustomDuration() {

				@Override
				public String getDurationText(final Annotation annotation) {
					return HumanDateFormatter.getDifferenceText(annotation.getCurrentStateDuration());
				}
			});
		}
	},
	SOLVED_IMPEDIMENT(AnnotationType.SOLVED_IMPEDIMENT) {
		@Override
		public void populateMenu(final AnnotationMenuWidget menu, final UUID subjectId, final Annotation annotation) {
			menu.add(new ImpedimentAnnotationMenuItem(subjectId, annotation));
		}

		@Override
		public String getContentStyle(final AnnotationTopicStyle style) {
			return style.solvedImpediment();
		}

		@Override
		public AnnotationMenuItem getSinceWidget(final Annotation annotation) {
			return new SinceAnnotationMenuItem(annotation, new CustomDuration() {

				@Override
				public String getDurationText(final Annotation annotation) {
					return HumanDateFormatter.getDifferenceText(annotation.getDurationOf(AnnotationType.OPEN_IMPEDIMENT));
				}
			});
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

	public AnnotationMenuItem getSinceWidget(final Annotation annotation) {
		return new SinceAnnotationMenuItem(annotation);
	}

}