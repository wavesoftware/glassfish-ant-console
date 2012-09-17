/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.wavesoftware.glassfish.console.enums;

/**
 *
 * @author ksuszyns
 */
public enum RealmTypes {

	FILE("FILE"),
	CERTIFICATE("CERTIFICATE"),
	JDBC("JDBC"), 
	LDAP("LDAP"), 
	PAM("PAM"), 
	SOLARIS("SOLARIS"), 
	CUSTOM("CUSTOM");

	private String type;

	private RealmTypes(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return type;
	}
}
