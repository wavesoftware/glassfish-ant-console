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
package pl.wavesoftware.glassfish.console;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.tools.ant.BuildException;

/**
 *
 * @author Krzysztof Suszyński <krzysztof.suszynski@gmail.com>
 */
public class CommandRunnerImpl extends FilterableCommandRunner {

	public CommandRunnerImpl(String command) {
		this.command = command;
	}

	public CommandRunnerImpl() {
		command = "";
	}

	@Override
	public int run() throws IOException, InterruptedException, BuildException {
		if (installDirectory != null) {
			File f = new File(installDirectory);
			if (!f.exists()) {
				throw new BuildException("Glassfish install directory : " + installDirectory + " not found."
						+ " Specify the correct directory as installDir attribute or asinstall.dir property");
			}
			File asadmin = getAsAdmin(f);
			return runCommand(asadmin.getAbsolutePath() + " " + command);
		} else {
			return runCommand(command);
		}
	}

	private int runCommand(String fullCommand) throws IOException, InterruptedException {
		errors.clear();
		output.clear();

		Process pr = Runtime.getRuntime().exec(fullCommand);

		BufferedReader error = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
		String errorLine;
		while ((errorLine = error.readLine()) != null) {
			errors.add(errorLine);
		}

		BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
		String inputLine;
		while ((inputLine = input.readLine()) != null) {
			output.add(inputLine);
		}

		int exitVal = pr.waitFor();
		return exitVal;

	}

	private File getAsAdmin(File installDir) {
		String osName = System.getProperty("os.name");
		File binDir = new File(installDir, "bin");
		if (osName.indexOf("Windows") == -1) {
			return new File(binDir, "asadmin");
		} else {
			return new File(binDir, "asadmin.bat");
		}
	}

}
