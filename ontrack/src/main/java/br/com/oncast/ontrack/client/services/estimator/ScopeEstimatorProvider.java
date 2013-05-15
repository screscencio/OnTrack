package br.com.oncast.ontrack.client.services.estimator;

import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.context.ContextProviderServiceImpl.ContextChangeListener;
import br.com.oncast.ontrack.shared.model.scope.ScopeEstimator;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ScopeEstimatorProvider implements ContextChangeListener {

	private ScopeEstimator scopeEstimator;
	private final ReleaseEstimatorProvider releaseEstimatorProvider;

	public ScopeEstimatorProvider(final ContextProviderService contextProviderService, final ReleaseEstimatorProvider releaseEstimatorProvider) {
		this.releaseEstimatorProvider = releaseEstimatorProvider;
		contextProviderService.addContextLoadListener(this);
	}

	public ScopeEstimator get() {
		return scopeEstimator == null ? scopeEstimator = new ScopeEstimator(releaseEstimatorProvider.get()) : scopeEstimator;
	}

	@Override
	public void onProjectChanged(final UUID projectId) {
		scopeEstimator = null;
	}
}
