/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.wavesoftware.glassfish.console.enums;

import pl.wavesoftware.glassfish.console.enums.RealmTypes;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ksuszyns
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
