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

import com.github.wavesoftware.glassfish.ant.tasks.enums.ConsoleActions;
import com.github.wavesoftware.glassfish.ant.tasks.enums.RealmTypes;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.ExecTask;
import org.apache.tools.ant.taskdefs.XmlProperty;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;

/**
 *
 * @author Krzysztof Suszyński <krzysztof.suszynski@gmail.com>
 */
public class AuthRealmTaskIT {

    private Path root;

    @Before
    public void before() {
        ExecTask task = getAsAdminExecutable();
        task.createArg().setLine("start-domain");
        task.execute();
    }

    @After
    public void after() {
        ExecTask task = getAsAdminExecutable();
        task.createArg().setLine("stop-domain");
        task.execute();
    }

    @Test
    public void testExecute() {
        Project project = spy(new Project());
        project.setBaseDir(getBasedir().toFile());
        AuthRealmTask task = new AuthRealmTask();
        task.setProject(project);
        task.setAction(ConsoleActions.AUTO.name());
        task.setType(RealmTypes.JDBC.name());
        task.setInstallDir(getGlassfishInstallDir().toString());
        File xmlFile = new File(this.getClass().getResource("glassfish-console.xml").getFile());
        loadAsAntProperties(task, xmlFile);
        task.getProject().setProperty("glassfish-console.security.realm.re-create", "");
        task.execute();
        verify(project, atLeastOnce()).log(any(Task.class), eq("done"), anyInt());
    }

    private void loadAsAntProperties(AuthRealmTask task, File propertiesFile) {
        XmlProperty xmlProperty = new XmlProperty();
        xmlProperty.setFile(propertiesFile);
        Project antProject = task.getProject();
        xmlProperty.setProject(antProject);
        xmlProperty.execute();
    }

    private Path getGlassfishInstallDir() {
        return getBasedir().resolve("target")
            .resolve("glassfish-4.0")
            .resolve("glassfish4");
    }

    private ExecTask getAsAdminExecutable() {
        ExecTask execTask = new ExecTask();
        Path base = getBasedir();
        Project project = new Project();
        project.setBaseDir(base.toFile());
        execTask.setProject(project);
        Path asadmin = getGlassfishInstallDir()
            .resolve("bin")
            .resolve("asadmin");
        execTask.setExecutable(asadmin.toString());
        return execTask;
    }

    private Path getMavenTestClasses() {
        return Paths.get("target", "test-classes");
    }

    private Path getBasedir() {
        if (root == null) {
            String separator = System.getProperty("file.separator");
            String pack = this.getClass().getPackage().getName().replace(".", separator);
            URL thisDir = this.getClass().getResource(".");
            String path = thisDir.getPath();
            String rootStr = path.replace(getMavenTestClasses() + separator, "")
                .replace(pack + separator, "");
            root = Paths.get(rootStr);
        }
        return root;
    }

}
