package br.com.oncast.ontrack.server.services.storage;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.log4j.Logger;

import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.server.configuration.Configurations;
import br.com.oncast.ontrack.shared.model.file.FileRepresentation;

import com.google.common.io.Files;

public class FileUploadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(FileUploadServlet.class);
	private StorageService storageService;

	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		try {
			final FileUploadFormObject fileUploadForm = parse(request);

			final FileRepresentation fileRepresentation = getStorageService().store(fileUploadForm.getFileId(), fileUploadForm.getProjectId(),
					fileUploadForm.getFile(Files.createTempDir()));

			configureResponse(response);

			writeResponse(response.getOutputStream(), fileRepresentation);
		}
		catch (final Exception e) {
			LOGGER.error("File upload failed", e);
			throw new ServletException(e);
		}
	}

	private void writeResponse(final OutputStream out, final FileRepresentation fileRepresentation) throws IOException {
		out.write(fileRepresentation.getId().toStringRepresentation().getBytes());
		out.flush();
		out.close();
	}

	private void configureResponse(final HttpServletResponse response) {
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/plain");
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
		handler.setFileSizeMax(Configurations.getInstance().getMaxFileSizeInBytes());
		return handler;
	}

	private StorageService getStorageService() {
		if (storageService == null) {
			storageService = ServerServiceProvider.getInstance().getStorageService()
					.setBaseDirectory(new File(Configurations.getInstance().getStorageBaseDir()));
		}
		return storageService;
	}
}
