package br.com.oncast.ontrack.client.ui.components.scopetree.widgets;

import static java.lang.Math.abs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.Random;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import br.com.oncast.ontrack.client.services.ClientServiceProviderTestUtils;
import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat;
import br.com.oncast.ontrack.shared.model.effort.EffortInferenceEngine;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.utils.mocks.models.UserRepresentationTestUtils;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;

import com.google.gwt.user.client.ui.Label;
import com.googlecode.gwt.test.GwtModule;
import com.googlecode.gwt.test.GwtTest;

@GwtModule("br.com.oncast.ontrack.Application")
public class ScopeTreeItemWidgetTest extends GwtTest {

	private static final Random RANDOM = new Random();
	private static final EffortInferenceEngine EFFORT_INFERENCE_ENGINE = new EffortInferenceEngine();
	private static final String STRIPED_CLASS_NAME = "labelStriped";
	private static final String TRANSLUCID_CLASS_NAME = "labelInfered";
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

	@BeforeClass
	public static void beforeClass() throws Exception {
		ClientServiceProviderTestUtils.configure().mockEssential();
	}

	@AfterClass
	public static void afterClass() throws Exception {
		ClientServiceProviderTestUtils.reset();
	}

	@Before
	public void setUp() {
		parent = ScopeTestUtils.createScope("parent");
		childA = ScopeTestUtils.createScope("child A");
		grandChildA1 = ScopeTestUtils.createScope("grand child A1");
		grandChildA2 = ScopeTestUtils.createScope("grand child A2");
		childB = ScopeTestUtils.createScope("child B");

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
	public void noLabelShoudBeDefinedWhenEffortIsNotDefinedNorInfered() {
		updateDisplays();

		assertNotDefinedDeclaredEffortLabel(parentWidget);
		assertNotDefinedDeclaredEffortLabel(childAWidget);
		assertNotDefinedDeclaredEffortLabel(childBWidget);
		assertNotDefinedDeclaredEffortLabel(grandChildA1Widget);
		assertNotDefinedDeclaredEffortLabel(grandChildA2Widget);

		assertNotDefinedInferedEffortLabel(parentWidget);
		assertNotDefinedInferedEffortLabel(childAWidget);
		assertNotDefinedInferedEffortLabel(childBWidget);
		assertNotDefinedInferedEffortLabel(grandChildA1Widget);
		assertNotDefinedInferedEffortLabel(grandChildA2Widget);
	}

	@Test
	public void declaredEffortLabelShoudBeDefinedAndNotStripedWhenHasDeclaredEffortOnly() {
		final int effortValue = anyEffortValue();
		declareEffort(childA, effortValue);
		updateEfforts();
		updateDisplays();

		assertNotDefinedDeclaredEffortLabel(parentWidget);
		assertDefinedDeclaredEffortLabel(childAWidget);
		assertNotDefinedDeclaredEffortLabel(childBWidget);
		assertNotDefinedDeclaredEffortLabel(grandChildA1Widget);
		assertNotDefinedDeclaredEffortLabel(grandChildA2Widget);

		assertEffortLabelValue(childAWidget.declaredEffortLabel, effortValue);
		assertNotStriped(childAWidget);
	}

	@Test
	public void inferedEffortLabelShouldBeDefinedAndTranslucidWhenAnyParentHasDeclaredEffort() {
		declareEffort(parent, anyEffortValue());
		updateEfforts();
		updateDisplays();

		assertNotDefinedInferedEffortLabel(parentWidget);
		assertDefinedAndInferedEffortLabel(childAWidget);
		assertDefinedAndInferedEffortLabel(childBWidget);
		assertDefinedAndInferedEffortLabel(grandChildA1Widget);
		assertDefinedAndInferedEffortLabel(grandChildA2Widget);

		assertDefinedDeclaredEffortLabel(parentWidget);
		assertNotDefinedDeclaredEffortLabel(childAWidget);
		assertNotDefinedDeclaredEffortLabel(childBWidget);
		assertNotDefinedDeclaredEffortLabel(grandChildA1Widget);
		assertNotDefinedDeclaredEffortLabel(grandChildA2Widget);
	}

	@Test
	public void inferedEffortLabelShouldBeDefinedAndTranslucidWhenAnyChildHasDeclaredEffort() {
		declareEffort(grandChildA2, anyEffortValue());
		updateEfforts();
		updateDisplays();

		assertDefinedAndInferedEffortLabel(parentWidget);
		assertDefinedAndInferedEffortLabel(childAWidget);
		assertNotDefinedInferedEffortLabel(childBWidget);
		assertNotDefinedInferedEffortLabel(grandChildA1Widget);
		assertNotDefinedInferedEffortLabel(grandChildA2Widget);

		assertNotDefinedDeclaredEffortLabel(parentWidget);
		assertNotDefinedDeclaredEffortLabel(childAWidget);
		assertNotDefinedDeclaredEffortLabel(childBWidget);
		assertNotDefinedDeclaredEffortLabel(grandChildA1Widget);
		assertDefinedDeclaredEffortLabel(grandChildA2Widget);
	}

	@Test
	public void inferedEffortLabelShouldNotBeDefinedWhenDeclaredValueHasSameValueOfBottomUpEffort() {
		final float childAEffort = 5;
		final float childBEffort = 8;
		final float parentEffort = childAEffort + childBEffort;
		declareEffort(parent, parentEffort);
		declareEffort(childA, childAEffort);
		declareEffort(childB, childBEffort);
		updateEfforts();
		updateDisplays();

		assertNotDefinedInferedEffortLabel(parentWidget);
		assertNotDefinedInferedEffortLabel(childAWidget);
		assertNotDefinedInferedEffortLabel(childBWidget);
		assertDefinedAndInferedEffortLabel(grandChildA1Widget);
		assertDefinedAndInferedEffortLabel(grandChildA2Widget);

		assertDefinedDeclaredEffortLabel(parentWidget);
		assertDefinedDeclaredEffortLabel(childAWidget);
		assertDefinedDeclaredEffortLabel(childBWidget);
		assertNotDefinedDeclaredEffortLabel(grandChildA1Widget);
		assertNotDefinedDeclaredEffortLabel(grandChildA2Widget);
	}

	@Test
	public void inferedEffortLabelShouldBeNotDefinedWhenHasDeclaredValueGreaterThanInferedValue() {
		final float childAEffort = 5;
		final float childBEffort = 8;
		final float parentDeclaredEffort = childAEffort + childBEffort + 3;

		declareEffort(parent, parentDeclaredEffort);
		declareEffort(childA, childAEffort);
		declareEffort(childB, childBEffort);
		updateEfforts();
		updateDisplays();

		assertNotDefinedInferedEffortLabel(parentWidget);
		assertNotDefinedInferedEffortLabel(childAWidget);
		assertNotDefinedInferedEffortLabel(childBWidget);
		assertDefinedAndInferedEffortLabel(grandChildA1Widget);
		assertDefinedAndInferedEffortLabel(grandChildA2Widget);

		assertDefinedDeclaredEffortLabel(parentWidget);
		assertDefinedDeclaredEffortLabel(childAWidget);
		assertDefinedDeclaredEffortLabel(childBWidget);
		assertNotDefinedDeclaredEffortLabel(grandChildA1Widget);
		assertNotDefinedDeclaredEffortLabel(grandChildA2Widget);

		assertEffortLabelValue(parentWidget.declaredEffortLabel, parentDeclaredEffort);

		assertNotStriped(parentWidget);
	}

	@Test
	public void declaredEffortLabelShouldBeDefinedAndStripedWhenHasDeclaredValueLessThanInferedValue() {
		final float childAEffort = 5;
		final float childBEffort = 8;
		final float parentInferedEffort = childAEffort + childBEffort;
		final float parentDeclaredEffort = parentInferedEffort - 3;

		declareEffort(parent, parentDeclaredEffort);
		declareEffort(childA, childAEffort);
		declareEffort(childB, childBEffort);
		updateEfforts();
		updateDisplays();

		assertDefinedAndInferedEffortLabel(parentWidget);
		assertNotDefinedInferedEffortLabel(childAWidget);
		assertNotDefinedInferedEffortLabel(childBWidget);
		assertDefinedAndInferedEffortLabel(grandChildA1Widget);
		assertDefinedAndInferedEffortLabel(grandChildA2Widget);

		assertDefinedDeclaredEffortLabel(parentWidget);
		assertDefinedDeclaredEffortLabel(childAWidget);
		assertDefinedDeclaredEffortLabel(childBWidget);
		assertNotDefinedDeclaredEffortLabel(grandChildA1Widget);
		assertNotDefinedDeclaredEffortLabel(grandChildA2Widget);

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
		EFFORT_INFERENCE_ENGINE.process(parent, UserRepresentationTestUtils.getAdmin(), new Date());
	}

	private void assertNotDefinedDeclaredEffortLabel(final ScopeTreeItemWidget widget) {
		assertNotDefined(widget.declaredEffortLabel);
	}

	private void assertDefinedDeclaredEffortLabel(final ScopeTreeItemWidget widget) {
		assertDefined(widget.declaredEffortLabel);
	}

	private void assertDefinedAndInferedEffortLabel(final ScopeTreeItemWidget widget) {
		assertDefined(widget.inferedEffortLabel);
		assertTrue(widget.inferedEffortLabel.getElement().getClassName().contains(TRANSLUCID_CLASS_NAME));
	}

	private void assertNotDefinedInferedEffortLabel(final ScopeTreeItemWidget widget) {
		assertNotDefined(widget.inferedEffortLabel);
	}

	private void assertDefined(final Label label) {
		assertFalse("".equals(label.getText()));
	}

	private void assertNotDefined(final Label label) {
		assertEquals("", label.getText());
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
}
