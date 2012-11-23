package br.com.oncast.ontrack.shared.model.project;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import br.com.oncast.ontrack.shared.model.tags.HasTags;
import br.com.oncast.ontrack.shared.model.tags.Tag;
import br.com.oncast.ontrack.shared.model.tags.TagType;
import br.com.oncast.ontrack.shared.model.uuid.UUID;
import br.com.oncast.ontrack.utils.model.ProjectTestUtils;

public class ProjectTest {

	private Project project;

	@Mock
	private Tag tag;

	@Mock
	private HasTags subject;

	private TagType tagType;

	private UUID tagId;

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		project = ProjectTestUtils.createProject();

		tagId = new UUID();
		tagType = TagType.USER;
		when(tag.getSubject()).thenReturn(subject);
		when(tag.getId()).thenReturn(tagId);
		when(tag.getTagType()).thenReturn(tagType);

		when(subject.getId()).thenReturn(new UUID());
	}

	@Test
	public void shouldBeAbleToGetTagsList() throws Exception {
		project.addTag(tag);

		final List<Tag> tagsList = project.getTagsList(subject, tagType);
		assertEquals(1, tagsList.size());
		assertEquals(tag, tagsList.get(0));
	}

	@Test
	public void shouldBeAbleToFindATagById() throws Exception {
		project.addTag(tag);

		assertEquals(tag, project.findTag(subject, tagType, tagId));
	}

	@Test
	public void shouldBeAbleToKnowIfASubjectHasTags() throws Exception {
		assertFalse(project.hasTags(subject));

		project.addTag(tag);

		assertTrue(project.hasTags(subject));
	}

	@Test
	public void removedTagsDoesNotCount() throws Exception {
		project.addTag(tag);
		assertTrue(project.hasTags(subject));

		project.removeTag(tag);
		assertFalse(project.hasTags(subject));
	}

}
