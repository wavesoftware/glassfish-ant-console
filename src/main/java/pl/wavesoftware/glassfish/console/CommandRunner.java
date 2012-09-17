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

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

/**
 *
 * @author Krzysztof Suszyński <krzysztof.suszynski@gmail.com>
 */
public interface CommandRunner {

	List<String> getFilteredErrorsList(List<Pattern> include, List<Pattern> exclude);

	List<String> getFilteredErrorsList(Pattern[] include, Pattern[] exclude);

	List<String> getFilteredOutputList(List<Pattern> include, List<Pattern> exclude);

	List<String> getFilteredOutputList(Pattern[] include, Pattern[] exclude);

	List<String> getErrorList();

	List<String> getOutputList();

	String getFilteredErrors(List<Pattern> include, List<Pattern> exclude);

	String getFilteredErrors(Pattern[] include, Pattern[] exclude);

	String getErrors();

	String getFilteredOutput(List<Pattern> include, List<Pattern> exclude);

	String getFilteredOutput(Pattern[] include, Pattern[] exclude);

	String getOutput();

	int run() throws IOException, InterruptedException;

	void setCommand(String command);

	void useAsAdmin(String installDir);
}
