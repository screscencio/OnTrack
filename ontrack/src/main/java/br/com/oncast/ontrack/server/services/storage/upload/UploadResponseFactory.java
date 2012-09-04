package br.com.oncast.ontrack.server.services.storage.upload;

import br.com.oncast.ontrack.shared.model.file.FileRepresentation;
import br.com.oncast.ontrack.shared.services.storage.BeanFactory;
import br.com.oncast.ontrack.shared.services.storage.UploadResponse;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.vm.AutoBeanFactorySource;

public class UploadResponseFactory {

	private static final String FILE_SIZE_LIMIT_MESSAGE = "The uploaded file exceeds the maximun size (%s)";
	private static final String SUCCESS = "success";
	private static final String ERROR = "error";
	private static BeanFactory factory = AutoBeanFactorySource.create(BeanFactory.class);

	public static String fileSizeExeededMaxLimit(final String maxSize) {
		final AutoBean<UploadResponse> bean = factory.response();
		final UploadResponse response = bean.as();
		response.setMessage(String.format(FILE_SIZE_LIMIT_MESSAGE, maxSize));
		response.setStatus(ERROR);
		return AutoBeanCodex.encode(bean).getPayload();
	}

	public static String success(final FileRepresentation fileRepresentation) {
		final AutoBean<UploadResponse> bean = factory.response();
		final UploadResponse response = bean.as();
		response.setMessage(String.format("Upload complete"));
		response.setStatus(SUCCESS);
		response.setFileId(fileRepresentation.getId().toStringRepresentation());
		return AutoBeanCodex.encode(bean).getPayload();
	}
}
