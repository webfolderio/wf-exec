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

package com.google.devtools.build.lib.windows.jni;

import static java.lang.System.getProperty;
import static java.lang.System.load;
import static java.nio.file.Files.copy;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.createFile;
import static java.nio.file.Files.exists;
import static java.nio.file.Paths.get;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

/** Loads native code under Windows. */
public class WindowsJniLoader {

    // temporary directory location
  private static final Path tmpdir = get(getProperty("java.io.tmpdir")).toAbsolutePath();

  private static final String version = "1.0.2";

  public static synchronized void loadJni() {
    Path libFile;
    ClassLoader cl = WindowsJniLoader.class.getClassLoader();
    InputStream is = null;
    try {
      is = cl.getResourceAsStream("META-INF/windows_jni.dll");
      libFile = tmpdir.resolve("win-exec-" + version).resolve("windows_jni.dll");
      if ( ! exists(libFile) ) {
        createDirectories(libFile.getParent());
    	createFile(libFile);
        copy(is, libFile, REPLACE_EXISTING);
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      if ( is != null ) {
        try {
          is.close();
        } catch (IOException e) {
          // ignore
        }
      }
    }
    load(libFile.toString());
  }
}
