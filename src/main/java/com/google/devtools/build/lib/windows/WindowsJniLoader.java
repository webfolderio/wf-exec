// Copyright 2016 The Bazel Authors. All rights reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.devtools.build.lib.windows;

import static java.lang.System.getProperty;
import static java.lang.System.load;
import static java.nio.file.Files.copy;
import static java.nio.file.Files.createDirectory;
import static java.nio.file.Files.createFile;
import static java.nio.file.Files.exists;
import static java.nio.file.Paths.get;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.util.Locale.ENGLISH;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/** Loads native code under Windows. */
public class WindowsJniLoader {

    // temporary directory location
  private static final Path tmpdir = get(getProperty("java.io.tmpdir")).toAbsolutePath();

  private static final String version = "1.1.0";

  private static final String  OS_NAME = getProperty("os.name").toLowerCase(ENGLISH);

  private static final boolean WINDOWS = OS_NAME.startsWith("windows");

  private static boolean loaded;

  public static synchronized boolean loadJni() {
    if (loaded) {
      return true;
    }
    if ( ! WINDOWS ) {
      return false;
    }
    Path libFile = tmpdir.resolve("wf-exec-" + version).resolve("wf-exec.dll");
    if ( ! exists(libFile) ) {
    	ClassLoader cl = WindowsJniLoader.class.getClassLoader();
    	try (InputStream is = cl.getResourceAsStream("META-INF/wf-exec.dll")) {
    		if ( ! exists(libFile.getParent()) ) {
    			createDirectory(libFile.getParent());
    		}
    		if ( ! exists(libFile) ) {
    			createFile(libFile);
    		}
    		copy(is, libFile, REPLACE_EXISTING);
    	} catch (IOException e) {
    		throw new RuntimeException(e);
    	}
    }
    load(libFile.toString());
    return loaded = true;
  }
}
