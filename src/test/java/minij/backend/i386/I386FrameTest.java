/*
 * MiniJCompiler: Compiler for a subset of the Java(R) language
 *
 * (C) Copyright 2014-2015 Tibor Goldschwendt <goldschwendt@cip.ifi.lmu.de>,
 * Michael Seifert <mseifert@error-reports.org>
 *
 * This file is part of MiniJCompiler.
 *
 * MiniJCompiler is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MiniJCompiler is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MiniJCompiler.  If not, see <http://www.gnu.org/licenses/>.
 */
package minij.backend.i386;

import static org.junit.Assert.assertEquals;
import minij.backend.i386.I386Frame;
import minij.translate.layout.Label;
import minij.translate.layout.Frame.Location;

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
