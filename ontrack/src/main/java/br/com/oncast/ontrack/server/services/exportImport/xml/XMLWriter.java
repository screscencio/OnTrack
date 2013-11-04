package br.com.oncast.ontrack.server.services.exportImport.xml;

import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.OntrackXML;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.ProjectAuthorizationXMLNode;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.ProjectXMLNode;
import br.com.oncast.ontrack.server.services.exportImport.xml.abstractions.UserXMLNode;
import br.com.oncast.ontrack.server.services.exportImport.xml.transform.CustomMatcher;
import br.com.oncast.ontrack.shared.services.notification.Notification;

import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

public class XMLWriter {

	private final OntrackXML ontrackXML;
	private static final Logger LOGGER = Logger.getLogger(XMLWriter.class);

	public XMLWriter() {
		ontrackXML = new OntrackXML();
		ontrackXML.setTimestamp(new Date());
		LOGGER.debug("Initializing parsing of OntrackXML");
	}

	public XMLWriter setUserList(final List<UserXMLNode> userList) {
		ontrackXML.setUsers(userList);
		LOGGER.debug("User list DONE!");
		return this;
	}

	public XMLWriter setVersion(final String version) {
		ontrackXML.setVersion(version);
		LOGGER.debug("Setted Version DONE!");
		return this;
	}

	public XMLWriter setProjectsList(final List<ProjectXMLNode> projectList) {
		ontrackXML.setProjects(projectList);
		LOGGER.debug("Projects List DONE!");
		return this;
	}

	public XMLWriter setProjectAuthorizationsList(final List<ProjectAuthorizationXMLNode> projectAuthorizations) {
		ontrackXML.setProjectAuthorizations(projectAuthorizations);
		LOGGER.debug("Project Authorizations DONE!");
		return this;
	}

	public XMLWriter setNotifications(final List<Notification> notifications) {
		ontrackXML.setNotifications(notifications);
		LOGGER.debug("Notifications DONE!");
		return this;
	}

	public void export(final OutputStream outputStream) {
		LOGGER.debug("Finished parsing OntrackXML");
		LOGGER.debug("Initializing XML Serialization");
		final Serializer serializer = new Persister(new CustomMatcher());

		try {
			serializer.write(ontrackXML, outputStream);
			LOGGER.debug("Finished XML Serialization");
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

}
