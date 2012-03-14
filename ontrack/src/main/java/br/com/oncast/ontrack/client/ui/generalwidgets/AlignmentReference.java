package br.com.oncast.ontrack.client.ui.generalwidgets;

import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentConfiguration.AlignmentAnchor;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentConfiguration.AlignmentDirection;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentConfiguration.AlignmentOrientation;
import br.com.oncast.ontrack.client.ui.generalwidgets.AlignmentConfiguration.AlignmentStyleAttribute;

import com.google.gwt.user.client.ui.UIObject;

public class AlignmentReference {

	private final UIObject referenceWidget;
	private final Alignment referenceAlignment;
	private final int referenceOffset;

	public AlignmentReference(final UIObject referenceWidget, final Alignment alignment) {
		this(referenceWidget, alignment, 0);
	}

	public AlignmentReference(final UIObject widget, final Alignment alignment, final int offset) {
		this.referenceWidget = widget;
		this.referenceAlignment = alignment;
		this.referenceOffset = offset;
	}

	public void align(final UIObject widget, final Alignment alignment) {
		final int desiredPos = referenceAlignment.getConfiguration().getPosition(referenceWidget);
		alignment.getConfiguration().setVisiblePosition(widget, desiredPos, referenceOffset);
	}

	public interface Alignment {
		public AlignmentConfiguration getConfiguration();
	}

	public enum HorizontalAlignment implements Alignment {
		LEFT(AlignmentStyleAttribute.LEFT, AlignmentDirection.POSITIVE, AlignmentAnchor.EDGE),
		RIGHT(AlignmentStyleAttribute.RIGHT, AlignmentDirection.NEGATIVE, AlignmentAnchor.EDGE),
		HORIZONTAL_CENTER(AlignmentStyleAttribute.LEFT, AlignmentDirection.POSITIVE, AlignmentAnchor.MIDDLE);

		private final AlignmentConfiguration configuration;

		private HorizontalAlignment(final AlignmentStyleAttribute styleAttribute, final AlignmentDirection direction, final AlignmentAnchor anchor) {
			configuration = new AlignmentConfiguration(AlignmentOrientation.HORIZONTAL, direction, styleAttribute, anchor);
		}

		@Override
		public AlignmentConfiguration getConfiguration() {
			return configuration;
		}
	}

	public enum VerticalAlignment implements Alignment {
		TOP(AlignmentStyleAttribute.TOP, AlignmentDirection.POSITIVE, AlignmentAnchor.EDGE),
		BOTTOM(AlignmentStyleAttribute.BOTTOM, AlignmentDirection.NEGATIVE, AlignmentAnchor.EDGE),
		VERTICAL_CENTER(AlignmentStyleAttribute.TOP, AlignmentDirection.POSITIVE, AlignmentAnchor.MIDDLE);

		private final AlignmentConfiguration configuration;

		private VerticalAlignment(final AlignmentStyleAttribute styleAttribute, final AlignmentDirection direction, final AlignmentAnchor anchor) {
			configuration = new AlignmentConfiguration(AlignmentOrientation.VERTICAL, direction, styleAttribute, anchor);
		}

		@Override
		public AlignmentConfiguration getConfiguration() {
			return configuration;
		}
	}

}
