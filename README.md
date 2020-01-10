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
    <version>1.0.0</version>
</dependency>
```

License
-------
Licensed under the [Apache License](https://github.com/webfolderio/wf-exec/blob/master/LICENSE).

Note: The included code from [bazel](https://github.com/bazelbuild/bazel) is licensed under [Apache](https://github.com/bazelbuild/bazel/blob/master/LICENSE).
