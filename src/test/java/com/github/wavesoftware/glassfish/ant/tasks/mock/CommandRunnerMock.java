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
package com.github.wavesoftware.glassfish.ant.tasks.mock;

import com.github.wavesoftware.glassfish.ant.tasks.AuthRealmTask;
import com.github.wavesoftware.glassfish.ant.tasks.common.FilterableCommandRunner;
import com.github.wavesoftware.glassfish.ant.tasks.enums.ConsoleActions;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author Krzysztof Suszyński <krzysztof.suszynski@gmail.com>
 */
public class CommandRunnerMock extends FilterableCommandRunner {

	private final String name;

	public CommandRunnerMock(String name) {
		this.name = name;
		realms.add("file");
		realms.add(name);
		realms.add("certificate");
	}
	
	List<String> realms = new ArrayList<String>();

	int runs = 0;

	@Override
	public int run() throws IOException, InterruptedException {
		runs++;
		
		Pattern listPattern = Pattern.compile(AuthRealmTask.getConsoleCommand(ConsoleActions.LIST));
		Pattern createPattern = Pattern.compile(AuthRealmTask.getConsoleCommand(ConsoleActions.CREATE));
		Pattern deletePattern = Pattern.compile(AuthRealmTask.getConsoleCommand(ConsoleActions.DELETE));
		if (listPattern.matcher(command).find()) {
			runs+=100;
			for (String realmName : realms) {
				output.add(realmName);
			}
			output.add("Command " + AuthRealmTask.getConsoleCommand(ConsoleActions.LIST) + " completed sucessfully");
			return 0;
		}
		if (createPattern.matcher(command).find()) {
			runs+=1000;
			realms.add(name);
			output.add("Command " + AuthRealmTask.getConsoleCommand(ConsoleActions.CREATE) + " completed sucessfully");
			return 0;
		}
		if (deletePattern.matcher(command).find()) {
			runs+=10000;
			realms.remove(name);
			output.add("Command " + AuthRealmTask.getConsoleCommand(ConsoleActions.DELETE) + " completed sucessfully");
			return 0;
		}

		return -1;
	}

	public int getRuns() {
		return runs;
	}
}
