package br.com.oncast.ontrack.shared.services.metrics;

import java.util.Date;

public interface UserUsageData {

	void setAuthorizedProjectsCount(int projectsCount);

	int getAuthorizedProjectsCount();

	void setInvitationTimestamp(Date timestamp);

	Date getInvitationTimestamp();

	void setSubmittedActionsCount(long count);

	long getSubmittedActionsCount();

	void setLastActionTimestamp(Date timestamp);

	Date getLastActionTimestamp();

	void setInvitedUsersCount(long count);

	long getInvitedUsersCount();

	void setUserEmail(String email);

	String getUserEmail();

	void setUserId(String id);

	String getUserId();

}
