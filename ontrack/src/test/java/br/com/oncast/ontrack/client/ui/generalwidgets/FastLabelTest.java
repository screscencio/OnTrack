package br.com.oncast.ontrack.client.ui.generalwidgets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.octo.gwt.test.GwtTest;

public class FastLabelTest extends GwtTest {
	@Test
	public void shouldReturnEmptyStringIfNeverSet() {
		final FastLabel l = new FastLabel();
		assertEquals("", l.getText());
	}

	@Test
	public void shouldReturnSameSetString() throws Exception {
		final FastLabel l = new FastLabel();
		l.setText("test text");
		assertEquals("test text", l.getText());
	}

	@Test
	public void shouldAllowResetingTheText() throws Exception {
		final FastLabel l = new FastLabel();
		l.setText("previous test text");
		l.setText("test text");
		assertEquals("test text", l.getText());
	}

	@Test
	public void shouldAllowAddingStyleName() throws Exception {
		final FastLabel l = new FastLabel();
		l.addStyleName("style");
		assertTrue(l.hasStyleName("style"));
	}

	@Test
	public void shouldNotForgetAStyleNameAdded() throws Exception {
		final FastLabel l = new FastLabel();
		l.addStyleName("style");
		l.addStyleName("style1");
		l.addStyleName("style2");
		l.addStyleName("style3");
		l.addStyleName("style4");

		assertTrue(l.hasStyleName("style"));
		assertTrue(l.hasStyleName("style1"));
		assertTrue(l.hasStyleName("style2"));
		assertTrue(l.hasStyleName("style3"));
		assertTrue(l.hasStyleName("style4"));
	}

	@Test
	public void shouldAllowForgetRemovedStyleNames() throws Exception {
		final FastLabel l = new FastLabel();
		l.addStyleName("style");
		l.addStyleName("style1");
		l.addStyleName("style2");
		l.addStyleName("style3");
		l.addStyleName("style4");

		l.removeStyleName("style");

		assertFalse(l.hasStyleName("style"));
		assertTrue(l.hasStyleName("style1"));
		assertTrue(l.hasStyleName("style2"));
		assertTrue(l.hasStyleName("style3"));
		assertTrue(l.hasStyleName("style4"));
	}

	@Test
	public void shouldDelegateStyleChangeToSuperClass() throws Exception {
		final FastLabel l = new FastLabel();
		l.addStyleName("style");
		l.addStyleName("style1");
		l.addStyleName("style2");
		l.addStyleName("style3");
		l.addStyleName("style4");

		assertEquals("gwt-Label style style1 style2 style3 style4", l.getStyleName());
	}

	@Test
	public void shouldDelegateTextChangeToSuperClass() throws Exception {
		final FastLabel l = new FastLabel();
		l.setText("test text");
		assertEquals("test text", l.getElement().getInnerText());
	}

	@Override
	public String getModuleName() {
		return "br.com.oncast.ontrack.Application";
	}
}
