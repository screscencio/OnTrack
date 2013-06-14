package br.com.oncast.ontrack.client.ui.components.scopetree.widgets;

import static java.lang.Math.abs;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import br.com.oncast.ontrack.client.services.ClientServicesTestUtils;
import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTreeItem;
import br.com.oncast.ontrack.client.utils.number.ClientDecimalFormat;
import br.com.oncast.ontrack.shared.model.prioritizationCriteria.EffortInferenceEngine;
import br.com.oncast.ontrack.shared.model.prioritizationCriteria.PrioritizationCriteria;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.mocks.models.UserRepresentationTestUtils;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;

import com.google.gwt.dom.client.Element;
import com.googlecode.gwt.test.GwtModule;
import com.googlecode.gwt.test.GwtTest;

@GwtModule("br.com.oncast.ontrack.Application")
public class ScopeTreeItemWidgetTest extends GwtTest {

	private static final Random RANDOM = new Random();
	private static final EffortInferenceEngine EFFORT_INFERENCE_ENGINE = new EffortInferenceEngine();
	private static final String CONFLICTED_STYLE_NAME = "amountConflicted";
	private static final String INFERED_STYLE_NAME = "amountInfered";

	private Map<UUID, ScopeTreeItemWidget> widgetMap;

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
		ClientServicesTestUtils.configure().mockEssential();
	}

	@AfterClass
	public static void afterClass() throws Exception {
		ClientServicesTestUtils.reset();
	}

	@Before
	public void setUp() {
		widgetMap = new HashMap<UUID, ScopeTreeItemWidget>();

		parent = ScopeTestUtils.createScope("parent");
		childA = ScopeTestUtils.createScope("child A");
		grandChildA1 = ScopeTestUtils.createScope("grand child A1");
		grandChildA2 = ScopeTestUtils.createScope("grand child A2");
		childB = ScopeTestUtils.createScope("child B");

		parent.add(childA);
		childA.add(grandChildA1);
		childA.add(grandChildA2);
		parent.add(childB);

		parentWidget = getWidget(parent);
		childAWidget = getWidget(childA);
		childBWidget = getWidget(childB);
		grandChildA1Widget = getWidget(grandChildA1);
		grandChildA2Widget = getWidget(grandChildA2);
	}

	@After
	public void cleanUp() throws Exception {
		getBrowserSimulator().fireLoopEnd();
	}

	@Test
	public void shouldDisplayEmptyLabelsWhenThereIsntAnyDeclaredNorInfered() {
		assertEmpty(parentWidget);
		assertEmpty(childAWidget);
		assertEmpty(childBWidget);
		assertEmpty(grandChildA1Widget);
		assertEmpty(grandChildA2Widget);
	}

	@Test
	public void shouldDisplayAsInferedWhenNotDeclaredAndTheParentHasAnyNonZeroValue() {
		declare(parent, anyValue());

		assertDeclared(parentWidget);
		assertInfered(childAWidget);
		assertInfered(childBWidget);
		assertInfered(grandChildA1Widget);
		assertInfered(grandChildA2Widget);
	}

	@Test
	public void shouldDisplayAsInferedWhenNotDeclaredAndAnyChildHasAnyNonZeroValue() {
		declare(grandChildA1, anyValue());

		assertDeclared(grandChildA1Widget);
		assertInfered(childAWidget);
		assertInfered(parentWidget);
	}

	@Test
	public void shouldDisplayEmptyLabelWhenNotDeclaredAndTheParentValueIsAllTakenByTheSimblings() {
		final int anyValue = anyValue();
		declare(parent, anyValue);
		declare(childA, anyValue);

		assertDeclared(parentWidget);
		assertDeclared(childAWidget);
		assertEmpty(childBWidget);
	}

	@Test
	public void shouldDisplayAsDeclaredWhenTheDeclaredIsTheSameAsTheInfered() {
		final float childAValue = 5;
		final float childBValue = 8;
		final float parentValue = childAValue + childBValue;
		declare(parent, parentValue);
		declare(childA, childAValue);
		declare(childB, childBValue);

		assertDeclared(parentWidget);
		assertDeclared(childAWidget);
		assertDeclared(childBWidget);
		assertInfered(grandChildA1Widget);
		assertInfered(grandChildA2Widget);
	}

	@Test
	public void shouldDisplayTheDeclaredWhenTheDeclaredIsGreaterThanTheInfered() {
		final float childAValue = 5;
		final float childBValue = 8;
		final float parentValue = childAValue + childBValue + 3;
		declare(parent, parentValue);
		declare(childA, childAValue);
		declare(childB, childBValue);

		assertValue(parentWidget, parentValue);
		assertDeclared(parentWidget);
		assertDeclared(childAWidget);
		assertDeclared(childBWidget);
		assertInfered(grandChildA1Widget);
		assertInfered(grandChildA2Widget);
	}

	@Test
	public void shouldNotConflictWhenThereIsntAnyDeclared() {
		assertNotConflicted(parentWidget);
		assertNotConflicted(childAWidget);
		assertNotConflicted(childBWidget);
		assertNotConflicted(grandChildA1Widget);
		assertNotConflicted(grandChildA2Widget);
	}

	@Test
	public void shouldNotConflictWhenOnlyTheParentHasDeclared() {
		declare(parent, anyValue());

		assertNotConflicted(parentWidget);
		assertNotConflicted(childAWidget);
		assertNotConflicted(childBWidget);
		assertNotConflicted(grandChildA1Widget);
		assertNotConflicted(grandChildA2Widget);
	}

	@Test
	public void shouldNotConflictWhenOnlyTheChildHasDeclared() {
		declare(grandChildA2, anyValue());

		assertNotConflicted(parentWidget);
		assertNotConflicted(childAWidget);
		assertNotConflicted(childBWidget);
		assertNotConflicted(grandChildA1Widget);
		assertNotConflicted(grandChildA2Widget);
	}

	@Test
	public void shouldNotConflictWhenTheParentsDeclaredValueIsGreaterThanTheSumOfTheChildrensValue() {
		declare(parent, 10);
		declare(grandChildA1, 1);
		declare(grandChildA2, 3);
		declare(childB, 5);

		assertNotConflicted(parentWidget);
		assertNotConflicted(childAWidget);
		assertNotConflicted(childBWidget);
		assertNotConflicted(grandChildA1Widget);
		assertNotConflicted(grandChildA2Widget);
	}

	@Test
	public void shouldNotConflictWhenTheParentsDeclaredValueIsEqualThanTheSumOfTheChildrensValue() {
		declare(parent, 10);
		declare(grandChildA1, 2);
		declare(grandChildA2, 3);
		declare(childB, 5);

		assertNotConflicted(parentWidget);
		assertNotConflicted(childAWidget);
		assertNotConflicted(childBWidget);
		assertNotConflicted(grandChildA1Widget);
		assertNotConflicted(grandChildA2Widget);
	}

	@Test
	public void effortLabelShouldHaveInferedEffortAndStyledAsConflictedWhenHasDeclaredEffortIsSmallerThanInferedEffort() {
		final float childAEffort = 5;
		final float childBEffort = 8;
		final float parentInferedEffort = childAEffort + childBEffort;
		final float parentDeclaredEffort = parentInferedEffort - 3;

		declare(parent, parentDeclaredEffort);
		declare(childA, childAEffort);
		declare(childB, childBEffort);

		assertInfered(parentWidget);
		assertDeclared(childAWidget);
		assertDeclared(childBWidget);
		assertInfered(grandChildA1Widget);
		assertInfered(grandChildA2Widget);

		assertValue(parentWidget, parentInferedEffort);
		assertConflicted(parentWidget);
	}

	private void assertNotConflicted(final ScopeTreeItemWidget widget) {
		assertFalse(containsConflictedStyle(widget));
	}

	private void assertConflicted(final ScopeTreeItemWidget widget) {
		assertTrue(containsConflictedStyle(widget));
	}

	private boolean containsConflictedStyle(final ScopeTreeItemWidget widget) {
		return getElement(widget).getParentElement().getClassName().contains(CONFLICTED_STYLE_NAME);
	}

	private void declare(final Scope scope, final float value) {
		getPriorizationCriteria(scope).setDeclared(value);
		final Set<UUID> influencedScopes = getEngine().process(scope, UserRepresentationTestUtils.getAdmin(), new Date());
		for (final UUID uuid : influencedScopes) {
			widgetMap.get(uuid).updateDisplay();
		}
	}

	private void assertInfered(final ScopeTreeItemWidget widget) {
		assertNotEmpty(widget);
		assertTrue(containsInferedStyle(widget));
	}

	private void assertDeclared(final ScopeTreeItemWidget widget) {
		assertNotEmpty(widget);
		assertFalse(containsInferedStyle(widget));
	}

	private boolean containsInferedStyle(final ScopeTreeItemWidget widget) {
		return getElement(widget).getParentElement().getClassName().contains(INFERED_STYLE_NAME);
	}

	private void assertNotEmpty(final ScopeTreeItemWidget element) {
		assertFalse(isEmpty(element));
	}

	private void assertEmpty(final ScopeTreeItemWidget element) {
		assertTrue(isEmpty(element));
	}

	private boolean isEmpty(final ScopeTreeItemWidget widget) {
		return getElement(widget).getInnerText().isEmpty();
	}

	private void assertValue(final ScopeTreeItemWidget widget, final float value) {
		assertEquals(ClientDecimalFormat.roundFloat(value, 1) + "ep", getElement(widget).getInnerText());
	}

	private int anyValue() {
		return abs(RANDOM.nextInt());
	}

	private PrioritizationCriteria getPriorizationCriteria(final Scope scope) {
		return scope.getEffort();
	}

	private Element getElement(final ScopeTreeItemWidget widget) {
		return widget.effortLabel;
	}

	private EffortInferenceEngine getEngine() {
		return EFFORT_INFERENCE_ENGINE;
	}

	private ScopeTreeItemWidget getWidget(final Scope scope) {
		final ScopeTreeItemWidget widget = new ScopeTreeItem(scope).getScopeTreeItemWidget();
		widgetMap.put(scope.getId(), widget);
		return widget;
	}
}
