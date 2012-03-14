package br.com.oncast.ontrack.client.ui.generalwidgets;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.UIObject;

public class AlignmentConfiguration {
	private final AlignmentOrientation orientation;
	private final AlignmentDirection direction;
	private final AlignmentStyleAttribute styleAttribute;
	private final AlignmentAnchor anchor;

	AlignmentConfiguration(final AlignmentOrientation orientation, final AlignmentDirection direction,
			final AlignmentStyleAttribute styleAttribute,
			final AlignmentAnchor anchor) {
		this.orientation = orientation;
		this.direction = direction;
		this.styleAttribute = styleAttribute;
		this.anchor = anchor;
	}

	/**
	 * Fixes the edge of the given widget to the given position in the Window.<br>
	 * if the widget grows he will grow to the opposite size of the alignment
	 * @param widget to be aligned
	 * @param The absolute position of the screen that it should be positioned. (0, 0) is the Top-Left corner of the Window
	 */
	public void setPosition(final UIObject widget, final double position) {
		final Element element = widget.getElement();
		final int anchorOffset = anchor.getOffsetOf(orientation, element);
		styleAttribute.setPositionTo(element, direction.convertForStyling(direction.addOffset(position, -anchorOffset), orientation));
	}

	/**
	 * Gets the absolute position of the widget's edge. (0, 0) is the Top-Left corner of the Window.
	 */
	public int getPosition(final UIObject widget) {
		final Element element = widget.getElement();
		return (int) direction.addOffset(styleAttribute.getPositionOf(element), anchor.getOffsetOf(orientation, element));
	}

	/**
	 * Same as {@link #setPosition(UIObject, double)} but don't let the widget go beyond the window's edges.<br>
	 * if the widget passes over some edge of the Window, the widget's edge is fixed to the passed Window edge. <br>
	 * if the widget is bigger then the Window it fixes the widget's edge to the same side edge of the Window and let the widget go beyond the other side
	 * @param widget to be aligned
	 * @param The absolute position of the screen that it should be positioned. (0,0) is Top-Left corner of the Window
	 * @param the offset of the widget, positive offset moves the widget to Bottom/Right side
	 */
	public void setVisiblePosition(final UIObject widget, final double desiredPosition, final double offset) {
		final Element element = widget.getElement();
		final double desiredPosWithOffset = direction.addOffset(desiredPosition, offset);

		final int anchorOffset = anchor.getOffsetOf(orientation, element);

		final double beginningEdgePosition = direction.addOffset(desiredPosWithOffset, -anchorOffset);
		if (!direction.fitsBeginning(beginningEdgePosition, orientation)) {
			setPosition(widget, direction.addOffset(direction.getMinimunPosition(orientation), anchorOffset));
			return;
		}

		final int remainingLength = orientation.getLengthOf(element) - anchorOffset;
		final double endEdgePosition = direction.addOffset(desiredPosWithOffset, remainingLength);

		if (!direction.fitsEnd(endEdgePosition, orientation)) {
			setPosition(widget, direction.addOffset(direction.getMaximunPosition(orientation), -remainingLength));
			return;
		}

		setPosition(widget, desiredPosWithOffset);
	}

	enum AlignmentOrientation {
		HORIZONTAL {
			@Override
			int getMaxLength() {
				return Window.getClientWidth();
			}

			@Override
			int getLengthOf(final Element element) {
				return element.getOffsetWidth();
			}
		},
		VERTICAL {
			@Override
			int getMaxLength() {
				return Window.getClientHeight();
			}

			@Override
			int getLengthOf(final Element element) {
				return element.getOffsetHeight();
			}
		};

		abstract int getMaxLength();

		abstract int getLengthOf(final Element element);
	}

	enum AlignmentStyleAttribute {
		LEFT {
			@Override
			void setPositionTo(final Element element, final double value) {
				element.getStyle().clearRight();
				element.getStyle().setLeft(value, UNIT);
			}

			@Override
			int getPositionOf(final Element element) {
				return element.getAbsoluteLeft();
			}
		},
		RIGHT {
			@Override
			public void setPositionTo(final Element element, final double value) {
				element.getStyle().clearLeft();
				element.getStyle().setRight(value, UNIT);
			}

			@Override
			public int getPositionOf(final Element element) {
				return element.getAbsoluteRight();
			}
		},
		TOP {
			@Override
			public void setPositionTo(final Element element, final double value) {
				element.getStyle().clearBottom();
				element.getStyle().setTop(value, UNIT);
			}

			@Override
			public int getPositionOf(final Element element) {
				return element.getAbsoluteTop();
			}
		},
		BOTTOM {
			@Override
			public void setPositionTo(final Element element, final double value) {
				element.getStyle().clearTop();
				element.getStyle().setBottom(value, UNIT);
			}

			@Override
			public int getPositionOf(final Element element) {
				return element.getAbsoluteBottom();
			}
		};

		private static final Unit UNIT = Unit.PX;

		abstract void setPositionTo(final Element element, final double value);

		abstract int getPositionOf(final Element element);
	}

	enum AlignmentAnchor {
		EDGE {
			@Override
			int getOffsetOf(final AlignmentOrientation orientation, final Element element) {
				return 0;
			}
		},
		MIDDLE {
			@Override
			int getOffsetOf(final AlignmentOrientation orientation, final Element element) {
				return orientation.getLengthOf(element) / 2;
			}
		};

		abstract int getOffsetOf(final AlignmentOrientation orientation, final Element element);
	}

	enum AlignmentDirection {
		POSITIVE {
			@Override
			double addOffset(final double position, final double offset) {
				return position + offset;
			}

			@Override
			int getMaximunPosition(final AlignmentOrientation orientation) {
				return orientation.getMaxLength();
			}

			@Override
			int getMinimunPosition(final AlignmentOrientation orientation) {
				return 0;
			}

			@Override
			boolean fitsBeginning(final double desiredPosition, final AlignmentOrientation orientation) {
				return desiredPosition >= getMinimunPosition(orientation);
			}

			@Override
			boolean fitsEnd(final double desiredPosition, final AlignmentOrientation orientation) {
				return desiredPosition <= getMaximunPosition(orientation);
			}

			@Override
			double convertForStyling(final double position, final AlignmentOrientation orientation) {
				return position;
			}
		},
		NEGATIVE {
			@Override
			protected double addOffset(final double position, final double offset) {
				return position - offset;
			}

			@Override
			int getMaximunPosition(final AlignmentOrientation orientation) {
				return 0;
			}

			@Override
			int getMinimunPosition(final AlignmentOrientation orientation) {
				return orientation.getMaxLength();
			}

			@Override
			boolean fitsBeginning(final double desiredPosition, final AlignmentOrientation orientation) {
				return desiredPosition <= getMinimunPosition(orientation);
			}

			@Override
			boolean fitsEnd(final double desiredPosition, final AlignmentOrientation orientation) {
				return desiredPosition >= getMaximunPosition(orientation);
			}

			@Override
			double convertForStyling(final double position, final AlignmentOrientation orientation) {
				return orientation.getMaxLength() - position;
			}
		};

		abstract double convertForStyling(double position, AlignmentOrientation orientation);

		abstract boolean fitsBeginning(double desiredPosition, AlignmentOrientation orientation);

		abstract boolean fitsEnd(double desiredPosition, AlignmentOrientation orientation);

		abstract int getMaximunPosition(AlignmentOrientation orientation);

		abstract int getMinimunPosition(AlignmentOrientation orientation);

		abstract double addOffset(final double position, final double offset);

	}
}