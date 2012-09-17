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
package pl.wavesoftware.glassfish.console.tasks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.glassfish.ant.tasks.AdminTask;
import pl.wavesoftware.glassfish.console.CommandRunner;
import pl.wavesoftware.glassfish.console.CommandRunnerImpl;
import pl.wavesoftware.glassfish.console.enums.ConsoleActions;
import pl.wavesoftware.glassfish.console.enums.RealmTypes;

/**
 *
 * @author Krzysztof Suszyński <krzysztof.suszynski@gmail.com>
 */
public class AuthRealmTask extends AdminTask {

	private ConsoleActions action = ConsoleActions.AUTO;

	private RealmTypes type;

	private List<UseProperties> useProperties = new ArrayList<UseProperties>();

	private List<Property> properties = new ArrayList<Property>();

	private final String DEFAULT_PREFIX = "glassfish-console.security.realm";

	private Map<String, String> readedPeroperties;

	private CommandRunner runner;

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
	
	public static String getConsoleCommand(ConsoleActions action) {
		switch (action) {
			case CREATE:
				return "create-auth-realm";
			case DELETE:
				return "delete-auth-realm";
			case LIST:
				return "list-auth-realms";
			default:
				return null;
		}
	}
	
	@Override
	public void execute() throws BuildException {
		String compiledCommand = prepareParams();
		if (compiledCommand == null) {
			return;
		}
		CommandRunner runnerInst = getCommandRunner();
		runnerInst.useAsAdmin(getInstallDir());
		runnerInst.setCommand(compiledCommand);
		try {
			int exitVal = runnerInst.run();
			if (exitVal != 0) {
				log("Command exited with error code " + exitVal, Project.MSG_WARN);
			}
			if (!runnerInst.getOutput().equals("")) {
				String out = runnerInst.getFilteredOutput(null, new Pattern[]{Pattern.compile("executed successfully")});
				log(out);
			}
			if (!runnerInst.getErrors().equals("")) {
				log(runnerInst.getErrors(), Project.MSG_ERR);
			}
		} catch (IOException ex) {
			throw new BuildException(ex);
		} catch (InterruptedException ex) {
			throw new BuildException(ex);
		}
	}

	String prepareParams() {
		String inheritedCommand = getCommand();
		switch (action) {
			case AUTO:
				doAutomaticAction(inheritedCommand);
				return null;
			case LIST:
				setPreserveCommand(getConsoleCommand(action), inheritedCommand);
				break;
			case CREATE:
				setPreserveCommand(getConsoleCommand(action), inheritedCommand);
				addCreateParams();
				break;
			case DELETE:
				setPreserveCommand(getConsoleCommand(action), inheritedCommand);
				addDeleteParams();
				break;
		}
		return getCommand();
	}

	private void doAutomaticAction(String inheritedCommand) {
		AuthRealmTask createTask = new AuthRealmTask();
		createTask.setInstallDir(getInstallDir());
		createTask.setProject(getProject());
		createTask.setCommandRunner(runner);
		createTask.setCommand(inheritedCommand);
		createTask.setType(type.toString());
		createTask.setAction(ConsoleActions.CREATE.toString());
		AuthRealmTask deleteTask = new AuthRealmTask();
		deleteTask.setInstallDir(getInstallDir());
		deleteTask.setProject(getProject());
		deleteTask.setCommandRunner(runner);
		deleteTask.setCommand(inheritedCommand);
		deleteTask.setType(type.toString());
		deleteTask.setAction(ConsoleActions.DELETE.toString());
		try {
			if (isRealmCreated()) {
				if (shouldRecreateRealm()) {
					deleteTask.execute();
					createTask.execute();
				}
			} else {
				createTask.execute();
			}
		} catch (IOException ex) {
			log(ex.getMessage());
		} catch (InterruptedException ex) {
			log(ex.getMessage());
		}
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
					if (object == null) {
						readedPeroperties.put(stripped, null);
					} else {
						readedPeroperties.put(stripped, object.toString());
					}
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

	private boolean isRealmCreated() throws IOException, InterruptedException {
		runner.setCommand(("list-auth-realms " + getCommand()).trim());
		runner.useAsAdmin(getInstallDir());
		int result = runner.run();
		if (result != 0) {
			throw new IOException(runner.getErrors());
		}
		Pattern include = Pattern.compile(Pattern.quote(getRequiredProperty("name")));
		Pattern[] includes = {include};
		Pattern[] excludes = {Pattern.compile("list-auth-realms")};
		List<String> filtered = runner.getFilteredOutputList(includes, excludes);
		return filtered.size() == 1;
	}

	private boolean shouldRecreateRealm() {
		return isPropertySet("re-create");
	}

	private boolean isPropertySet(String name) {
		Map<String, String> props = getProperties();
		return props.containsKey(name);
	}

	private CommandRunner getCommandRunner() {
		if (this.runner == null) {
			this.runner = new CommandRunnerImpl("");
		}
		return this.runner;
	}
	
	void setCommandRunner(CommandRunner runner) {
		this.runner = runner;
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
