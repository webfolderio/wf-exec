package io.webfolder.winexec;

import static com.google.devtools.build.lib.windows.WindowsSubprocessFactory.INSTANCE;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Locale;
import java.util.Scanner;

import org.junit.Test;

import com.google.devtools.build.lib.shell.Subprocess;
import com.google.devtools.build.lib.shell.SubprocessBuilder;
import com.google.devtools.build.lib.windows.jni.WindowsJniLoader;

public class TestProcess {

  static {
    WindowsJniLoader.loadJni();
  }

  @Test
  public void testExecute() throws Exception {
    SubprocessBuilder builder = new SubprocessBuilder(INSTANCE);
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
