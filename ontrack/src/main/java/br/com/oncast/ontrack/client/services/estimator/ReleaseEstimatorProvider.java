package br.com.oncast.ontrack.client.services.estimator;

import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.context.ContextProviderServiceImpl.ContextChangeListener;
import br.com.oncast.ontrack.shared.model.release.ReleaseEstimator;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ReleaseEstimatorProvider implements ContextChangeListener {

	private final ContextProviderService contextProviderService;

	private ReleaseEstimator releaseEstimator;

	public ReleaseEstimatorProvider(final ContextProviderService contextProviderService) {
		this.contextProviderService = contextProviderService;
		contextProviderService.addContextLoadListener(this);
	}

	public ReleaseEstimator get() {
		return releaseEstimator == null ? releaseEstimator = new ReleaseEstimator(contextProviderService.getCurrent().getProjectRelease()) : releaseEstimator;
	}

	@Override
	public void onProjectChanged(final UUID projectId, final Long lastActionId) {
		releaseEstimator = null;
	}
}
