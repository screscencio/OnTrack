package br.com.oncast.ontrack.utils.deepEquality.custom.mocks;

import br.com.oncast.ontrack.shared.model.prioritizationCriteria.Effort;
import br.com.oncast.ontrack.utils.deepEquality.DeepEqualityException;
import br.com.oncast.ontrack.utils.deepEquality.custom.DeepEqualityComparator;

public class EffortDeepEqualityComparator implements DeepEqualityComparator<Effort> {

	@Override
	public void assertObjectEquality(final Effort expected, final Effort actual) throws DeepEqualityException {
		// Do not assert anything of this class.
	}
}