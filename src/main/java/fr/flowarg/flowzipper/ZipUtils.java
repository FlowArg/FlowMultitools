package fr.flowarg.flowzipper;

import fr.flowarg.flowio.FileUtils;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public final class ZipUtils
{
    /**
     * Extract a tar.gz archive
     * @param tarGzFile the tar.gz archive to extract.
     * @param destinationDir directory to extract the archive.
     * @throws IOException if an I/O error occurred.
     */
    public static void decompressTarArchive(final Path tarGzFile, final Path destinationDir) throws IOException
    {
        try(final InputStream is = Files.newInputStream(tarGzFile)
            ; final GzipCompressorInputStream gzipIn = new GzipCompressorInputStream(is)
            ; final TarArchiveInputStream tarIn = new TarArchiveInputStream(gzipIn)
        )
        {
            TarArchiveEntry entry;

            while ((entry = tarIn.getNextEntry()) != null)
            {
                final Path path = destinationDir.resolve(entry.getName());
                if(entry.isDirectory()) Files.createDirectories(path);
                else
                {
                    int count;
                    final byte[] data = new byte[4096];
                    try(final OutputStream fos = Files.newOutputStream(path))
                    {
                        while((count = tarIn.read(data, 0, 4096)) != -1)
                            fos.write(data, 0, count);
                    }
                }
            }
        }
    }

    public static void gzipFile(Path baseFile, Path newFile) throws IOException
    {
        final byte[] buffer = new byte[4096];

        if(baseFile != null && newFile != null)
        {
            try(final GZIPOutputStream gzipOutputStream = new GZIPOutputStream(Files.newOutputStream(newFile));
                final InputStream fileInputStream = Files.newInputStream(baseFile))
            {
                int bytesRead;

                while ((bytesRead = fileInputStream.read(buffer)) > 0)
                    gzipOutputStream.write(buffer, 0, bytesRead);

                gzipOutputStream.finish();
            }
        }
    }

    /**
     * Compress directory content into a .zip file.
     * @param directory directory to compress.
     * @param destZipFile destination .zip file
     * @throws IOException if an I/O error occurred.
     */
    public static void compressDirectory(Path directory, Path destZipFile) throws IOException
    {
        compressFiles(FileUtils.list(directory), destZipFile);
    }

    /**
     * Compress files into a .zip file.
     * @param listFiles files to compress.
     * @param destZipFile destination .zip file
     * @throws IOException if an I/O error occurred.
     */
    public static void compressFiles(List<Path> listFiles, Path destZipFile) throws IOException
    {
        try(final ZipOutputStream zos = new ZipOutputStream(Files.newOutputStream(destZipFile)))
        {
            for (Path file : listFiles)
            {
                if (Files.isDirectory(file)) addFolderToZip(file, file.getFileName().toString(), zos);
                else addFileToZip(file, zos);
            }

            zos.flush();
        }
    }

    private static void addFolderToZip(Path folder, String parentFolder, ZipOutputStream zos) throws IOException
    {
        for (final Path path : FileUtils.list(folder))
        {
            if (Files.isDirectory(path))
            {
                addFolderToZip(path, parentFolder + '/' + path.getFileName().toString(), zos);
                continue;
            }
            zos.putNextEntry(new ZipEntry(parentFolder + "/" + path.getFileName().toString()));

            try(final InputStream bis = Files.newInputStream(path))
            {
                int read;
                final byte[] buffer = new byte[4096];

                while ((read = bis.read(buffer)) != -1)
                    zos.write(buffer, 0, read);
            }
        }
    }

    private static void addFileToZip(Path file, ZipOutputStream zos) throws IOException
    {
        zos.putNextEntry(new ZipEntry(file.getFileName().toString()));

        try(final InputStream bis = Files.newInputStream(file))
        {
            int read;
            final byte[] buffer = new byte[4096];

            while ((read = bis.read(buffer)) != -1)
                zos.write(buffer, 0, read);
        }
    }

    /**
     * Unzip a .zip file to a folder
     * @param destinationDir where the zip will be extracted.
     * @param zipFile the zip to extract.
     * @throws IOException if an I/O error occurred.
     */
    public static void unzip(Path destinationDir, Path zipFile) throws IOException
    {
        try(final ZipFile toUnZip = new ZipFile(zipFile.toFile()))
        {
            final Enumeration<? extends ZipEntry> enu = toUnZip.entries();
            while (enu.hasMoreElements())
            {
                final ZipEntry entry = enu.nextElement();
                unzip0(destinationDir.resolve(entry.getName()), toUnZip, entry);
            }
        }
    }

    private static void unzip0(Path fl, ZipFile zipFile, ZipEntry entry) throws IOException
    {
        if(entry.isDirectory())
            return;

        if (Files.notExists(fl))
            Files.createDirectories(fl.getParent());

        Files.copy(zipFile.getInputStream(entry), fl, StandardCopyOption.REPLACE_EXISTING);
    }

    public static void unzipJar(Path destinationDir, Path jarPath, String... args) throws IOException
    {
        try(final JarFile jar = new JarFile(jarPath.toFile()))
        {
            final Enumeration<JarEntry> enu = jar.entries();
            while(enu.hasMoreElements())
            {
                final JarEntry je = enu.nextElement();
                final Path fl = destinationDir.resolve(je.getName());

                if(args.length >= 1 && args[0] != null && args[0].equals("ignoreMetaInf"))
                {
                    if (fl.toString().contains("META-INF"))
                        continue;
                }

                unzip0(fl, jar, je);
            }
        }
    }
}
