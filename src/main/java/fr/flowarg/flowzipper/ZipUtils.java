package fr.flowarg.flowzipper;

import fr.flowarg.flowio.FileUtils;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicReference;
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
     * @deprecated use {@link #decompressTarArchive(Path, Path)} instead.
     */
    @Deprecated
    public static void decompressTarArchive(final File tarGzFile, final File destinationDir) throws IOException
    {
        try(final FileInputStream fis = new FileInputStream(tarGzFile)
            ; final BufferedInputStream bis = new BufferedInputStream(fis)
            ; final GzipCompressorInputStream gzipIn = new GzipCompressorInputStream(bis)
            ; final TarArchiveInputStream tarIn = new TarArchiveInputStream(gzipIn);
            )
        {
            TarArchiveEntry entry;

            while ((entry = (TarArchiveEntry)tarIn.getNextEntry()) != null)
            {
                if(entry.isDirectory())
                {
                    final File f = new File(destinationDir, entry.getName());
                    f.mkdirs();
                }
                else
                {
                    int count;
                    final byte[] data = new byte[4096];
                    try(final FileOutputStream fos = new FileOutputStream(new File(destinationDir, entry.getName()), false)
                        ; final BufferedOutputStream dest = new BufferedOutputStream(fos, 4096))
                    {
                        while((count = tarIn.read(data, 0, 4096)) != -1)
                            dest.write(data, 0, count);
                    }
                }
            }
        }
    }

    /**
     * Extract a tar.gz archive
     * @param tarGzFile the tar.gz archive to extract.
     * @param destinationDir directory to extract the archive.
     * @throws IOException if an I/O error occurred.
     */
    public static void decompressTarArchive(final Path tarGzFile, final Path destinationDir) throws IOException
    {
        try(final InputStream is = Files.newInputStream(tarGzFile)
            ; final BufferedInputStream bis = new BufferedInputStream(is)
            ; final GzipCompressorInputStream gzipIn = new GzipCompressorInputStream(bis)
            ; final TarArchiveInputStream tarIn = new TarArchiveInputStream(gzipIn)
        )
        {
            TarArchiveEntry entry;

            while ((entry = (TarArchiveEntry)tarIn.getNextEntry()) != null)
            {
                if(entry.isDirectory()) Files.createDirectories(Paths.get(destinationDir.toString(), entry.getName()));
                else
                {
                    int count;
                    final byte[] data = new byte[4096];
                    try(final OutputStream fos = Files.newOutputStream(Paths.get(destinationDir.toString(), entry.getName()))
                        ; final BufferedOutputStream dest = new BufferedOutputStream(fos, 4096))
                    {
                        while((count = tarIn.read(data, 0, 4096)) != -1)
                            dest.write(data, 0, count);
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
                final BufferedInputStream fileInputStream = new BufferedInputStream(Files.newInputStream(baseFile)))
            {
                int bytesRead;

                while ((bytesRead = fileInputStream.read(buffer)) > 0)
                    gzipOutputStream.write(buffer, 0, bytesRead);

                gzipOutputStream.finish();
            }
        }
    }

    /**
     * Unzip a jar containing lzma files.
     * @param destinationDir the destination folder.
     * @param jarFile the jar to extract.
     * @throws IOException if an I/O error occurred.
     * @deprecated use {@link #unzipJarWithLZMACompat(Path, Path)} instead.
     */
    @Deprecated
    public static void unzipJarWithLZMACompat(final File destinationDir, final File jarFile) throws IOException
    {
        try(final JarFile jar = new JarFile(jarFile))
        {
            for (Enumeration<JarEntry> enums = jar.entries(); enums.hasMoreElements(); )
            {
                final JarEntry entry = enums.nextElement();

                final String fileName = destinationDir + File.separator + entry.getName();
                final File file = new File(fileName);

                if (fileName.endsWith("/")) file.mkdirs();
            }

            for (Enumeration<JarEntry> enums = jar.entries(); enums.hasMoreElements(); )
            {
                final JarEntry entry = enums.nextElement();

                final String fileName = destinationDir + File.separator + entry.getName();
                final File file = new File(fileName);

                if (!fileName.endsWith("/"))
                {
                    if (fileName.endsWith(".lzma"))
                    {
                        new File(destinationDir, "data").mkdir();
                        try(final InputStream stream = jar.getInputStream(entry))
                        {
                            Files.copy(stream, new File(destinationDir, entry.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                    else
                    {
                        try(final InputStream is = jar.getInputStream(entry);final FileOutputStream fos = new FileOutputStream(file))
                        {
                            while (is.available() > 0)
                                fos.write(is.read());
                        }
                    }
                }
            }
        }
    }

    /**
     * Unzip a jar containing lzma files.
     * @param destinationDir the destination folder.
     * @param jarFile the jar to extract.
     * @throws IOException if an I/O error occurred.
     */
    public static void unzipJarWithLZMACompat(final Path destinationDir, final Path jarFile) throws IOException
    {
        try(final JarFile jar = new JarFile(jarFile.toFile()))
        {
            for (Enumeration<JarEntry> enums = jar.entries(); enums.hasMoreElements(); )
            {
                final JarEntry entry = enums.nextElement();
                final Path file = destinationDir.resolve(entry.getName());

                if (file.getFileName().toString().endsWith("/")) Files.createDirectories(file);
            }

            for (Enumeration<JarEntry> enums = jar.entries(); enums.hasMoreElements(); )
            {
                final JarEntry entry = enums.nextElement();

                final Path path = destinationDir.resolve(entry.getName());
                final String fileName = path.getFileName().toString();

                if (!fileName.endsWith("/"))
                {
                    if (fileName.endsWith(".lzma"))
                    {
                        Files.createDirectory(Paths.get(destinationDir.toString(), "data"));
                        try(final InputStream stream = jar.getInputStream(entry))
                        {
                            Files.copy(stream, Paths.get(destinationDir.toString(), entry.getName()), StandardCopyOption.REPLACE_EXISTING);
                        }
                    }
                    else
                    {
                        try(final InputStream is = jar.getInputStream(entry);final OutputStream fos = Files.newOutputStream(path))
                        {
                            while (is.available() > 0)
                                fos.write(is.read());
                        }
                    }
                }
            }
        }
    }

    /**
     * Compress files into a .zip file.
     * @param listFiles files to compress.
     * @param destZipFile destination .zip file
     * @throws IOException if an I/O error occurred.
     * @deprecated use {@link #compressFiles(Path[], Path)} instead.
     */
    @Deprecated
    public static void compressFiles(File[] listFiles, File destZipFile) throws IOException
    {
        try(final ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(destZipFile)))
        {
            for (File file : listFiles)
            {
                if (file.isDirectory()) addFolderToZip(file, file.getName(), zos);
                else addFileToZip(file, zos);
            }

            zos.flush();
        }
    }

    /**
     * Compress files into a .zip file.
     * @param listFiles files to compress.
     * @param destZipFile destination .zip file
     * @throws IOException if an I/O error occurred.
     */
    public static void compressFiles(Path[] listFiles, Path destZipFile) throws IOException
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

    @Deprecated
    private static void addFolderToZip(File folder, String parentFolder, ZipOutputStream zos) throws IOException
    {
        for (File file : FileUtils.list(folder))
        {
            if (file.isDirectory())
            {
                addFolderToZip(file, parentFolder + "/" + file.getName(), zos);
                continue;
            }
            zos.putNextEntry(new ZipEntry(parentFolder + "/" + file.getName()));

            try(final BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file)))
            {
                final byte[] buffer = new byte[4096];
                int read;

                while ((read = bis.read(buffer)) != -1)
                    zos.write(buffer, 0, read);

                zos.closeEntry();
            }
        }
    }

    private static void addFolderToZip(Path folder, String parentFolder, ZipOutputStream zos) throws IOException
    {
        final AtomicReference<Throwable> err = new AtomicReference<>(null);
        FileUtils.list(folder).forEach(path -> {
            try {
                if (Files.isDirectory(path))
                {
                    addFolderToZip(path, parentFolder + '/' + path.getFileName().toString(), zos);
                    return;
                }
                zos.putNextEntry(new ZipEntry(parentFolder + "/" + path.getFileName().toString()));

                try(final BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(path)))
                {
                    final byte[] buffer = new byte[4096];
                    int read;

                    while ((read = bis.read(buffer)) != -1)
                        zos.write(buffer, 0, read);

                    zos.closeEntry();
                }
            } catch (IOException e)
            {
                err.set(e);
            }
        });
        if(err.get() != null) throw new IOException(err.get());
    }

    @Deprecated
    private static void addFileToZip(File file, ZipOutputStream zos) throws IOException
    {
        zos.putNextEntry(new ZipEntry(file.getName()));

        try(final BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file)))
        {
            int read;
            final byte[] buffer = new byte[4096];

            while ((read = bis.read(buffer)) != -1)
                zos.write(buffer, 0, read);

            zos.closeEntry();
        }
    }

    private static void addFileToZip(Path file, ZipOutputStream zos) throws IOException
    {
        zos.putNextEntry(new ZipEntry(file.getFileName().toString()));

        try(final BufferedInputStream bis = new BufferedInputStream(Files.newInputStream(file)))
        {
            int read;
            final byte[] buffer = new byte[4096];

            while ((read = bis.read(buffer)) != -1)
                zos.write(buffer, 0, read);

            zos.closeEntry();
        }
    }

    /**
     * Unzip a .zip file to a folder
     * @param destinationDir where the zip will be extracted.
     * @param zipPath the zip to extract.
     * @throws IOException if an I/O error occurred.
     * @deprecated use {@link #unzip(Path, Path)} instead.
     */
    @Deprecated
    public static void unzip(String destinationDir, String zipPath) throws IOException
    {
        unzip(new File(destinationDir), new File(zipPath));
    }

    /**
     * Unzip a .zip file to a folder
     * @param destinationDir where the zip will be extracted.
     * @param zipPath the zip to extract.
     * @throws IOException if an I/O error occurred.
     * @deprecated use {@link #unzip(Path, Path)} instead.
     */
    @Deprecated
    public static void unzip(File destinationDir, String zipPath) throws IOException
    {
        unzip(destinationDir, new File(zipPath));
    }

    /**
     * Unzip a .zip file to a folder
     * @param destinationDir where the zip will be extracted.
     * @param zipFile the zip to extract.
     * @throws IOException if an I/O error occurred.
     * @deprecated use {@link #unzip(Path, Path)} instead.
     */
    @Deprecated
    public static void unzip(String destinationDir, File zipFile) throws IOException
    {
        unzip(new File(destinationDir), zipFile);
    }

    /**
     * Unzip a .zip file to a folder
     * @param destinationDir where the zip will be extracted.
     * @param zipFile the zip to extract.
     * @throws IOException if an I/O error occurred.
     * @deprecated use {@link #unzip(Path, Path)} instead.
     */
    @Deprecated
    public static void unzip(File destinationDir, File zipFile) throws IOException
    {
        try(final ZipFile toUnZip = new ZipFile(zipFile))
        {
            final Enumeration<? extends ZipEntry> enu = toUnZip.entries();
            while (enu.hasMoreElements())
            {
                final ZipEntry entry = enu.nextElement();
                final File fl = new File(destinationDir + File.separator + entry.getName());

                unzip0(fl, toUnZip, entry);
            }
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

    @Deprecated
    private static void unzip0(File fl, ZipFile zipFile, ZipEntry entry) throws IOException
    {
        if (fl.getName().endsWith("/")) fl.mkdirs();
        if(!fl.exists())
            fl.getParentFile().mkdirs();
        if(entry.isDirectory())
            return;

        try(final InputStream is = zipFile.getInputStream(entry);final FileOutputStream fo = new FileOutputStream(fl))
        {
            while(is.available() > 0)
                fo.write(is.read());
        }
    }

    private static void unzip0(Path fl, ZipFile zipFile, ZipEntry entry) throws IOException
    {
        if (fl.getFileName().toString().endsWith("/")) Files.createDirectory(fl);
        if(Files.notExists(fl))
            Files.createDirectories(fl.getParent());
        if(entry.isDirectory())
            return;

        try(final BufferedInputStream is = new BufferedInputStream(zipFile.getInputStream(entry));final BufferedOutputStream fo = new BufferedOutputStream(Files.newOutputStream(fl)))
        {
            while(is.available() > 0)
                fo.write(is.read());
        }
    }

    /**
     * This method unzips a jar into a specified directory.
     * @param destinationDir destination folder.
     * @param jarPath the jar to extract.
     * @param args "ignoreMetaInf" option will not extract META-INF files.
     * @throws IOException if an I/O error occurred.
     * @deprecated use {@link #unzipJar(Path, Path, String...)}
     */
    @Deprecated
    public static void unzipJar(String destinationDir, String jarPath, String... args) throws IOException
    {
        try(final JarFile jar = new JarFile(Paths.get(jarPath).toFile()))
        {
            final Enumeration<JarEntry> enu = jar.entries();
            while(enu.hasMoreElements())
            {
                final JarEntry je = enu.nextElement();
                final Path fl = Paths.get(destinationDir, je.getName());

                if(args.length >= 1 && args[0] != null && args[0].equals("ignoreMetaInf"))
                {
                    if (fl.toString().contains("META-INF")) continue;
                }

                unzip0(fl, jar, je);
            }
        }
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
                    if (fl.toString().contains("META-INF")) continue;
                }

                unzip0(fl, jar, je);
            }
        }
    }

    public static void unzipJars(JarPath... jars) throws IOException
    {
        for (JarPath jar : jars)
            unzipJar(jar.getDestination(), jar.getJarPath());
    }

    public static class JarPath implements Serializable
    {
        private static final long serialVersionUID = 1L;

        private final Path destination;
        private final Path jarPath;

        public JarPath(Path destination, Path jarPath)
        {
            this.destination = destination;
            this.jarPath = jarPath;
        }

        public Path getDestination()
        {
            return destination;
        }

        public Path getJarPath()
        {
            return jarPath;
        }
    }
}
