package br.com.oncast.ontrack.client.services.storage;

import br.com.oncast.ontrack.client.services.actionSync.ActionSyncEntry;
import br.com.oncast.ontrack.client.ui.settings.ViewSettings.ScopeTreeColumn;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.metrics.OnTrackRealTimeServerMetricsBag;
import br.com.oncast.ontrack.shared.services.metrics.OnTrackServerStatisticsBag;

import java.util.List;

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

	void storeOnTrackRealTimeServerMetricsList(OnTrackRealTimeServerMetricsBag statisticsBag);

	OnTrackRealTimeServerMetricsBag loadOnTrackRealTimeServerMetricsList();

	OnTrackServerStatisticsBag loadOnTrackServerStatisticsList();

	void storeOnTrackServerStatisticsList(OnTrackServerStatisticsBag statistics);

	List<ActionSyncEntry> loadActionSyncEntries();

	void storeActionSyncEntries(List<ActionSyncEntry> entries);

}
