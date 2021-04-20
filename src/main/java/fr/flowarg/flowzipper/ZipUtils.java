package fr.flowarg.flowzipper;

import fr.flowarg.flowio.FileUtils;
import org.codehaus.plexus.archiver.tar.TarGZipUnArchiver;
import org.codehaus.plexus.logging.console.ConsoleLoggerManager;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public final class ZipUtils
{
    public static void decompressTarArchive(final File tarGzFile, final File destinationDir)
    {
        final TarGZipUnArchiver unArchiver = new TarGZipUnArchiver();
        final ConsoleLoggerManager loggerManager = new ConsoleLoggerManager();
        loggerManager.initialize();
        unArchiver.setSourceFile(tarGzFile);
        unArchiver.enableLogging(loggerManager.getLoggerForComponent("[FlowMultitools]"));
        unArchiver.setDestDirectory(destinationDir);
        destinationDir.mkdirs();
        unArchiver.extract();
    }

    public static void gzipFile(String baseFile, String newFile) throws IOException
    {
        final byte[] buffer = new byte[1024];

        if(baseFile != null && newFile != null)
        {
            final FileOutputStream fileOutputStream = new FileOutputStream(newFile);
            final GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fileOutputStream);
            final BufferedInputStream fileInputStream = new BufferedInputStream(new FileInputStream(baseFile));
            int bytesRead;

            while ((bytesRead = fileInputStream.read(buffer)) > 0)
                gzipOutputStream.write(buffer, 0, bytesRead);

            fileInputStream.close();
            gzipOutputStream.finish();
            gzipOutputStream.close();
        }
    }

    public static void unzipJarWithLZMACompat(final File destinationDir, final File jarFile) throws IOException
    {
        final JarFile jar = new JarFile(jarFile);

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
                    final InputStream stream = jar.getInputStream(entry);
                    Files.copy(stream, new File(destinationDir, entry.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
                    stream.close();
                }
                else
                {
                    final InputStream is = jar.getInputStream(entry);
                    final FileOutputStream fos = new FileOutputStream(file);

                    while (is.available() > 0)
                        fos.write(is.read());

                    fos.close();
                    is.close();
                }
                jar.getInputStream(entry).close();
            }
        }

        jar.close();
    }

    public static void compressFiles(File[] listFiles, File destZipFile) throws IOException
    {
        final ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(destZipFile));

        for (File file : listFiles)
        {
            if (file.isDirectory()) addFolderToZip(file, file.getName(), zos);
            else addFileToZip(file, zos);
        }

        zos.flush();
        zos.close();
    }

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

            final BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            final byte[] buffer = new byte[4096];
            int read;

            while ((read = bis.read(buffer)) != -1)
                zos.write(buffer, 0, read);

            zos.closeEntry();
            bis.close();
        }
    }

    private static void addFileToZip(File file, ZipOutputStream zos) throws IOException
    {
        zos.putNextEntry(new ZipEntry(file.getName()));

        final BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));

        int read;
        final byte[] buffer = new byte[1024];

        while ((read = bis.read(buffer)) != -1)
            zos.write(buffer, 0, read);

        zos.closeEntry();
        bis.close();
    }

    public static void unzip(String destinationDir, String zipPath) throws IOException
    {
        unzip(new File(destinationDir), new File(zipPath));
    }

    public static void unzip(File destinationDir, String zipPath) throws IOException
    {
        unzip(destinationDir, new File(zipPath));
    }

    public static void unzip(String destinationDir, File zipFile) throws IOException
    {
        unzip(new File(destinationDir), zipFile);
    }

    public static void unzip(File destinationDir, File zipFile) throws IOException
    {
        final ZipFile toUnZip = new ZipFile(zipFile);
        final Enumeration<? extends ZipEntry> enu = toUnZip.entries();
        while (enu.hasMoreElements())
        {
            final ZipEntry entry = enu.nextElement();
            final File fl = new File(destinationDir + File.separator + entry.getName());

            unzip0(fl, toUnZip, entry);
        }

        toUnZip.close();
    }

    private static void unzip0(File fl, ZipFile zipFile, ZipEntry entry) throws IOException
    {
        if (fl.getName().endsWith("/")) fl.mkdirs();
        if(!fl.exists())
            fl.getParentFile().mkdirs();
        if(entry.isDirectory())
            return;

        final InputStream is = zipFile.getInputStream(entry);
        final FileOutputStream fo = new FileOutputStream(fl);
        while(is.available() > 0)
            fo.write(is.read());
        fo.close();
        is.close();
    }

    public static void unzipJar(String destinationDir, String jarPath, String... args) throws IOException
    {
        final File file = new File(jarPath);
        final JarFile jar = new JarFile(file);
        final Enumeration<JarEntry> enu = jar.entries();
        while(enu.hasMoreElements())
        {
            final JarEntry je = enu.nextElement();
            final File fl = new File(destinationDir + File.separator + je.getName());

            if(args.length >= 1 && args[0] != null && args[0].equals("ignoreMetaInf"))
            {
                if (fl.getAbsolutePath().contains("META-INF")) continue;
            }

            unzip0(fl, jar, je);
        }

        jar.close();
    }

    public static void unzipJars(JarPath... jars) throws IOException
    {
        for (JarPath jar : jars)
            unzipJar(jar.getDestination(), jar.getJarPath());
    }

    public static class JarPath implements Serializable
    {
        private static final long serialVersionUID = 1L;

        private final String destination;
        private final String jarPath;

        public JarPath(String destination, String jarPath)
        {
            this.destination = destination;
            this.jarPath = jarPath;
        }

        public String getDestination()
        {
            return destination;
        }

        public String getJarPath()
        {
            return jarPath;
        }
    }
}
