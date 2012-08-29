package br.com.oncast.ontrack.utils.mocks.gwtTestUtilsPatchers;

import br.com.oncast.ontrack.client.services.metric.ClientMetricServiceNewRelicImpl;

import com.googlecode.gwt.test.patchers.PatchClass;
import com.googlecode.gwt.test.patchers.PatchMethod;

@PatchClass(ClientMetricServiceNewRelicImpl.class)
public class ClientMetricServiceNewRelicImplPatcher {

	@PatchMethod
	static void header() {}

	@PatchMethod
	static void footer() {}

}
