/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pl.wavesoftware.glassfish.console.tasks;

import pl.wavesoftware.glassfish.console.tasks.AuthRealmTask;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import org.apache.tools.ant.Project;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import pl.wavesoftware.glassfish.console.tasks.AuthRealmTask.Property;
import pl.wavesoftware.glassfish.console.tasks.AuthRealmTask.UseProperties;

/**
 *
 * @author ksuszyns
 */
public class AuthRealmTaskTest {

	private AuthRealmTask instance;

	@Before
	public void setUp() {
		instance = new AuthRealmTask();
	}

	private Project getInitiatedProject(String fileName) throws FileNotFoundException, IOException {
		Properties props = new Properties();
		props.load(getClass().getResourceAsStream(fileName));
		Project project = new Project();
		for (Map.Entry<Object, Object> entry : props.entrySet()) {
			String key = entry.getKey().toString();
			String value = entry.getValue().toString();
			project.setProperty(key, value);
		}
		return project;
	}

	private void logIn(String user, String password) {
		instance.setUser(user);
		instance.setPasswordFile(password);
	}

	@Test
	public void testJdbcPrepareParams() throws FileNotFoundException, IOException {
		logIn("testuser", "secretfile.sec");
		instance.setProject(getInitiatedProject("jdbc-good-sample.properties"));
		instance.setType("jdbc");
		instance.setAction("create");
		String expResult = "--user=testuser --passwordfile=secretfile.sec create-auth-realm --classname=com.sun.enterprise.security.auth.realm.jdbc.JDBCRealm --property=group-table-user-name-column=login:password-column=password:user-table=users:jaas-context=jdbcRealm:group-name-column=group_name:datasource-jndi=jdbc/GoodSample:digest-algorithm=MD5:group-table=user_group:user-name-column=login JDBC_good";
		String result = instance.prepareParams();
		assertEquals(expResult, result);
	}

	@Test
	public void testJdbcBadPrepareParams() throws FileNotFoundException, IOException {
		logIn("testuser", "secretfile.sec");
		instance.setProject(getInitiatedProject("jdbc-bad-sample.properties"));
		instance.setType("jdbc");
		instance.setAction("create");
		try {
			instance.prepareParams();
		} catch (Exception e) {
			assertEquals("Property named \"datasource-jndi\" is required", e.getMessage());
			return;
		}
		fail("Should throw an exception");
	}
	
	@Test
	public void testByHandPrepareParams() throws FileNotFoundException, IOException {
		instance.setProject(getInitiatedProject("jdbc-bad-sample.properties"));
		instance.setType("file");
		instance.setAction("create");
		Property prop;
		
		prop = new Property();
		prop.setName("file");
		prop.addText("/secret-file.sec");
		instance.addProperty(prop);
		
		String expResult = "create-auth-realm --classname=com.sun.enterprise.security.auth.realm.file.FileRealm --property=jaas-context=jdbcRealm:file=/secret-file.sec JDBC_good";
		String result = instance.prepareParams();
		assertEquals(expResult, result);
	}
	
	@Test
	public void testDelete() throws FileNotFoundException, IOException {
		instance.setProject(getInitiatedProject("jdbc-bad-sample.properties"));
		instance.setType("file");
		instance.setAction("delete");
		Property prop;
		
		prop = new Property();
		prop.setName("file");
		prop.addText("/secret-file.sec");
		instance.addProperty(prop);
		
		String expResult = "delete-auth-realm JDBC_good";
		String result = instance.prepareParams();
		assertEquals(expResult, result);
	}
}
