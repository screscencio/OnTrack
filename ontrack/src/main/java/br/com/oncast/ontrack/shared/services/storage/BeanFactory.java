package br.com.oncast.ontrack.shared.services.storage;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

public interface BeanFactory extends AutoBeanFactory {
	AutoBean<UploadResponse> response();
}
