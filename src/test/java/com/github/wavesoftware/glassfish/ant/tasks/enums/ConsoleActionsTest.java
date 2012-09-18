/*
 * The MIT License
 *
 * Copyright 2012 Krzysztof Suszyński <krzysztof.suszynski@gmail.com>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.github.wavesoftware.glassfish.ant.tasks.enums;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Krzysztof Suszyński <krzysztof.suszynski@gmail.com>
 */
public class ConsoleActionsTest {
	
	@Test
	public void testValues() {
		ConsoleActions[] expResult = {ConsoleActions.CREATE, ConsoleActions.DELETE, ConsoleActions.LIST, ConsoleActions.AUTO};
		ConsoleActions[] result = ConsoleActions.values();
		assertArrayEquals(expResult, result);
	}

	@Test
	public void testValueOf() {
		String name = "LIST";
		ConsoleActions expResult = ConsoleActions.LIST;
		ConsoleActions result = ConsoleActions.valueOf(name);
		assertEquals(expResult, result);
	}

	@Test
	public void testToString() {
		ConsoleActions instance = ConsoleActions.AUTO;
		String expResult = "AUTO";
		String result = instance.toString();
		assertEquals(expResult, result);
	}
}
