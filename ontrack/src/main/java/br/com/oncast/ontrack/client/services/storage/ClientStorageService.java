package br.com.oncast.ontrack.client.services.storage;

import java.util.List;

import br.com.oncast.ontrack.client.ui.settings.ViewSettings.ScopeTreeColumn;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.metrics.OnTrackServerMetricsBag;

public interface ClientStorageService {

	UUID loadSelectedScopeId(UUID defaultValue);

	void storeSelectedScopeId(UUID scopeId);

	boolean loadScopeTreeColumnVisibility(ScopeTreeColumn column);

	void storeScopeTreeColumnVisibility(ScopeTreeColumn column, boolean isVisible);

	String loadLastUserEmail(String defaultValue);

	void storeLastUserEmail(String email);

	void storeReleaseContainerState(Release release, boolean containerState);

	List<UUID> loadModifiedContainerStateReleases();

	void storeDefaultPlaceToken(String placeToken);

	String loadDefaultPlaceToken();

	void appendOnTrackServerMetrics(OnTrackServerMetricsBag statisticsBag);

	OnTrackServerMetricsBag loadOnTrackServerMetricsList();

}
