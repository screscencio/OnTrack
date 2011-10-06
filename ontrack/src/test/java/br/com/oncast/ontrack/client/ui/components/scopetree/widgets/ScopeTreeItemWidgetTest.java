package br.com.oncast.ontrack.client.ui.components.scopetree.widgets;

import static java.lang.Math.abs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat;
import br.com.oncast.ontrack.shared.model.effort.EffortInferenceEngine;
import br.com.oncast.ontrack.shared.model.scope.Scope;

import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.octo.gwt.test.GwtTest;

public class ScopeTreeItemWidgetTest extends GwtTest {

	private static final Random RANDOM = new Random();
	private static final EffortInferenceEngine EFFORT_INFERENCE_ENGINE = new EffortInferenceEngine();
	private static final String STRIPED_CLASS_NAME = "effortLabelStriped";
	private static final String TRANSLUCID_CLASS_NAME = "effortLabelTranslucid";
	private ScopeTreeItemWidget parentWidget;
	private ScopeTreeItemWidget childAWidget;
	private Scope parent;
	private Scope childA;
	private Scope grandChildA1;
	private Scope grandChildA2;
	private Scope childB;
	private ScopeTreeItemWidget childBWidget;
	private ScopeTreeItemWidget grandChildA1Widget;
	private ScopeTreeItemWidget grandChildA2Widget;

	@Before
	public void setUp() {
		parent = new Scope("parent");
		childA = new Scope("child A");
		grandChildA1 = new Scope("grand child A1");
		grandChildA2 = new Scope("grand child A2");
		childB = new Scope("child B");

		parent.add(childA);
		childA.add(grandChildA1);
		childA.add(grandChildA2);
		parent.add(childB);

		parentWidget = getScopeTreeItemWidget(parent);
		childAWidget = getScopeTreeItemWidget(childA);
		childBWidget = getScopeTreeItemWidget(childB);
		grandChildA1Widget = getScopeTreeItemWidget(grandChildA1);
		grandChildA2Widget = getScopeTreeItemWidget(grandChildA2);
	}

	@Test
	public void noLabelShoudBeVisibleWhenEffortIsNotDefinedNorInfered() {
		updateDisplays();

		assertNotVisibleDeclaredEffortLabel(parentWidget);
		assertNotVisibleDeclaredEffortLabel(childAWidget);
		assertNotVisibleDeclaredEffortLabel(childBWidget);
		assertNotVisibleDeclaredEffortLabel(grandChildA1Widget);
		assertNotVisibleDeclaredEffortLabel(grandChildA2Widget);

		assertNotVisibleInferedEffortLabel(parentWidget);
		assertNotVisibleInferedEffortLabel(childAWidget);
		assertNotVisibleInferedEffortLabel(childBWidget);
		assertNotVisibleInferedEffortLabel(grandChildA1Widget);
		assertNotVisibleInferedEffortLabel(grandChildA2Widget);
	}

	@Test
	public void declaredEffortLabelShoudBeVisibleAndNotStripedWhenHasDeclaredEffortOnly() {
		final int effortValue = anyEffortValue();
		declareEffort(childA, effortValue);
		updateEfforts();
		updateDisplays();

		assertNotVisibleDeclaredEffortLabel(parentWidget);
		assertVisibleDeclaredEffortLabel(childAWidget);
		assertNotVisibleDeclaredEffortLabel(childBWidget);
		assertNotVisibleDeclaredEffortLabel(grandChildA1Widget);
		assertNotVisibleDeclaredEffortLabel(grandChildA2Widget);

		assertEffortLabelValue(childAWidget.declaredEffortLabel, effortValue);
		assertNotStriped(childAWidget);
	}

	@Test
	public void inferedEffortLabelShouldBeVisibleAndTranslucidWhenAnyParentHasDeclaredEffort() {
		declareEffort(parent, anyEffortValue());
		updateEfforts();
		updateDisplays();

		assertNotVisibleInferedEffortLabel(parentWidget);
		assertVisibleAndTranslucidInferedEffortLabel(childAWidget);
		assertVisibleAndTranslucidInferedEffortLabel(childBWidget);
		assertVisibleAndTranslucidInferedEffortLabel(grandChildA1Widget);
		assertVisibleAndTranslucidInferedEffortLabel(grandChildA2Widget);

		assertVisibleDeclaredEffortLabel(parentWidget);
		assertNotVisibleDeclaredEffortLabel(childAWidget);
		assertNotVisibleDeclaredEffortLabel(childBWidget);
		assertNotVisibleDeclaredEffortLabel(grandChildA1Widget);
		assertNotVisibleDeclaredEffortLabel(grandChildA2Widget);
	}

	@Test
	public void inferedEffortLabelShouldBeVisibleAndTranslucidWhenAnyChildHasDeclaredEffort() {
		declareEffort(grandChildA2, anyEffortValue());
		updateEfforts();
		updateDisplays();

		assertVisibleAndTranslucidInferedEffortLabel(parentWidget);
		assertVisibleAndTranslucidInferedEffortLabel(childAWidget);
		assertNotVisibleInferedEffortLabel(childBWidget);
		assertNotVisibleInferedEffortLabel(grandChildA1Widget);
		assertNotVisibleInferedEffortLabel(grandChildA2Widget);

		assertNotVisibleDeclaredEffortLabel(parentWidget);
		assertNotVisibleDeclaredEffortLabel(childAWidget);
		assertNotVisibleDeclaredEffortLabel(childBWidget);
		assertNotVisibleDeclaredEffortLabel(grandChildA1Widget);
		assertVisibleDeclaredEffortLabel(grandChildA2Widget);
	}

	@Test
	public void inferedEffortLabelShouldNotBeVisibleWhenDeclaredValueHasSameValueOfBottomUpEffort() {
		final float childAEffort = 5;
		final float childBEffort = 8;
		final float parentEffort = childAEffort + childBEffort;
		declareEffort(parent, parentEffort);
		declareEffort(childA, childAEffort);
		declareEffort(childB, childBEffort);
		updateEfforts();
		updateDisplays();

		assertNotVisibleInferedEffortLabel(parentWidget);
		assertNotVisibleInferedEffortLabel(childAWidget);
		assertNotVisibleInferedEffortLabel(childBWidget);
		assertVisibleAndTranslucidInferedEffortLabel(grandChildA1Widget);
		assertVisibleAndTranslucidInferedEffortLabel(grandChildA2Widget);

		assertVisibleDeclaredEffortLabel(parentWidget);
		assertVisibleDeclaredEffortLabel(childAWidget);
		assertVisibleDeclaredEffortLabel(childBWidget);
		assertNotVisibleDeclaredEffortLabel(grandChildA1Widget);
		assertNotVisibleDeclaredEffortLabel(grandChildA2Widget);
	}

	@Test
	public void inferedEffortLabelShouldBeNotVisibleWhenHasDeclaredValueGreaterThanInferedValue() {
		final float childAEffort = 5;
		final float childBEffort = 8;
		final float parentDeclaredEffort = childAEffort + childBEffort + 3;

		declareEffort(parent, parentDeclaredEffort);
		declareEffort(childA, childAEffort);
		declareEffort(childB, childBEffort);
		updateEfforts();
		updateDisplays();

		assertNotVisibleInferedEffortLabel(parentWidget);
		assertNotVisibleInferedEffortLabel(childAWidget);
		assertNotVisibleInferedEffortLabel(childBWidget);
		assertVisibleAndTranslucidInferedEffortLabel(grandChildA1Widget);
		assertVisibleAndTranslucidInferedEffortLabel(grandChildA2Widget);

		assertVisibleDeclaredEffortLabel(parentWidget);
		assertVisibleDeclaredEffortLabel(childAWidget);
		assertVisibleDeclaredEffortLabel(childBWidget);
		assertNotVisibleDeclaredEffortLabel(grandChildA1Widget);
		assertNotVisibleDeclaredEffortLabel(grandChildA2Widget);

		assertEffortLabelValue(parentWidget.declaredEffortLabel, parentDeclaredEffort);

		assertNotStriped(parentWidget);
	}

	@Test
	public void declaredEffortLabelShouldBeVisibleAndStripedWhenHasDeclaredValueLessThanInferedValue() {
		final float childAEffort = 5;
		final float childBEffort = 8;
		final float parentInferedEffort = childAEffort + childBEffort;
		final float parentDeclaredEffort = parentInferedEffort - 3;

		declareEffort(parent, parentDeclaredEffort);
		declareEffort(childA, childAEffort);
		declareEffort(childB, childBEffort);
		updateEfforts();
		updateDisplays();

		assertVisibleAndTranslucidInferedEffortLabel(parentWidget);
		assertNotVisibleInferedEffortLabel(childAWidget);
		assertNotVisibleInferedEffortLabel(childBWidget);
		assertVisibleAndTranslucidInferedEffortLabel(grandChildA1Widget);
		assertVisibleAndTranslucidInferedEffortLabel(grandChildA2Widget);

		assertVisibleDeclaredEffortLabel(parentWidget);
		assertVisibleDeclaredEffortLabel(childAWidget);
		assertVisibleDeclaredEffortLabel(childBWidget);
		assertNotVisibleDeclaredEffortLabel(grandChildA1Widget);
		assertNotVisibleDeclaredEffortLabel(grandChildA2Widget);

		assertEffortLabelValue(parentWidget.declaredEffortLabel, parentDeclaredEffort);
		assertEffortLabelValue(parentWidget.inferedEffortLabel, parentInferedEffort);

		assertStriped(parentWidget);
	}

	private void assertNotStriped(final ScopeTreeItemWidget widget) {
		assertFalse(hasStripedClassName(widget.declaredEffortLabel));
	}

	private void assertStriped(final ScopeTreeItemWidget widget) {
		assertTrue(hasStripedClassName(widget.declaredEffortLabel));
	}

	private boolean hasStripedClassName(final Label label) {
		return label.getElement().getClassName().contains(STRIPED_CLASS_NAME);
	}

	private void declareEffort(final Scope scope, final float effort) {
		scope.getEffort().setDeclared((int) effort);
	}

	private void updateEfforts() {
		EFFORT_INFERENCE_ENGINE.process(parent);
	}

	private void assertNotVisibleDeclaredEffortLabel(final ScopeTreeItemWidget widget) {
		assertNotVisible(widget.declaredEffortLabel);
	}

	private void assertVisibleDeclaredEffortLabel(final ScopeTreeItemWidget widget) {
		assertVisible(widget.declaredEffortLabel);
	}

	private void assertVisibleAndTranslucidInferedEffortLabel(final ScopeTreeItemWidget widget) {
		assertVisible(widget.inferedEffortLabel);
		assertTrue(widget.inferedEffortLabel.getElement().getClassName().contains(TRANSLUCID_CLASS_NAME));
	}

	private void assertNotVisibleInferedEffortLabel(final ScopeTreeItemWidget widget) {
		assertNotVisible(widget.inferedEffortLabel);
	}

	private void assertVisible(final Widget widget) {
		assertTrue(widget.isVisible());
	}

	private void assertNotVisible(final Widget widget) {
		assertFalse(widget.isVisible());
	}

	private void assertEffortLabelValue(final Label label, final float effortValue) {
		assertEquals(ClientDecimalFormat.roundFloat(effortValue, 1) + "ep", label.getText());
	}

	private int anyEffortValue() {
		return abs(RANDOM.nextInt());
	}

	private void updateDisplays() {
		parentWidget.updateDisplay();
		childAWidget.updateDisplay();
		childBWidget.updateDisplay();
		grandChildA1Widget.updateDisplay();
		grandChildA2Widget.updateDisplay();
	}

	private ScopeTreeItemWidget getScopeTreeItemWidget(final Scope parent) {
		return new ScopeTreeItem(parent).getScopeTreeItemWidget();
	}

	@Override
	public String getModuleName() {
		return "br.com.oncast.ontrack.Application";
	}

}
