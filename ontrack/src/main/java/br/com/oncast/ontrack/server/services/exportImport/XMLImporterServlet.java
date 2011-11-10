package br.com.oncast.ontrack.server.services.exportImport;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.dom4j.Document;

import br.com.oncast.ontrack.server.services.authentication.basic.BasicAutheticator;
import br.com.oncast.ontrack.server.services.exportImport.xml.XMLUtils;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.OntrackMigrationManager;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.XMLImporter;
import br.com.oncast.ontrack.server.services.persistence.exceptions.PersistenceException;

public class XMLImporterServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static final String SOURCE_XML_FILE_NAME = "ontrack.xml";
	private static final String MIGRATED_XML_FILE_NAME = "migrated.xml";

	@Override
	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		try {
			BasicAutheticator.authenticate(request);
			doReply(request, response);
		}
		catch (final Exception e) {
			// TODO Display an user-friendly error message.
			throw new ServletException(e);
		}
	}

	private void doReply(final HttpServletRequest request, final HttpServletResponse response) throws Exception {
		final File sourceXML = extractXMLFromRequest(request);
		final File migratedXML = migrate(sourceXML);
		updateDatabase(migratedXML);
	}

	@SuppressWarnings("rawtypes")
	private File extractXMLFromRequest(final HttpServletRequest request) throws FileUploadException, Exception {
		final ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
		final Iterator iterator = upload.parseRequest(request).iterator();

		while (iterator.hasNext()) {
			final FileItem fileItem = (FileItem) iterator.next();
			if (!fileItem.isFormField()) return toFile(fileItem);
		}

		throw new RuntimeException("Failed to extract xml: there is no xml attached to this request.");
	}

	private File toFile(final FileItem item) throws Exception {
		final File xmlFile = new File(SOURCE_XML_FILE_NAME);
		item.write(xmlFile);

		return xmlFile;
	}

	private File migrate(final File sourceXML) throws Exception {
		final Document document = XMLUtils.read(sourceXML);

		OntrackMigrationManager.applyMigrationsOn(document);

		final File migratedXML = new File(MIGRATED_XML_FILE_NAME);
		XMLUtils.write(document, migratedXML);

		return migratedXML;
	}

	private void updateDatabase(final File xmlFile) throws PersistenceException {
		final XMLImporter xmlImporter = new XMLImporter();
		xmlImporter.loadXML(xmlFile).persistObjects();
	}

}
