package br.com.oncast.ontrack.server.services.exportImport;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import br.com.oncast.ontrack.server.services.authentication.basic.BasicAutheticator;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.XMLImporter;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;

public class XMLImporterServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		try {
			BasicAutheticator.authenticate(request);
			doReply(request, response);
		}
		catch (final Exception e) {
			doHandleError(request, response, e);
		}
	}

	@SuppressWarnings("rawtypes")
	private void doReply(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
		final Iterator iter = upload.parseRequest(request).iterator();

		while (iter.hasNext()) {
			final FileItem fileItem = (FileItem) iter.next();
			if (!fileItem.isFormField()) updateDatabase(convertToFile(fileItem));
		}
	}

	// TODO Display an user-friendly error message.
	private void doHandleError(final HttpServletRequest request, final HttpServletResponse response, final Exception e) throws ServletException {
		throw new ServletException(e);
	}

	private File convertToFile(final FileItem item) throws Exception {
		final File xmlFile = new File("ontrack.xml");
		item.write(xmlFile);

		return xmlFile;
	}

	private void updateDatabase(final File xmlFile) throws PersistenceException {
		final XMLImporter xmlImporter = new XMLImporter();
		xmlImporter.loadXML(xmlFile).persistObjects();
	}
}
