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
package com.github.wavesoftware.glassfish.ant.tasks.common;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.tools.ant.BuildException;

/**
 *
 * @author Krzysztof Suszyński <krzysztof.suszynski@gmail.com>
 */
public abstract class FilterableCommandRunner implements CommandRunner {

	protected List<String> errors = new ArrayList<String>();

	protected List<String> output = new ArrayList<String>();
	
	protected String command;
	
	protected String installDirectory = null;
	
	@Override
	public void useAsAdmin(String installDir) {
		if (installDir == null) {
			throw new BuildException("Install Directory of application server not known. " + 
					"Specify either the installDir attribute or the asinstall.dir property");
		}
		this.installDirectory = installDir;
	}

	public FilterableCommandRunner() {
	}

	@Override
	public List<String> getErrorList() {
		return errors;
	}

	protected List<String> getFiltered(List<String> list, Pattern[] include, Pattern[] exclude) {
		List<String> filtered = new ArrayList<String>();
		if (include == null) {
			include = new Pattern[] {Pattern.compile(".*")};
		}
		if (exclude == null) {
			exclude = new Pattern[0];
		}
		for (String line : list) {
			boolean excluded = false;
			boolean included = false;
			for (int i = 0; i < exclude.length; i++) {
				Pattern pattern = exclude[i];
				if (pattern.matcher(line).find()) {
					excluded = true;
					break;
				}
			}
			if (excluded) {
				continue;
			}
			for (int i = 0; i < include.length; i++) {
				Pattern pattern = include[i];
				if (pattern.matcher(line).find()) {
					included = true;
					break;
				}
			}
			if (included) {
				filtered.add(line);
				continue;
			}
		}
		return filtered;
	}

	@Override
	public List<String> getFilteredErrorsList(List<Pattern> include, List<Pattern> exclude) {
		Pattern[] ts = null;
		return getFilteredErrorsList(include.toArray(ts), exclude.toArray(ts));
	}

	@Override
	public List<String> getFilteredErrorsList(Pattern[] include, Pattern[] exclude) {
		return getFiltered(errors, include, exclude);
	}

	@Override
	public List<String> getFilteredOutputList(List<Pattern> include, List<Pattern> exclude) {
		Pattern[] ts = null;
		return getFilteredOutputList(include.toArray(ts), exclude.toArray(ts));
	}

	@Override
	public List<String> getFilteredOutputList(Pattern[] include, Pattern[] exclude) {
		return getFiltered(output, include, exclude);
	}
	
	@Override
	public String getFilteredErrors(List<Pattern> include, List<Pattern> exclude) {
		Pattern[] ts = null;
		return getFilteredErrors(include.toArray(ts), exclude.toArray(ts));
	}

	@Override
	public String getFilteredErrors(Pattern[] include, Pattern[] exclude) {
		return concatByNewLine(getFiltered(errors, include, exclude));
	}

	@Override
	public String getFilteredOutput(List<Pattern> include, List<Pattern> exclude) {
		Pattern[] ts = null;
		return getFilteredOutput(include.toArray(ts), exclude.toArray(ts));
	}

	@Override
	public String getFilteredOutput(Pattern[] include, Pattern[] exclude) {
		return concatByNewLine(getFiltered(output, include, exclude));
	}
	
	private String concatByNewLine(List<String> list) {
		StringBuilder sb = new StringBuilder();
		for (String line : list) {
			sb.append(line);
			sb.append("\n");
		}
		if (sb.length() >= 1) {
			sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString().trim();
	}

	@Override
	public String getErrors() {
		return concatByNewLine(errors);
	}

	@Override
	public String getOutput() {
		return concatByNewLine(output);
	}

	@Override
	public List<String> getOutputList() {
		return output;
	}
	
	@Override
	public void setCommand(String command) {
		this.command = command;
	}
}
