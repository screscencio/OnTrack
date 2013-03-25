package br.com.oncast.ontrack.client.services.estimator;

import br.com.oncast.ontrack.client.services.context.ContextProviderService;
import br.com.oncast.ontrack.client.services.context.ContextProviderServiceImpl.ContextChangeListener;
import br.com.oncast.ontrack.shared.model.release.ReleaseEstimator;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ReleaseEstimatorProvider {

	private final ContextProviderService contextProviderService;
	private ReleaseEstimator releaseEstimator;

	public ReleaseEstimatorProvider(final ContextProviderService contextProviderService) {
		this.contextProviderService = contextProviderService;
		contextProviderService.addContextLoadListener(new ContextChangeListener() {

			@Override
			public void onProjectChanged(final UUID projectId) {
				releaseEstimator = null;
			}
		});
	}

	public ReleaseEstimator get() {
		return releaseEstimator == null ? releaseEstimator = new ReleaseEstimator(contextProviderService.getCurrent().getProjectRelease()) : releaseEstimator;
	}
}
