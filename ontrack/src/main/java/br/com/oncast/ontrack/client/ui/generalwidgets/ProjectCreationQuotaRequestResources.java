package br.com.oncast.ontrack.client.ui.generalwidgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface ProjectCreationQuotaRequestResources extends ClientBundle {

	static ProjectCreationQuotaRequestResources INSTANCE = GWT.create(ProjectCreationQuotaRequestResources.class);

	ImageResource quotaRequestIcon();
}
