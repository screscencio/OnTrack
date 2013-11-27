package br.com.oncast.ontrack.server.services.storage;

import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.server.configuration.Configurations;
import br.com.oncast.ontrack.server.services.storage.upload.UploadResponseFactory;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadBase.FileSizeLimitExceededException;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.google.common.io.Files;

public class FileUploadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(FileUploadServlet.class);
	private static final String MAX_SIZE_LIMIT = FileUtils.byteCountToDisplaySize(Configurations.get().getMaxFileSizeInBytes());
	private StorageService storageService;

	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		configureResponse(response);
		try {
			final FileUploadFormObject fileUploadForm = parse(request);

			final FileRepresentation fileRepresentation = getStorageService().store(fileUploadForm.getFileId(), fileUploadForm.getProjectId(),
					fileUploadForm.getFile(Files.createTempDir()));

			writeResponse(response.getOutputStream(), UploadResponseFactory.success(fileRepresentation));
		}
		catch (final FileSizeLimitExceededException e) {
			LOGGER.error("File exceeded max size limit", e);
			writeResponse(response.getOutputStream(), UploadResponseFactory.fileSizeExeededMaxLimit(MAX_SIZE_LIMIT));
		}
		catch (final Exception e) {
			LOGGER.error("File upload failed", e);
			throw new ServletException(e);
		}
	}

	private void writeResponse(final OutputStream out, final String jsonResponse) throws IOException {
		out.write(jsonResponse.getBytes());
		out.flush();
		out.close();
	}

	private void configureResponse(final HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("application/json");
	}

	@SuppressWarnings("rawtypes")
	private FileUploadFormObject parse(final HttpServletRequest request) throws FileUploadException {
		final Iterator iterator = getServletFileUpload().parseRequest(request).iterator();

		final FileUploadFormObject formObject = new FileUploadFormObject();
		while (iterator.hasNext() && !formObject.isComplete()) {
			formObject.parseField((FileItem) iterator.next());
		}

		return formObject;
	}

	private ServletFileUpload getServletFileUpload() {
		final ServletFileUpload handler = new ServletFileUpload(new DiskFileItemFactory());
		handler.setFileSizeMax(Configurations.get().getMaxFileSizeInBytes());
		return handler;
	}

	private StorageService getStorageService() {
		if (storageService == null) {
			storageService = ServerServiceProvider.getInstance().getStorageService()
					.setBaseDirectory(new File(Configurations.get().getStorageBaseDir()));
		}
		return storageService;
	}
}
