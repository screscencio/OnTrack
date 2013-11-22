package br.com.oncast.ontrack.client.services.storage;

import br.com.oncast.ontrack.client.services.actionSync.ActionSyncEntry;
import br.com.oncast.ontrack.client.services.authentication.AuthenticationService;
import br.com.oncast.ontrack.client.services.context.ProjectRepresentationProvider;
import br.com.oncast.ontrack.client.ui.settings.DefaultViewSettings;
import br.com.oncast.ontrack.client.ui.settings.ViewSettings.ScopeTreeColumn;
import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.NullAction;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.metrics.OnTrackRealTimeServerMetrics;
import br.com.oncast.ontrack.shared.services.metrics.OnTrackRealTimeServerMetricsBag;
import br.com.oncast.ontrack.shared.services.metrics.OnTrackServerStatistics;
import br.com.oncast.ontrack.shared.services.metrics.OnTrackServerStatisticsBag;
import br.com.oncast.ontrack.shared.services.metrics.OnTrackStatisticsFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Joiner;
import com.google.gwt.core.client.GWT;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.rpc.SerializationException;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.seanchenxi.gwt.storage.client.StorageExt;
import com.seanchenxi.gwt.storage.client.StorageKey;
import com.seanchenxi.gwt.storage.client.StorageKeyFactory;
import com.seanchenxi.gwt.storage.client.StorageQuotaExceededException;

import static br.com.oncast.ontrack.client.services.storage.ClientStorageColumnNames.SELECTED_SCOPE_ID;

public class Html5StorageClientStorageService implements ClientStorageService {

	private static final String SEPARATOR = ".";
	private static final String PREFIX = "OnTrack" + SEPARATOR;

	private static final OnTrackStatisticsFactory FACTORY = GWT.create(OnTrackStatisticsFactory.class);

	private final Storage storage;
	private final AuthenticationService authenticationService;
	private final ProjectRepresentationProvider projectRepresentationProvider;
	private final Map<String, List<String>> resultsListCache;
	private final StorageExt gwtStorage;

	public Html5StorageClientStorageService(final AuthenticationService authenticationService, final ProjectRepresentationProvider projectRepresentationProvider) {
		this.authenticationService = authenticationService;
		this.projectRepresentationProvider = projectRepresentationProvider;
		storage = Storage.getLocalStorageIfSupported();
		gwtStorage = StorageExt.getLocalStorage();
		resultsListCache = new HashMap<String, List<String>>();
	}

	@Override
	public UUID loadSelectedScopeId(final UUID defaultValue) {
		final String item = getUserProjectSpecificItem(SELECTED_SCOPE_ID);
		return item == null ? defaultValue : new UUID(item);
	}

	@Override
	public void storeSelectedScopeId(final UUID scopeId) {
		setUserProjectSpecificItem(SELECTED_SCOPE_ID, scopeId.toString());
	}

	@Override
	public boolean loadScopeTreeColumnVisibility(final ScopeTreeColumn column) {
		final String item = getUserProjectSpecificItem(getScopeTreeColumnVisibilityKey(column));
		return item == null ? column.getDefaultValue() : Boolean.valueOf(item);
	}

	@Override
	public void storeScopeTreeColumnVisibility(final ScopeTreeColumn column, final boolean value) {
		setUserProjectSpecificItem(getScopeTreeColumnVisibilityKey(column), String.valueOf(value));
	}

	@Override
	public String loadLastUserEmail(final String defaultValue) {
		final String item = getItem(ClientStorageColumnNames.LAST_USER_EMAIL);
		return item == null ? defaultValue : item;
	}

	@Override
	public void storeLastUserEmail(final String email) {
		setItem(ClientStorageColumnNames.LAST_USER_EMAIL, email);
	}

	@Override
	public void storeReleaseContainerState(final Release release, final boolean containerState) {
		final String userProjectSpecificKey = getUserProjectStorageKey(ClientStorageColumnNames.MODIFIED_CONTAINER_STATE_RELEASES);
		final List<String> modifiedReleases = getList(userProjectSpecificKey);

		final boolean hasBeenModified = containerState != DefaultViewSettings.RELEASE_PANEL_CONTAINER_STATE;

		final String releaseId = release.getId().toString();
		if (modifiedReleases.contains(releaseId) == hasBeenModified) return;

		if (hasBeenModified) modifiedReleases.add(releaseId);
		else modifiedReleases.remove(releaseId);

		storeList(userProjectSpecificKey);
	}

	@Override
	public List<UUID> loadModifiedContainerStateReleases() {
		final List<UUID> modifiedReleases = new ArrayList<UUID>();

		for (final String idString : getList(getUserProjectStorageKey(ClientStorageColumnNames.MODIFIED_CONTAINER_STATE_RELEASES))) {
			modifiedReleases.add(new UUID(idString));
		}

		return modifiedReleases;
	}

	@Override
	public void storeDefaultPlaceToken(final String placeToken) {
		setItem(ClientStorageColumnNames.DEFAULT_PLACE, placeToken);
	}

	@Override
	public String loadDefaultPlaceToken() {
		return getItem(ClientStorageColumnNames.DEFAULT_PLACE);
	}

	private List<String> getList(final String key) {
		if (!resultsListCache.containsKey(key)) {
			String item = getItem(key);
			if (item == null) item = "";
			resultsListCache.put(key, new ArrayList<String>(Arrays.asList(item.split(","))));
		}

		return resultsListCache.get(key);
	}

	private void storeList(final String key) {
		final List<String> list = getList(key);
		final String resultString = Joiner.on(',').join(list);
		setItem(key, resultString);
	}

	private String getScopeTreeColumnVisibilityKey(final ScopeTreeColumn column) {
		return ClientStorageColumnNames.SCOPE_TREE_COLUMN_VISIBILITY + SEPARATOR + column.name();
	}

	private String getUserProjectSpecificItem(final String key) {
		if (storage == null) return null;

		return storage.getItem(getUserProjectStorageKey(key));
	}

	private void setUserProjectSpecificItem(final String key, final String value) {
		if (storage == null) return;

		storage.setItem(getUserProjectStorageKey(key), value);
	}

	private String getItem(final String key) {
		if (storage == null) return null;

		return storage.getItem(getApplicationKey(key));
	}

	private void setItem(final String key, final String value) {
		if (storage == null) return;

		storage.setItem(getApplicationKey(key), value);
	}

	private String getApplicationKey(final String key) {
		return PREFIX + key;
	}

	private String getCurrentUserStorageKey(final String key) {
		if (!authenticationService.isUserAvailable()) throw new RuntimeException("There is no user available for user dependant storage operation");
		return getApplicationKey(authenticationService.getCurrentUserId() + SEPARATOR + key);
	}

	private String getUserProjectStorageKey(final String key) {
		if (!authenticationService.isUserAvailable()) throw new RuntimeException("There is no user available for user dependant storage operation");
		return getApplicationKey(authenticationService.getCurrentUserId() + SEPARATOR + projectRepresentationProvider.getCurrent().getId() + SEPARATOR + key);
	}

	@Override
	public void storeOnTrackRealTimeServerMetricsList(final OnTrackRealTimeServerMetricsBag metrics) {
		final String key = getCurrentUserStorageKey(ClientStorageColumnNames.REAL_TIME_SERVER_METRICS);
		setItem(key, serialize(metrics));
	}

	@Override
	public OnTrackRealTimeServerMetricsBag loadOnTrackRealTimeServerMetricsList() {
		final String key = getCurrentUserStorageKey(ClientStorageColumnNames.REAL_TIME_SERVER_METRICS);
		final String item = getItem(key);
		if (item == null || item.isEmpty()) {
			final OnTrackRealTimeServerMetricsBag bag = FACTORY.createOnTrackRealTimeServerMetricsBag().as();
			bag.setOnTrackRealTimeServerMetricsList(new ArrayList<OnTrackRealTimeServerMetrics>());
			return bag;
		}

		return deserialize(item, OnTrackRealTimeServerMetricsBag.class);
	}

	@Override
	public void storeOnTrackServerStatisticsList(final OnTrackServerStatisticsBag statistics) {
		final String key = getCurrentUserStorageKey(ClientStorageColumnNames.SERVER_STATISTICS);
		setItem(key, serialize(statistics));
	}

	@Override
	public OnTrackServerStatisticsBag loadOnTrackServerStatisticsList() {
		final String key = getCurrentUserStorageKey(ClientStorageColumnNames.SERVER_STATISTICS);
		final String item = getItem(key);
		if (item == null || item.isEmpty()) {
			final OnTrackServerStatisticsBag bag = FACTORY.createOnTrackServerStatisticsBag().as();
			bag.setOnTrackServerStatisticsList(new ArrayList<OnTrackServerStatistics>());
			return bag;
		}

		return deserialize(item, OnTrackServerStatisticsBag.class);
	}

	public static <T> String serialize(final T serializable) {
		final AutoBean<T> bean = AutoBeanUtils.getAutoBean(serializable);
		return AutoBeanCodex.encode(bean).getPayload();
	}

	public static <T> T deserialize(final String json, final Class<T> type) {
		final AutoBean<T> bean = AutoBeanCodex.decode(FACTORY, type, json);
		return bean.as();
	}

	private void storePendingActions(final List<ModelAction> pendingActions) {
		if (gwtStorage == null || !authenticationService.isUserAvailable()) return;

		final String keyName = getUserProjectStorageKey(ClientStorageColumnNames.PENDING_ACTIONS_LIST);
		final StorageKey<ArrayList<ModelAction>> key = StorageKeyFactory.objectKey(keyName);
		try {
			gwtStorage.put(key, new ArrayList<ModelAction>(pendingActions));
		} catch (final SerializationException e) {
			e.printStackTrace();
		} catch (final StorageQuotaExceededException e) {
			e.printStackTrace();
		}
	}

	private List<ModelAction> loadPendingActions() {
		try {
			final String keyName = getUserProjectStorageKey(ClientStorageColumnNames.PENDING_ACTIONS_LIST);
			final StorageKey<ArrayList<ModelAction>> key = StorageKeyFactory.objectKey(keyName);
			return new ArrayList<ModelAction>(gwtStorage.get(key));
		} catch (final Exception e) {
			return new ArrayList<ModelAction>();
		}
	}

	@Override
	public List<ActionSyncEntry> loadActionSyncEntries() {
		final ArrayList<ActionSyncEntry> entries = new ArrayList<ActionSyncEntry>();
		final List<ModelAction> actions = loadPendingActions();
		for (int i = 0; i < actions.size(); i++) {
			final ModelAction action = actions.get(i);
			entries.add(new ActionSyncEntry(action, new NullAction(), new ActionContext(authenticationService.getCurrentUserId(), new Date())));
		}

		return entries;
	}

	@Override
	public void storeActionSyncEntries(final List<ActionSyncEntry> entries) {
		if (gwtStorage == null || !authenticationService.isUserAvailable()) return;

		final ArrayList<ModelAction> actions = new ArrayList<ModelAction>();
		final ArrayList<ActionContext> contexts = new ArrayList<ActionContext>();
		for (final ActionSyncEntry actionSyncEntry : entries) {
			actions.add(actionSyncEntry.getAction());
			contexts.add(actionSyncEntry.getContext());
		}
		storePendingActions(actions);

		return;
	}

}
