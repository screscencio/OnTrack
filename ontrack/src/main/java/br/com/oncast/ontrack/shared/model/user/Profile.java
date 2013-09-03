package br.com.oncast.ontrack.shared.model.user;


public enum Profile {

	GUEST, CONTRIBUTOR, PEOPLE_MANAGER, PROJECT_MANAGER, ACCOUNT_MANAGER, SYSTEM_ADMIN;

	public boolean hasPermissionsOf(final Profile profile) {
		return this.compareTo(profile) >= 0;
	}

	public static Profile getDefaultProfile() {
		return PROJECT_MANAGER;
	}

}
