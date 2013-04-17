package br.com.oncast.ontrack.client;

import com.google.gwt.dom.client.Element;

public class WidgetVisibilityEnsurer {

	/**
	 * This method is convenient method to scroll the container trying to show more area as possible of the given element with margin = 0.
	 * @param element to be shown
	 * @param container to be scrolled
	 * @param orientation, the scroll orientation
	 * @param alignment defines the element position related to the container
	 * @return true if the element became completely visible, false otherwise
	 */
	public static boolean ensureVisible(final Element element, final Element container, final Orientation orientation) {
		return ensureVisible(element, container, orientation, 0);
	}

	/**
	 * This method is convenient method to scroll the container trying to show more area as possible of the given element
	 * @param element to be shown
	 * @param container to be scrolled
	 * @param orientation, the scroll orientation
	 * @param alignment defines the element position related to the container
	 * @param margin defines the margin of the element after aligned.
	 * @return true if the element became completely visible, false otherwise;
	 */
	public static boolean ensureVisible(final Element element, final Element container, final Orientation orientation, final int margin) {
		return ensureVisible(element, container, orientation, ContainerAlignment.BEGIN, ContainerAlignment.END, margin, margin);
	}

	/**
	 * This method scrolls the container trying to show more area as possible of the given element
	 * @param element to be shown
	 * @param container to be scrolled it's important that the given container has some padding or position set to relative
	 * @param orientation, the scroll orientation
	 * @param alignmentWhenElementWasBefore defines the element position related to the container when the element is hidden before the current visible area of
	 *            the container
	 * @param alignmentWhenElementWasAfter defines the element position related to the container when the element is hidden after the current visible area of
	 *            the container
	 * @param marginWhenElementWasBefore defines how much more margin the element gets after aligned.
	 * @param marginWhenElementWasAfter defines how much more margin the element gets after aligned.
	 * @return true if the element became completely visible, false otherwise;
	 */
	public static boolean ensureVisible(
			final Element element, final Element container,
			final Orientation orientation,
			final ContainerAlignment alignmentWhenElementWasBefore,
			final ContainerAlignment alignmentWhenElementWasAfter,
			final int marginWhenElementWasBefore,
			final int marginWhenElementWasAfter) {

		final int scrollStart = orientation.getScroll(container);
		final int scrollLength = orientation.getLength(container);
		final int scrollEnd = scrollStart + scrollLength;

		final int elementStart = getOffsetSum(element, container, orientation);
		final int elementLength = orientation.getLength(element);
		final int elementEnd = elementStart + elementLength;

		if (elementStart > scrollStart && elementEnd < scrollEnd) return elementLength <= scrollLength;

		if (elementStart < scrollStart || elementLength > scrollLength) orientation.setScroll(container,
				alignmentWhenElementWasBefore.getScroll(elementStart, elementLength, scrollLength) - marginWhenElementWasBefore);
		else if (elementEnd > scrollEnd) orientation.setScroll(container,
				alignmentWhenElementWasAfter.getScroll(elementStart, elementLength, scrollLength) + marginWhenElementWasAfter);

		return elementLength <= scrollLength;
	}

	private static int getOffsetSum(final Element widget, final Element container, final Orientation orientation) {
		final Element parent = widget.getOffsetParent();
		if (parent == null) return 0;

		if (parent == container) return orientation.getOffset(widget);

		return getOffsetSum(parent, container, orientation) + orientation.getOffset(widget);
	}

	public enum ContainerAlignment {
		BEGIN {
			@Override
			protected int getScroll(final int elementOffset, final int elementLength, final int containerLength) {
				return elementOffset;
			}
		},
		CENTER {
			@Override
			protected int getScroll(final int elementOffset, final int elementLength, final int containerLength) {
				return elementOffset - containerLength / 2 + elementLength / 2;
			}
		},
		END {
			@Override
			protected int getScroll(final int elementOffset, final int elementLength, final int containerLength) {
				return elementOffset - containerLength + elementLength;
			}
		};

		protected abstract int getScroll(int elementOffset, int elementLength, int containerLength);
	}

	public enum Orientation {
		HORIZONTAL {
			@Override
			protected int getOffset(final Element e) {
				return e.getOffsetLeft();
			}

			@Override
			protected int getLength(final Element e) {
				return e.getOffsetWidth();
			}

			@Override
			protected int getScroll(final Element e) {
				return e.getScrollLeft();
			}

			@Override
			protected void setScroll(final Element e, final int pos) {
				e.setScrollLeft(pos);
			}
		},
		VERTICAL {
			@Override
			protected int getOffset(final Element e) {
				return e.getOffsetTop();
			}

			@Override
			protected int getLength(final Element e) {
				return e.getOffsetHeight();
			}

			@Override
			protected int getScroll(final Element e) {
				return e.getScrollTop();
			}

			@Override
			protected void setScroll(final Element e, final int pos) {
				e.setScrollTop(pos);
			}
		};

		protected abstract int getOffset(Element e);

		protected abstract void setScroll(Element e, int pos);

		protected abstract int getScroll(Element e);

		protected abstract int getLength(Element e);
	}

}
