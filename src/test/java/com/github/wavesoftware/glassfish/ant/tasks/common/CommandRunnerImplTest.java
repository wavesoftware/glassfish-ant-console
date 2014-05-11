/*
 * The MIT License
 *
 * Copyright 2014 Krzysztof Suszyński <krzysztof.suszynski@wavesoftware.pl>.
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
package com.github.wavesoftware.glassfish.ant.tasks.common;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

/**
 *
 * @author Krzysztof Suszyński <krzysztof.suszynski@wavesoftware.pl>
 */
public class CommandRunnerImplTest {

    private Path root;

    @Test
    public void testRun() throws Exception {
        Path basedir = getBasedir();
        Path glassfishDir = basedir
            .resolve("target")
            .resolve("glassfish-4.0")
            .resolve("glassfish4");
        CommandRunnerImpl instance = new CommandRunnerImpl("help");
        instance.useAsAdmin(glassfishDir.toString());
        int expResult = 0;
        int result = instance.run();
        assertThat(result).isEqualTo(expResult);
        assertThat(instance.getOutput()).contains(
            "create-jdbc-connection-pool", "start-domain", "list-commands"
        );
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
