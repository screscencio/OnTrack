package br.com.oncast.ontrack.shared.model.project;

import br.com.oncast.ontrack.shared.model.metadata.HasMetadata;
import br.com.oncast.ontrack.shared.model.metadata.Metadata;
import br.com.oncast.ontrack.shared.model.metadata.MetadataType;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.model.ProjectTestUtils;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.when;

import static junit.framework.Assert.assertEquals;

public class ProjectTest {

	private Project project;

	@Mock
	private Metadata metadata;

	@Mock
	private HasMetadata subject;

	private MetadataType metadataType;

	private UUID metadataId;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		project = ProjectTestUtils.createProject();

		metadataId = new UUID();
		metadataType = MetadataType.USER;
		when(metadata.getSubject()).thenReturn(subject);
		when(metadata.getId()).thenReturn(metadataId);
		when(metadata.getMetadataType()).thenReturn(metadataType);

		when(subject.getId()).thenReturn(new UUID());
	}

	@Test
	public void shouldBeAbleToGetTagsList() throws Exception {
		project.addMetadata(metadata);

		final List<Metadata> metadataList = project.getMetadataList(subject, metadataType);
		assertEquals(1, metadataList.size());
		assertEquals(metadata, metadataList.get(0));
	}

	@Test
	public void shouldBeAbleToFindATagById() throws Exception {
		project.addMetadata(metadata);

		assertEquals(metadata, project.findMetadata(subject, metadataType, metadataId));
	}

	@Test
	public void shouldBeAbleToKnowIfASubjectHasTags() throws Exception {
		assertFalse(project.hasMetadata(subject));

		project.addMetadata(metadata);

		assertTrue(project.hasMetadata(subject));
	}

	@Test
	public void removedTagsDoesNotCount() throws Exception {
		project.addMetadata(metadata);
		assertTrue(project.hasMetadata(subject));

		project.removeMetadata(metadata);
		assertFalse(project.hasMetadata(subject));
	}

}
