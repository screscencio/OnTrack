package br.com.oncast.ontrack.client.ui.components.releasepanel.events;

import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeDetailUpdateEvent;
import br.com.oncast.ontrack.client.ui.components.scopetree.events.ScopeDetailUpdateEventHandler;
import br.com.oncast.ontrack.shared.model.annotation.Annotation;
import br.com.oncast.ontrack.shared.model.scope.Scope;
import br.com.oncast.ontrack.utils.model.ScopeTestUtils;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import static br.com.oncast.ontrack.shared.model.annotation.AnnotationType.OPEN_IMPEDIMENT;
import static br.com.oncast.ontrack.shared.model.annotation.DeprecationState.DEPRECATED;
import static br.com.oncast.ontrack.shared.model.annotation.DeprecationState.VALID;
import static br.com.oncast.ontrack.utils.model.AnnotationTestUtils.createAnnotation;

public class AbstractSubjectDetailUpdateEventTest {

	private AbstractSubjectDetailUpdateEvent<Scope, ScopeDetailUpdateEventHandler> event;
	private List<Annotation> annotations;
	private List<Annotation> validOpenImpediments;

	@Before
	public void setup() throws Exception {
		event = new ScopeDetailUpdateEvent(ScopeTestUtils.createScope());
		annotations = new ArrayList<Annotation>();

		final Annotation validAnnotation1 = createAnnotation(OPEN_IMPEDIMENT, VALID);
		final Annotation validAnnotation2 = createAnnotation(OPEN_IMPEDIMENT, VALID);

		annotations.add(createAnnotation(OPEN_IMPEDIMENT, DEPRECATED));
		annotations.add(validAnnotation1);
		annotations.add(createAnnotation(OPEN_IMPEDIMENT, DEPRECATED));
		annotations.add(createAnnotation(OPEN_IMPEDIMENT, DEPRECATED));
		annotations.add(validAnnotation2);

		validOpenImpediments = new ArrayList<Annotation>();
		validOpenImpediments.add(validAnnotation1);
		validOpenImpediments.add(validAnnotation2);

		event.setAnnotations(annotations);
	}

	@Test
	public void shouldNotConsiderDeprecatedImpedimentsAsOpenImpediments() throws Exception {
		// Given
		final List<Annotation> annotations = new ArrayList<Annotation>();
		annotations.add(createAnnotation(OPEN_IMPEDIMENT, DEPRECATED));
		event.setAnnotations(annotations);

		// When
		final boolean actual = event.hasOpenImpediments();

		// Then
		assertFalse(actual);
	}

	@Test
	public void shouldCountTwoWhenTwoImpedimentsAreNotDeprecated() throws Exception {
		assertEquals(validOpenImpediments.size(), event.getOpenImpedimentsCount());
	}

	@Test
	public void shouldGetOnlyValidOpenImpediments() throws Exception {
		assertEquals(validOpenImpediments, event.getOpenImpediments());
	}

}
