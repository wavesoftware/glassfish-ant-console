/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.wavesoftware.glassfish.console.enums;

import pl.wavesoftware.glassfish.console.enums.ConsoleActions;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author ksuszyns
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
