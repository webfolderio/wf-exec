# wf-exec

[![AppVeyor](https://img.shields.io/appveyor/ci/WebFolder/wf-exec.svg?label=Windows)](https://ci.appveyor.com/project/WebFolder/wf-exec) [![License](https://img.shields.io/badge/license-Apache-blue.svg)](https://github.com/webfolderio/wf-exec/blob/master/LICENSE)

Supported Java Versions
-----------------------

Oracle & OpenJDK Java 8, 11.

Both the JRE and the JDK are suitable for use with this library.

Supported Platforms
-------------------
* Windows 7, 8 and 10

How it is tested
----------------
wf-exec is regularly tested on [appveyor](https://ci.appveyor.com/project/WebFolder/wf-exec) (Windows)

Integration with Maven
----------------------

The project artifacts are available in [Maven Central Repository](https://search.maven.org/artifact/io.webfolder/wf-exec).

To use the official release of wf-exec, please use the following snippet in your `pom.xml` file.

Add the following to your POM's `<dependencies>` tag:

```xml
<dependency>
    <groupId>io.webfolder</groupId>
    <artifactId>wf-exec</artifactId>
    <version>1.0.1</version>
</dependency>
```

Example
-------

```java
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

    builder.setArgv("java.exe", "-version");

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
    process.close();

    System.out.println(buffer.toString());
  }
}
```

License
-------
Licensed under the [Apache License](https://github.com/webfolderio/wf-exec/blob/master/LICENSE).

Note: The included code from [bazel](https://github.com/bazelbuild/bazel) is licensed under [Apache](https://github.com/bazelbuild/bazel/blob/master/LICENSE).
