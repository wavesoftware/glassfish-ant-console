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
package pl.wavesoftware.glassfish.console.enums;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author Krzysztof Suszyński <krzysztof.suszynski@gmail.com>
 */
public class RealmTypesTest {
	
	/**
	 * Test of values method, of class RealmTypes.
	 */
	@Test
	public void testValues() {
		RealmTypes[] expResult = {RealmTypes.FILE, RealmTypes.CERTIFICATE, RealmTypes.JDBC, RealmTypes.LDAP, RealmTypes.PAM, RealmTypes.SOLARIS, RealmTypes.CUSTOM};
		RealmTypes[] result = RealmTypes.values();
		assertArrayEquals(expResult, result);
	}

	/**
	 * Test of valueOf method, of class RealmTypes.
	 */
	@Test
	public void testValueOf() {
		String name = "JDBC";
		RealmTypes expResult = RealmTypes.JDBC;
		RealmTypes result = RealmTypes.valueOf(name);
		assertEquals(expResult, result);
	}
	
	@Test
	public void testToString() {
		String expResult = "JDBC";
		RealmTypes input = RealmTypes.JDBC;
		assertEquals(expResult, input.toString());
	}
	
}
