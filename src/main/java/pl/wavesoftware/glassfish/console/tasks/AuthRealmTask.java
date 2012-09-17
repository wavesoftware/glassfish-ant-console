/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.wavesoftware.glassfish.console.tasks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.tools.ant.BuildException;
import org.glassfish.ant.tasks.AdminTask;
import pl.wavesoftware.glassfish.console.enums.ConsoleActions;
import pl.wavesoftware.glassfish.console.enums.RealmTypes;

/**
 *
 * @author ksuszyns
 */
public class AuthRealmTask extends AdminTask {

	private ConsoleActions action = ConsoleActions.AUTO;

	private RealmTypes type;

	private List<UseProperties> useProperties = new ArrayList<UseProperties>();

	private List<Property> properties = new ArrayList<Property>();

	private final String DEFAULT_PREFIX = "glassfish-console.security.realm";

	private Map<String, String> readedPeroperties;

	public void setAction(String action) {
		this.action = ConsoleActions.valueOf(action.toUpperCase());
	}

	public void setType(String type) {
		this.type = RealmTypes.valueOf(type.toUpperCase());
	}

	public AuthRealmTask() {
		super();
		UseProperties up = new UseProperties();
		up.setPrefix(DEFAULT_PREFIX);
		this.useProperties.add(up);
	}

	@Override
	public void execute() throws BuildException {
		String compiledCommand = prepareParams();
		super.execute(compiledCommand);
	}

	public void addUseProperties(UseProperties useProperties) {
		this.useProperties.add(useProperties);
	}

	public void addProperty(Property property) {
		properties.add(property);
	}

	private String getProperty(String name) {
		return getProperty(name, null);
	}

	private String getRequiredProperty(String name) {
		String prop = getProperty(name, null);
		if (prop == null) {
			throw new IllegalArgumentException("Property named \"" + name + "\" is required");
		}
		return prop;
	}

	private String getProperty(String name, String defaultValue) {
		Map<String, String> props = getProperties();
		if (!props.containsKey(name)) {
			return defaultValue;
		}
		return props.get(name);
	}

	private Map<String, String> getProperties() {
		if (readedPeroperties == null) {
			readedPeroperties = new HashMap<String, String>();
			readUses();
			readProperties();
		}
		return readedPeroperties;
	}

	private void readUses() {
		Map<Object, Object> all = getProject().getProperties();
		for (UseProperties useProperty : useProperties) {
			for (Map.Entry<Object, Object> entry : all.entrySet()) {
				String key = entry.getKey().toString();
				Object object = entry.getValue();
				if (key.startsWith(useProperty.prefix)) {
					String stripped = key.substring(useProperty.prefix.length());
					readedPeroperties.put(stripped, object.toString());
				}
			}
		}
	}

	private void readProperties() {
		for (Property property : properties) {
			readedPeroperties.put(property.name, property.value);
		}
	}

	private void addCreateParams() {
		String clazz = "";
		List<String> requiredProperties = new ArrayList<String>();
		List<String> optionalProperties = new ArrayList<String>();
		requiredProperties.add("jaas-context");
		optionalProperties.add("assign-groups");
		switch (type) {
			case FILE:
				clazz = "com.sun.enterprise.security.auth.realm.file.FileRealm";
				requiredProperties.add("file");
				break;
			case CERTIFICATE:
				clazz = "com.sun.enterprise.security.auth.realm.certificate.CertificateRealm";
				requiredProperties.add("LoginModule");
				break;
			case JDBC:
				clazz = "com.sun.enterprise.security.auth.realm.jdbc.JDBCRealm";
				requiredProperties.add("datasource-jndi");
				requiredProperties.add("user-table");
				requiredProperties.add("user-name-column");
				requiredProperties.add("password-column");
				requiredProperties.add("group-table");
				requiredProperties.add("group-name-column");

				optionalProperties.add("charset");
				optionalProperties.add("encoding");
				optionalProperties.add("digestrealm-password-enc-algorithm");
				optionalProperties.add("digest-algorithm");
				optionalProperties.add("db-password");
				optionalProperties.add("db-user");
				optionalProperties.add("assign-groups");
				optionalProperties.add("group-table-user-name-column");
				break;
			case LDAP:
				clazz = "com.sun.enterprise.security.auth.realm.ldap.LDAPRealm";
				requiredProperties.add("directory");
				requiredProperties.add("base-dn");

				optionalProperties.add("search-filter");
				optionalProperties.add("group-base-dn");
				optionalProperties.add("group-search-filter");
				optionalProperties.add("group-target");
				optionalProperties.add("search-bind-dn");
				optionalProperties.add("search-bind-password");
				break;
			case PAM:
				clazz = "com.sun.enterprise.security.auth.realm.ldap.PamRealm";
				break;
			case SOLARIS:
				clazz = "com.sun.enterprise.security.auth.realm.solaris.SolarisRealm";
				break;
			case CUSTOM:
				clazz = getRequiredProperty("class");
				for (Map.Entry<String, String> entry : getProperties().entrySet()) {
					String key = entry.getKey();
					if (!key.equals("class") && !key.equals("name")) {
						optionalProperties.add(key);
					}
				}
		}
		addCommandParameter("classname", clazz);
		HashMap<String, String> targetProperties = new HashMap<String, String>();
		for (String propertName : requiredProperties) {
			useRequiredProperty(targetProperties, propertName);
		}
		for (String propertyName : optionalProperties) {
			useOptionalProperty(targetProperties, propertyName);
		}
		String propertyString = joinProperties(targetProperties);
		addCommandParameter("property", propertyString);
		addCommandOperand(getRequiredProperty("name"));
	}

	private void useRequiredProperty(Map<String, String> set, String name) {
		String prop = getRequiredProperty(name);
		set.put(name, prop);
	}

	private void useOptionalProperty(Map<String, String> set, String name) {
		String prop = getProperty(name);
		if (prop != null) {
			set.put(name, prop);
		}
	}

	private void addDeleteParams() {
		String name = getRequiredProperty("name");
		addCommandOperand(name);
	}

	private void doAutomaticAction(String inheritedCommand) {
		// TODO: implement method
		throw new UnsupportedOperationException("Not yet implemented");
	}

	private void setPreserveCommand(String command, String inheritedCommand) {
		StringBuilder out = new StringBuilder();
		out.append(inheritedCommand.trim());
		if (out.length() != 0) {
			out.append(" ");
		}
		out.append(command);
		setCommand(out.toString());
	}

	private String joinProperties(HashMap<String, String> targetProperties) {
		StringBuilder sb = new StringBuilder();
		List<String> firstJoin = new ArrayList<String>();
		for (Map.Entry<String, String> entry : targetProperties.entrySet()) {
			String name = entry.getKey();
			String value = entry.getValue();
			firstJoin.add(name + "=" + value);
		}
		for (String pair : firstJoin) {
			sb.append(pair);
			sb.append(":");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	String prepareParams() {
		String inheritedCommand = getCommand();
		switch (action) {
			case AUTO:
				doAutomaticAction(inheritedCommand);
				break;
			case LIST:
				setPreserveCommand("list-auth-realms", inheritedCommand);
				break;
			case CREATE:
				setPreserveCommand("create-auth-realm", inheritedCommand);
				addCreateParams();
				break;
			case DELETE:
				setPreserveCommand("delete-auth-realm", inheritedCommand);
				addDeleteParams();
				break;
		}
		return getCommand();
	}

	public static class UseProperties {

		private String prefix;

		public void setPrefix(String prefix) {
			this.prefix = prefix;
			if (!prefix.endsWith(".")) {
				this.prefix += ".";
			}
		}
	}

	public static class Property {

		private String name;

		private String value;

		/**
		 * Set the value of name
		 *
		 * @param name new value of name
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * Set the value of text
		 *
		 * @param value new value of text
		 */
		public void setValue(String value) {
			this.value = value;
		}

		/**
		 * Set the value of text
		 *
		 * @param text new value of text
		 */
		public void addText(String text) {
			this.value = text;
		}
	}
}
