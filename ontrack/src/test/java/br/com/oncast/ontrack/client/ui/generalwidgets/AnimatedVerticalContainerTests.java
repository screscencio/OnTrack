package br.com.oncast.ontrack.client.ui.generalwidgets;

import br.com.oncast.ontrack.client.ui.generalwidgets.animation.AnimationFactory;
import br.com.oncast.ontrack.client.ui.generalwidgetsa.animation.AnimationMockFactory;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.gwt.user.client.ui.ComplexPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.googlecode.gwt.test.GwtModule;
import com.googlecode.gwt.test.GwtTest;

@GwtModule("br.com.oncast.ontrack.Application")
public class AnimatedVerticalContainerTests extends GwtTest {

	public static final int ANIMATION_DURATION = 10;
	private static final int SLEEP_DELAY = 50;
	AnimatedContainer container;

	private final AnimationFactory animationMockFactory = new AnimationMockFactory(ANIMATION_DURATION);

	@Before
	public void setUp() throws Exception {
		container = new AnimatedContainer(new VerticalPanel(), animationMockFactory);
	}

	@Test
	public void shouldBeginWithNoWidgets() {
		Assert.assertEquals(0, container.getWidgetCount());
	}

	@Test
	public void insertShouldImediatelyMakeNewWidgetAvailable() {
		final Label widget = new Label();
		container.insert(widget, 0);

		Assert.assertEquals(1, container.getWidgetCount());
		Assert.assertEquals(widget, container.getWidget(0));
	}

	@Test
	public void insertShouldImediatelyMakeNewWidgetsAvailable() {
		final Label widget1 = new Label();
		final Label widget2 = new Label();

		container.insert(widget1, 0);
		container.insert(widget2, 0);

		Assert.assertEquals(2, container.getWidgetCount());
		Assert.assertEquals(widget1, container.getWidget(1));
		Assert.assertEquals(widget2, container.getWidget(0));
	}

	@Test
	public void insertShouldPlaceNewWidgetsInTheCorrectIndex() {
		final Label widget1 = new Label();
		final Label widget2 = new Label();
		final Label widget3 = new Label();
		final Label widget4 = new Label();
		final Label widget5 = new Label();

		container.insert(widget1, 0);
		container.insert(widget2, 0);
		container.insert(widget3, 0);
		container.insert(widget4, 1);
		container.insert(widget5, 3);

		Assert.assertEquals(5, container.getWidgetCount());
		Assert.assertEquals(widget1, container.getWidget(4));
		Assert.assertEquals(widget2, container.getWidget(2));
		Assert.assertEquals(widget3, container.getWidget(0));
		Assert.assertEquals(widget4, container.getWidget(1));
		Assert.assertEquals(widget5, container.getWidget(3));
	}

	@Test
	public void insertWhileRemovalIsOccuringShouldPlaceNewWidgetsInTheCorrectIndex() {
		final Label widget1 = new Label();
		final Label widget2 = new Label();
		final Label widget3 = new Label();
		final Label widget4 = new Label();
		final Label widget5 = new Label();

		container.insert(widget1, 0);
		container.insert(widget2, 1);
		container.insert(widget3, 2);

		Assert.assertEquals(3, container.getWidgetCount());
		Assert.assertEquals(widget1, container.getWidget(0));
		Assert.assertEquals(widget2, container.getWidget(1));
		Assert.assertEquals(widget3, container.getWidget(2));

		container.remove(widget2);

		Assert.assertEquals(2, container.getWidgetCount());

		container.insert(widget4, 1);
		container.insert(widget5, 0);

		Assert.assertEquals(4, container.getWidgetCount());
		Assert.assertEquals(widget5, container.getWidget(0));
		Assert.assertEquals(widget1, container.getWidget(1));
		Assert.assertEquals(widget4, container.getWidget(2));
		Assert.assertEquals(widget3, container.getWidget(3));
	}

	@Test
	public void removeShouldInstalyRemoveFromWidgetCount() {
		populateContainer();

		Assert.assertEquals(5, container.getWidgetCount());
		container.remove(2);
		Assert.assertEquals(4, container.getWidgetCount());
	}

	@Test
	public void removeShouldInstalyRemoveMultipleWidgetsFromWidgetCount() {
		populateContainer();

		Assert.assertEquals(5, container.getWidgetCount());
		container.remove(2);
		container.remove(2);
		Assert.assertEquals(3, container.getWidgetCount());
	}

	@Test
	public void removeShouldRemoveCorrectWidget() {
		populateContainer();

		final Widget widget = container.getWidget(3);
		container.remove(widget);
		Assert.assertEquals(4, container.getWidgetCount());
		Assert.assertNotSame(widget, container.getWidget(0));
		Assert.assertNotSame(widget, container.getWidget(1));
		Assert.assertNotSame(widget, container.getWidget(2));
		Assert.assertNotSame(widget, container.getWidget(3));
	}

	@Test
	public void removeShouldRemoveCorrectWidgetByIndex() {
		populateContainer();

		final Widget widget = container.getWidget(3);
		container.remove(3);
		Assert.assertEquals(4, container.getWidgetCount());
		Assert.assertNotSame(widget, container.getWidget(0));
		Assert.assertNotSame(widget, container.getWidget(1));
		Assert.assertNotSame(widget, container.getWidget(2));
		Assert.assertNotSame(widget, container.getWidget(3));
	}

	@Test
	public void removeShouldRemoveCorrectWidgetsByIndex() {
		populateContainer();

		final Widget widget0 = container.getWidget(0);
		container.remove(0);
		final Widget widget3 = container.getWidget(3);
		container.remove(3);
		Assert.assertEquals(3, container.getWidgetCount());
		Assert.assertNotSame(widget0, container.getWidget(0));
		Assert.assertNotSame(widget0, container.getWidget(1));
		Assert.assertNotSame(widget0, container.getWidget(2));
		Assert.assertNotSame(widget3, container.getWidget(0));
		Assert.assertNotSame(widget3, container.getWidget(1));
		Assert.assertNotSame(widget3, container.getWidget(2));
	}

	@Test
	public void removeShouldRemoveCorrectWidgets() {
		populateContainer();

		final Widget widget0 = container.getWidget(0);
		final Widget widget3 = container.getWidget(3);
		container.remove(0);
		container.remove(widget3);
		Assert.assertEquals(3, container.getWidgetCount());
		Assert.assertNotSame(widget0, container.getWidget(0));
		Assert.assertNotSame(widget0, container.getWidget(1));
		Assert.assertNotSame(widget0, container.getWidget(2));
		Assert.assertNotSame(widget3, container.getWidget(0));
		Assert.assertNotSame(widget3, container.getWidget(1));
		Assert.assertNotSame(widget3, container.getWidget(2));
	}

	@Test
	public void testGetWidgetIndex() {
		populateContainer();
		for (int i = 0; i < container.getWidgetCount(); i++) {
			final Widget widget = container.getWidget(i);
			Assert.assertEquals(i, container.getWidgetIndex(widget));
		}
	}

	@Test
	public void testClear() {
		populateContainer();
		container.clear();
		Assert.assertEquals(0, container.getWidgetCount());
	}

	@Test
	public void testContainerSyncOnInsertionAssertingSizes() throws InterruptedException {
		populateContainer();
		Thread.sleep(SLEEP_DELAY);

		final ComplexPanel panel = container.container;
		final List<IsWidget> list = container.widgets;

		Assert.assertTrue(panel.getWidgetCount() == list.size());
	}

	@Test
	public void testContainerSyncOnInsertionAssertingItems() throws InterruptedException {
		populateContainer();
		Thread.sleep(SLEEP_DELAY);

		final ComplexPanel panel = container.container;
		final List<IsWidget> list = container.widgets;

		for (final IsWidget isWidget : list) {
			Assert.assertTrue(panel.getWidgetIndex(isWidget) >= 0);
		}
	}

	@Test
	public void testContainerSyncOnInsertionAssertingItemsPositions() throws InterruptedException {
		populateContainer();
		Thread.sleep(SLEEP_DELAY);

		final ComplexPanel panel = container.container;
		final List<IsWidget> list = container.widgets;

		for (int i = 0; i < list.size(); i++) {
			Assert.assertEquals("Itens do not match in index '" + i + "'.", list.get(i), panel.getWidget(i));
		}
	}

	@Test
	public void testContainerSyncOnRemovalAssertingSizes() throws InterruptedException {
		populateContainer();
		container.remove(3);
		container.remove(1);
		Thread.sleep(SLEEP_DELAY);

		final ComplexPanel panel = container.container;
		final List<IsWidget> list = container.widgets;

		Assert.assertTrue(panel.getWidgetCount() == list.size());
	}

	@Test
	public void testContainerSyncOnRemovalAssertingItems() throws InterruptedException {
		populateContainer();
		container.remove(3);
		container.remove(1);
		getBrowserSimulator().fireLoopEnd();
		Thread.sleep(SLEEP_DELAY);

		final ComplexPanel panel = container.container;
		final List<IsWidget> list = container.widgets;

		for (final IsWidget isWidget : list) {
			Assert.assertTrue(panel.getWidgetIndex(isWidget) >= 0);
		}
	}

	@Test
	public void testContainerSyncOnRemovalAssertingItemsPositions() throws InterruptedException {
		populateContainer();
		container.remove(3);
		container.remove(1);
		Thread.sleep(SLEEP_DELAY);

		final ComplexPanel panel = container.container;
		final List<IsWidget> list = container.widgets;

		for (int i = 0; i < list.size(); i++) {
			Assert.assertEquals("Itens do not match in index '" + i + "'.", list.get(i), panel.getWidget(i));
		}
	}

	@Test
	public void testContainerSyncOnBothInsertionAndRemovalAssertingSizes() throws InterruptedException {
		final Label widget = new Label("w");

		populateContainer();
		container.remove(3);
		container.insert(widget, 4);
		container.remove(1);
		Thread.sleep(SLEEP_DELAY);

		final ComplexPanel panel = container.container;
		final List<IsWidget> list = container.widgets;

		Assert.assertTrue(panel.getWidgetCount() == list.size());
	}

	@Test
	public void testContainerSyncOnBothInsertionAndRemovalAssertingItems() throws InterruptedException {
		final Label widget = new Label("w");

		populateContainer();
		container.remove(3);
		container.insert(widget, 4);
		container.remove(1);
		Thread.sleep(SLEEP_DELAY);

		final ComplexPanel panel = container.container;
		final List<IsWidget> list = container.widgets;

		for (final IsWidget isWidget : list) {
			Assert.assertTrue(panel.getWidgetIndex(isWidget) >= 0);
		}
	}

	@Test
	public void testContainerSyncOnBothInsertionAndRemovalAssertingItemsPositions() throws InterruptedException {
		final Label widget = new Label("w");

		populateContainer();
		container.remove(3);
		container.insert(widget, 4);
		container.remove(1);
		Thread.sleep(SLEEP_DELAY);

		final ComplexPanel panel = container.container;
		final List<IsWidget> list = container.widgets;

		for (int i = 0; i < list.size(); i++) {
			Assert.assertEquals("Itens do not match in index '" + i + "'.", list.get(i), panel.getWidget(i));
		}
	}

	private void populateContainer() {
		final Label widget1 = new Label("w1");
		final Label widget2 = new Label("w2");
		final Label widget3 = new Label("w3");
		final Label widget4 = new Label("w4");
		final Label widget5 = new Label("w5");

		container.insert(widget1, 0);
		container.insert(widget2, 1);
		container.insert(widget3, 2);
		container.insert(widget4, 3);
		container.insert(widget5, 4);

		Assert.assertEquals(5, container.getWidgetCount());
		Assert.assertEquals(widget1, container.getWidget(0));
		Assert.assertEquals(widget2, container.getWidget(1));
		Assert.assertEquals(widget3, container.getWidget(2));
		Assert.assertEquals(widget4, container.getWidget(3));
		Assert.assertEquals(widget5, container.getWidget(4));
	}
}
