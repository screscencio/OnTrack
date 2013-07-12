package br.com.oncast.ontrack.server.services.storage;

import br.com.oncast.ontrack.server.business.ServerServiceProvider;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.shared.services.storage.FileUploadFieldNames;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;

import com.google.common.io.Files;

public class FileDownloadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = Logger.getLogger(FileDownloadServlet.class);

	@Override
	protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		try {
			final String fileId = request.getParameter(FileUploadFieldNames.FILE_ID);
			if (fileId == null) throw new RuntimeException("Thre request is NOT complete, maybe there are missing request parameters.");

			final File file = ServerServiceProvider.getInstance().getStorageService().retrieve(new UUID(fileId));

			final ServletContext context = getServletConfig().getServletContext();
			String mimetype = context.getMimeType(file.getAbsolutePath());
			mimetype = (mimetype != null) ? mimetype : "application/octet-stream";

			if (acceptMimetype(mimetype, request)) doDownload(request, response, file, mimetype);
			else response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE);

			LOGGER.debug("File download succeded.");
		} catch (final Exception e) {
			LOGGER.error("File download failed", e);
			throw new ServletException(e);
		}
	}

	private boolean acceptMimetype(final String mimetype, final HttpServletRequest request) {
		final String acceptedTypes = request.getParameter(FileUploadFieldNames.ACCEPT);
		if (acceptedTypes == null || acceptedTypes.trim().isEmpty()) return true;

		for (final String type : acceptedTypes.trim().toLowerCase().split(",")) {
			if (!type.trim().isEmpty() && mimetype.contains(type)) return true;
		}
		return false;
	}

	private void doDownload(final HttpServletRequest request, final HttpServletResponse response, final File file, final String mimetype) throws IOException {
		response.setContentType(mimetype);
		response.setContentLength((int) file.length());
		response.setHeader("Content-Disposition", "inline; filename=\"" + encodeFileName(file.getName()) + "\"");

		Files.copy(file, response.getOutputStream());
	}

	private String encodeFileName(final String fileName) throws UnsupportedEncodingException {
		final StringBuffer buffer = new StringBuffer();
		for (final String split : fileName.split(" ")) {
			buffer.append(URLEncoder.encode(split, "UTF-8"));
			buffer.append(" ");
		}
		return buffer.toString().trim();
	}
}
