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

import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.XMLImporter;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;

public class XMLImporterServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	// TODO +++Authenticate user before accept this post.
	// TODO verify error treatment
	@Override
	@SuppressWarnings("rawtypes")
	protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
		final ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
		try {
			final Iterator iter = upload.parseRequest(req).iterator();
			while (iter.hasNext()) {
				final FileItem item = (FileItem) iter.next();
				if (!item.isFormField()) processUploadedFile(item);
			}
		}
		catch (final Exception e) {
			e.printStackTrace();
		}
	}

	private void processUploadedFile(final FileItem item) throws Exception {
		final File xmlFile = new File("ontrack.xml");
		item.write(xmlFile);

		updateDatabase(xmlFile);
	}

	private void updateDatabase(final File xmlFile) throws ServletException {
		final XMLImporter xmlImporter = new XMLImporter();
		try {
			xmlImporter.loadXML(xmlFile).persistObjects();
		}
		catch (final PersistenceException e) {
			throw new ServletException(e);
		}
	}
}
