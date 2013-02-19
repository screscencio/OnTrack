package br.com.oncast.ontrack.shared.services.metrics;

import java.util.List;

public interface ProjectMetrics {

	void setUsersCount(int count);

	int getUsersCount();

	void setScopesCount(int count);

	int getScopesCount();

	void setScopesDepth(List<Integer> scopesDepth);

	List<Integer> getScopesDepth();

	void setReleasesCount(int count);

	int getReleasesCount();

	void setReleasesDepth(List<Integer> releasesDepth);

	List<Integer> getReleasesDepth();

	void setReleasesDuration(List<Integer> releasesDuration);

	List<Integer> getReleasesDuration();

	void setStoriesPerRelease(List<Integer> releasesStoryCount);

	List<Integer> getStoriesPerRelease();

	void setProjectName(String name);

	String getProjectName();

}
