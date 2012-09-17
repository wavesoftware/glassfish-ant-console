/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.wavesoftware.glassfish.console.enums;

/**
 *
 * @author ksuszyns
 */
public enum ConsoleActions {
	CREATE("CREATE"),
	DELETE("DELETE"),
	LIST("LIST"), 
	AUTO("AUTO");

	private String type;

	private ConsoleActions(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return type;
	}
}
