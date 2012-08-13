package br.com.oncast.ontrack.server.business.actionPostProcessments;

import java.util.Date;

import junit.framework.Assert;

import org.junit.Test;
import org.mockito.Mockito;

import br.com.oncast.ontrack.shared.model.action.ActionContext;
import br.com.oncast.ontrack.shared.model.action.ScopeDeclareProgressAction;
import br.com.oncast.ontrack.shared.model.project.ProjectContext;
import br.com.oncast.ontrack.shared.model.uuid.UUID;

public class ScopeDeclareProgressPostProcessorTest {

	@Test
	public void postProcessingShouldUpdateActionTimestampWithActionContextTimestamp() {
		final ScopeDeclareProgressPostProcessor postProcessor = new ScopeDeclareProgressPostProcessor();

		final Date actionTimestamp = new Date(1l);
		final Date actionContextTimestamp = new Date(10l);

		final ScopeDeclareProgressAction action = new ScopeDeclareProgressAction(new UUID(), "description");
		action.setTimestamp(actionTimestamp);

		final ActionContext actionContextMock = Mockito.mock(ActionContext.class);
		Mockito.when(actionContextMock.getTimestamp()).thenReturn(actionContextTimestamp);

		postProcessor.process(action, actionContextMock, Mockito.mock(ProjectContext.class));

		final Date timestamp = action.getTimestamp();
		Assert.assertFalse(actionTimestamp.equals(timestamp));
		Assert.assertEquals(actionContextTimestamp, timestamp);
	}
}
