package minijava.backend.i386;

import static org.junit.Assert.assertEquals;

import minijava.translate.layout.Frame.Location;
import minijava.translate.layout.Label;
import org.junit.Before;
import org.junit.Test;

public class I386FrameTest {
	private static I386Frame frame;

	@Before
	public void setUp() throws Exception {
		frame = new I386Frame(new Label("TestFrame"), 2);
	}

	@Test
	public void testSize() {
		// Minimum frame size must include the space for the frame pointer
		assertEquals(4, frame.size());

		frame.addLocal(Location.ANYWHERE);
		assertEquals(4, frame.size());

		frame.addLocal(Location.IN_MEMORY);
		assertEquals(8, frame.size());

		frame.addLocal(Location.ANYWHERE);
		assertEquals(8, frame.size());
	}
}
