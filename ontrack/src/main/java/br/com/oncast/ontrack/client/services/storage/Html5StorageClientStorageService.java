package br.com.oncast.ontrack.client.services.storage;

import static br.com.oncast.ontrack.client.services.storage.ClientStorageColumnNames.SELECTED_SCOPE_ID;
import br.com.oncast.ontrack.client.services.authentication.AuthenticationService;
import br.com.oncast.ontrack.client.ui.settings.ViewSettings.ScopeTreeColumn;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

import com.google.gwt.storage.client.Storage;

public class Html5StorageClientStorageService implements ClientStorageService {

	private static final String SEPARATOR = ".";
	private static final String PREFIX = "OnTrack" + SEPARATOR;

	private final Storage storage;
	private final AuthenticationService authenticationService;

	public Html5StorageClientStorageService(final AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
		storage = Storage.getLocalStorageIfSupported();
	}

	@Override
	public UUID loadSelectedScopeId(final UUID defaultValue) {
		final String item = getUserSpecificItem(SELECTED_SCOPE_ID);
		return item == null ? defaultValue : new UUID(item);
	}

	@Override
	public void storeSelectedScopeId(final UUID scopeId) {
		setUserSpecificItem(SELECTED_SCOPE_ID, scopeId.toStringRepresentation());
	}

	@Override
	public boolean loadScopeTreeColumnVisibility(final ScopeTreeColumn column) {
		final String item = getUserSpecificItem(getScopeTreeColumnVisibilityKey(column));
		return item == null ? column.getDefaultValue() : Boolean.valueOf(item);
	}

	@Override
	public void storeScopeTreeColumnVisibility(final ScopeTreeColumn column, final boolean value) {
		setUserSpecificItem(getScopeTreeColumnVisibilityKey(column), String.valueOf(value));
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

	private String getScopeTreeColumnVisibilityKey(final ScopeTreeColumn column) {
		return ClientStorageColumnNames.SCOPE_TREE_COLUMN_VISIBILITY + SEPARATOR + column.name();
	}

	private String getUserSpecificItem(final String key) {
		if (storage == null) return null;

		return storage.getItem(getCurrentUserStorageKey(key));
	}

	private void setUserSpecificItem(final String key, final String value) {
		if (storage == null) return;

		storage.setItem(getCurrentUserStorageKey(key), value);
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
		return getApplicationKey(authenticationService.getCurrentUser().getEmail() + SEPARATOR + key);
	}

}
