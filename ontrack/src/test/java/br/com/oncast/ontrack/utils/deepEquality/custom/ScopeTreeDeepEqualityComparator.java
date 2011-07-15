package br.com.oncast.ontrack.utils.deepEquality.custom;

import junit.framework.Assert;
import br.com.oncast.ontrack.client.ui.components.scopetree.ScopeTree;
import br.com.oncast.ontrack.client.ui.components.scopetree.widgets.ScopeTreeWidget;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityException;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityTestUtils;

public class ScopeTreeDeepEqualityComparator implements DeepEqualityComparator<ScopeTree> {

	@Override
	public void assertObjectEquality(final ScopeTree expected, final ScopeTree actual) throws DeepEqualityException {

		final ScopeTreeWidget expectedTreeWidget = (ScopeTreeWidget) expected.asWidget();
		final ScopeTreeWidget actualTreeWidget = (ScopeTreeWidget) actual.asWidget();

		final int expectedItemCount = expectedTreeWidget.getItemCount();
		final int actualItemCount = actualTreeWidget.getItemCount();

		Assert.assertEquals(expectedItemCount, actualItemCount);

		for (int i = 0; i < expectedItemCount; i++)
			DeepEqualityTestUtils.assertObjectEquality(expectedTreeWidget.getItem(i), actualTreeWidget.getItem(i));
	}
}
