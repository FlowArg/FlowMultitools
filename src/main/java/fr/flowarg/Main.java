package fr.flowarg;

import fr.flowarg.flowio.FileUtils;
import fr.flowarg.flowzipper.ZipUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        long start = System.currentTimeMillis();
        final Path gradleDir = Paths.get("/home/flow/.gradle/wrapper/dists/gradle-8.6-all/3mbtmo166bl6vumsh5k2lkq5h/gradle-8.6/");
        final Path zipPath = Paths.get("/home/flow/.gradle/wrapper/dists/gradle-8.6-all/3mbtmo166bl6vumsh5k2lkq5h/gradle.zip");
        final Path gzPath = Paths.get("/home/flow/.gradle/wrapper/dists/gradle-8.6-all/3mbtmo166bl6vumsh5k2lkq5h/gradle.gz");
        final Path extractionZipDir = Paths.get("build/zip");
        final Path extractionTarDir = Paths.get("build/tar");

        ZipUtils.compressDirectory(gradleDir, zipPath);

        System.out.println("compression, " + (System.currentTimeMillis() - start) + " ms.");

        start = System.currentTimeMillis();
        ZipUtils.unzip(extractionZipDir, zipPath);
        System.out.println("unzip, " + (System.currentTimeMillis() - start) + " ms.");

        Files.deleteIfExists(zipPath);
        Files.deleteIfExists(gzPath);
        FileUtils.deleteDirectory(extractionZipDir);
        FileUtils.deleteDirectory(extractionTarDir);
    }
}
