package io.webfolder.winexec;

import static java.lang.System.getProperty;
import static java.util.Locale.ENGLISH;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

import com.google.devtools.build.lib.shell.JavaSubprocessFactory;
import com.google.devtools.build.lib.shell.Subprocess;
import com.google.devtools.build.lib.shell.SubprocessBuilder;
import com.google.devtools.build.lib.shell.SubprocessFactory;
import com.google.devtools.build.lib.windows.WindowsJniLoader;
import com.google.devtools.build.lib.windows.WindowsSubprocessFactory;

public class Example {

  private static final boolean WINDOWS  = getProperty("os.name")
                                              .toLowerCase(ENGLISH)
                                              .startsWith("windows");

  public static void main(String[] args) throws IOException {
    // load jni library
    WindowsJniLoader.loadJni();
    
    SubprocessFactory factory = WINDOWS ? WindowsSubprocessFactory.INSTANCE :
                                          JavaSubprocessFactory.INSTANCE;

    SubprocessBuilder builder = new SubprocessBuilder(factory);

    builder.setWorkingDirectory(new File("."));

    builder.setArgv(Arrays.asList("java.exe", "-version"));

    StringBuilder buffer = new StringBuilder();

    Subprocess process = builder.start();
    try (Scanner scanner = new Scanner(process.getErrorStream())) {
      while (scanner.hasNext()) {
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) {
          continue;
        }
        buffer.append(line).append("\r\n");
      }
    }

    // terminate the process
    process.destroy();

    while (process.finished()) {
      // wait until process finished
    }

    // close the native resources
    process.destroyAndWait();
    process.close();

    System.out.println(buffer.toString());
  }
}
