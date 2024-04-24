package fr.flowarg.flowzipper;

import fr.flowarg.flowio.FileUtils;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ZipUtilsTest
{
    private static final Path TEST_DIR = Paths.get("test");
    private static final Path TEST_CONTENT_DIR = TEST_DIR.resolve("content");
    private static final Path TEST_ZIP = TEST_DIR.resolve("test.zip");

    @BeforeAll
    public static void setUp() throws Exception
    {
        Files.createDirectories(TEST_DIR);
        Files.copy(new URL("https://flowarg.github.io/minecraft/launcher/patches.jar").openStream(), TEST_ZIP, StandardCopyOption.REPLACE_EXISTING);
    }

    @AfterAll
    public static void cleanup() throws Exception
    {
        FileUtils.deleteDirectory(TEST_DIR);
    }

    @Test
    public void testCompressFiles() throws IOException
    {
        ZipUtils.unzipJar(TEST_CONTENT_DIR, TEST_ZIP);
        final List<Path> contentFull = FileUtils.listRecursive(TEST_CONTENT_DIR);
        final Path compressed = TEST_DIR.resolve("compressed.zip");
        ZipUtils.compressFiles(FileUtils.list(TEST_CONTENT_DIR), compressed);
        FileUtils.deleteDirectory(TEST_CONTENT_DIR);
        ZipUtils.unzip(TEST_CONTENT_DIR, compressed);
        assertEquals(contentFull, FileUtils.listRecursive(TEST_CONTENT_DIR));
    }
}
