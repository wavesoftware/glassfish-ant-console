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
package com.github.wavesoftware.glassfish.ant.tasks;

import com.github.wavesoftware.glassfish.ant.tasks.AuthRealmTask.Property;
import com.github.wavesoftware.glassfish.ant.tasks.mock.CommandRunnerMock;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;
import org.apache.tools.ant.Project;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Krzysztof Suszyński <krzysztof.suszynski@gmail.com>
 */
public class AuthRealmTaskTest {

    private AuthRealmTask instance;

    @Before
    public void setUp() {
        instance = new AuthRealmTask();
        // this is only sample
        instance.setInstallDir("/opt/glassfish-4.0/glassfish");
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

    @Test
    public void testAutoExecute() throws FileNotFoundException, IOException {
        CommandRunnerMock runner = new CommandRunnerMock("JDBC_good");
        instance.setCommandRunner(runner);
        instance.setProject(getInitiatedProject("jdbc-good-sample.properties"));
        instance.setType("jdbc");
        instance.setAction("auto");

        instance.execute();
        assertEquals(11103, runner.getRuns());
    }

    @Test
    public void testListExecute() throws FileNotFoundException, IOException {
        CommandRunnerMock runner = new CommandRunnerMock("JDBC_good");
        instance.setCommandRunner(runner);
        instance.setProject(getInitiatedProject("jdbc-good-sample.properties"));
        instance.setType("jdbc");
        instance.setAction("list");

        instance.execute();

        String out = runner.getFilteredOutput(null, new Pattern[]{Pattern.compile("completed")});
        assertEquals("file\nJDBC_good\ncertificate", out);
    }
}
