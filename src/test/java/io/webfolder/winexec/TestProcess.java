package io.webfolder.winexec;

import static java.lang.System.getProperty;
import static java.util.Locale.ENGLISH;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Locale;
import java.util.Scanner;

import org.junit.Test;

import com.google.devtools.build.lib.shell.JavaSubprocessFactory;
import com.google.devtools.build.lib.shell.Subprocess;
import com.google.devtools.build.lib.shell.SubprocessBuilder;
import com.google.devtools.build.lib.shell.SubprocessFactory;
import com.google.devtools.build.lib.windows.WindowsSubprocessFactory;
import com.google.devtools.build.lib.windows.jni.WindowsJniLoader;

public class TestProcess {

  static {
    WindowsJniLoader.loadJni();
  }

  private static final String  OS_NAME  = getProperty("os.name").toLowerCase(ENGLISH);

  private static final boolean WINDOWS  = OS_NAME.startsWith("windows");

  @Test
  public void testExecute() throws Exception {
      SubprocessFactory factory = WINDOWS ? WindowsSubprocessFactory.INSTANCE :
                                            JavaSubprocessFactory.INSTANCE;

    SubprocessBuilder builder = new SubprocessBuilder(factory);
    builder.setWorkingDirectory(new File("."));

    builder.setArgv("java.exe", "-version");

    StringBuilder buffer = new StringBuilder();

    Subprocess process = builder.start();
    try (Scanner scanner = new Scanner(process.getErrorStream())) {
      while (scanner.hasNext()) {
        String line = scanner.nextLine().trim();
        if (line.isEmpty()) {
          continue;
        }
        buffer.append(line);
      }
    }

    process.destroy();

    assertTrue(buffer.toString().toLowerCase(Locale.ENGLISH).contains("runtime"));
  }
}
