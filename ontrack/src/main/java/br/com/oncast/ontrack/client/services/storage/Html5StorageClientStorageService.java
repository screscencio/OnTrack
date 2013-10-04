package br.com.oncast.ontrack.client.services.storage;

import br.com.oncast.ontrack.client.services.authentication.AuthenticationService;
import br.com.oncast.ontrack.client.services.context.ProjectRepresentationProvider;
import br.com.oncast.ontrack.client.ui.settings.DefaultViewSettings;
import br.com.oncast.ontrack.client.ui.settings.ViewSettings.ScopeTreeColumn;
import br.com.oncast.ontrack.shared.model.action.ModelAction;
import br.com.oncast.ontrack.shared.model.action.ModelActionSerializer;
import br.com.oncast.ontrack.shared.model.release.Release;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.metrics.OnTrackServerMetrics;
import br.com.oncast.ontrack.shared.services.metrics.OnTrackServerMetricsBag;
import br.com.oncast.ontrack.shared.services.metrics.OnTrackStatisticsFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Joiner;
import com.google.gwt.core.client.GWT;
import com.google.gwt.storage.client.Storage;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import static br.com.oncast.ontrack.client.services.storage.ClientStorageColumnNames.SELECTED_SCOPE_ID;

public class Html5StorageClientStorageService implements ClientStorageService {

	private static final String LIST_SEPARATOR = "__,__";

	private static final OnTrackStatisticsFactory FACTORY = GWT.create(OnTrackStatisticsFactory.class);

	private static final String KEY_SEPARATOR = ".";

	private static final String PREFIX = "OnTrack" + KEY_SEPARATOR;

	private final Storage storage;
	private final AuthenticationService authenticationService;
	private final ProjectRepresentationProvider projectRepresentationProvider;
	private final Map<String, List<String>> resultsListCache;

	public Html5StorageClientStorageService(final AuthenticationService authenticationService, final ProjectRepresentationProvider projectRepresentationProvider) {
		this.authenticationService = authenticationService;
		this.projectRepresentationProvider = projectRepresentationProvider;
		storage = Storage.getLocalStorageIfSupported();
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
		final String userProjectSpecificKey = getUserProjectSpecificItem(ClientStorageColumnNames.MODIFIED_CONTAINER_STATE_RELEASES);
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

		for (final String idString : getList(getUserProjectSpecificItem(ClientStorageColumnNames.MODIFIED_CONTAINER_STATE_RELEASES))) {
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
			resultsListCache.put(key, new ArrayList<String>(Arrays.asList(item.split(LIST_SEPARATOR))));
		}

		return resultsListCache.get(key);
	}

	private void storeList(final String key) {
		final List<String> list = getList(key);
		final String resultString = Joiner.on(LIST_SEPARATOR).join(list);
		setItem(key, resultString);
	}

	private String getScopeTreeColumnVisibilityKey(final ScopeTreeColumn column) {
		return ClientStorageColumnNames.SCOPE_TREE_COLUMN_VISIBILITY + KEY_SEPARATOR + column.name();
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
		return getApplicationKey(authenticationService.getCurrentUserId() + KEY_SEPARATOR + key);
	}

	private String getUserProjectStorageKey(final String key) {
		if (!authenticationService.isUserAvailable()) throw new RuntimeException("There is no user available for user dependant storage operation");
		return getApplicationKey(authenticationService.getCurrentUserId() + KEY_SEPARATOR + projectRepresentationProvider.getCurrent().getId() + KEY_SEPARATOR + key);
	}

	@Override
	public void appendOnTrackServerMetrics(final OnTrackServerMetricsBag metrics) {
		final String key = getCurrentUserStorageKey(ClientStorageColumnNames.SERVER_STATISTICS);
		setItem(key, serialize(metrics));
	}

	@Override
	public OnTrackServerMetricsBag loadOnTrackServerMetricsList() {
		final String key = getCurrentUserStorageKey(ClientStorageColumnNames.SERVER_STATISTICS);
		final String item = getItem(key);
		if (item == null || item.isEmpty()) {
			final OnTrackServerMetricsBag bag = FACTORY.createOnTrackServerMetricsBag().as();
			bag.setStatisticsList(new ArrayList<OnTrackServerMetrics>());
			return bag;
		}

		return deserialize(item);
	}

	public static String serialize(final OnTrackServerMetricsBag serializable) {
		final AutoBean<OnTrackServerMetricsBag> bean = AutoBeanUtils.getAutoBean(serializable);
		return AutoBeanCodex.encode(bean).getPayload();
	}

	public static OnTrackServerMetricsBag deserialize(final String json) {
		final AutoBean<OnTrackServerMetricsBag> bean = AutoBeanCodex.decode(FACTORY, OnTrackServerMetricsBag.class, json);
		return bean.as();
	}

	@Override
	public void savePendingActions(final List<ModelAction> pendingActions) {
		final String key = getUserProjectStorageKey(ClientStorageColumnNames.PENDING_ACTIONS);
		final List<String> serialized = new ArrayList<String>();

		for (final ModelAction action : pendingActions)
			serialized.add(ModelActionSerializer.serialize(action));

		resultsListCache.put(key, serialized);
		storeList(key);
	}

	@Override
	public List<ModelAction> loadPendingActions() {
		final String key = getUserProjectStorageKey(ClientStorageColumnNames.PENDING_ACTIONS);
		final List<String> serialized = getList(key);

		final List<ModelAction> pendingActions = new ArrayList<ModelAction>();
		for (final String s : serialized) {
			final ModelAction m = ModelActionSerializer.deSerialize(s);
			if (m != null) pendingActions.add(m);
		}

		return pendingActions;
	}

}
