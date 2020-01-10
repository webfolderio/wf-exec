package io.webfolder.winexec;

import static java.nio.file.Files.copy;
import static java.nio.file.Files.createDirectories;
import static java.nio.file.Files.exists;
import static java.nio.file.Files.readAllLines;
import static java.nio.file.Files.walkFileTree;
import static java.nio.file.Paths.get;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.apache.commons.io.FileUtils.copyURLToFile;
import static org.zeroturnaround.zip.ZipUtil.unpack;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.zip.NameMapper;

public class Extract {

    private static Path bazelJar;

    private static Path windowsJniDll;

    private static final String bazelDownloadUrl =
                                "https://github.com/bazelbuild/bazel/releases/download/2.0.0/bazel-2.0.0-windows-x86_64.exe";

    public static void main(String[] args) throws Exception {
        // download bazel windows to current directory if not exists
        File bazel = new File("bazel-2.0.0-windows-x86_64.exe");
        if ( ! bazel.exists() ) {
            copyURLToFile(new URL(bazelDownloadUrl), bazel);
        }

        // install bazel
        File bazelInstallDir = new File("bazelInstallDir").getAbsoluteFile();
        if ( ! bazelInstallDir.exists() ) {
        	bazelInstallDir.mkdir();
            ProcessExecutor executor = new ProcessExecutor();
            executor.command(bazel.getAbsolutePath(),
                             "--output_user_root=" + bazelInstallDir.getAbsolutePath()
                            ).execute();
        }

        // find the necessary jar file and dll
        walkFileTree(bazelInstallDir.toPath().toAbsolutePath(), new SimpleFileVisitor<Path>() {

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                String fileName = file.getFileName().toString();
                if (bazelJar == null && fileName.equals("A-server.jar")) {
                    bazelJar = file;
                }
                if (windowsJniDll == null && fileName.equals("windows_jni.dll")) {
                    windowsJniDll = file;
                }
                return super.visitFile(file, attrs);
            }
        });

        // we will bundle only required classes
        final List<String> classes = readAllLines(get("classes.txt"));

        File buildDir = new File("target/classes");

        // unpack the classes to build directory
        unpack(bazelJar.toFile(), buildDir, (NameMapper) name -> {
            if ( ! classes.contains(name) ) {
                return null;
            }
            return name;
        });

        // copy dll file to build directory
        Path to = get("target/classes/META-INF/windows_jni.dll");
        Path metaInf = to.getParent();
        if ( ! exists(metaInf) ) {
            createDirectories(metaInf);
        }

        copy(windowsJniDll, to, REPLACE_EXISTING);
    }
}
