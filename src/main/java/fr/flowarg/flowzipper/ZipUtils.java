package fr.flowarg.flowzipper;

import org.codehaus.plexus.archiver.tar.TarGZipUnArchiver;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils
{
    public static void decompressTarArchive(final File tarGzFile, final File destinationDir)
    {
        final TarGZipUnArchiver unArchiver = new TarGZipUnArchiver();
        unArchiver.setSourceFile(tarGzFile);
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
        for (File file : folder.listFiles())
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

        final byte[] buffer = new byte[1024];
        int read;
        while ((read = bis.read(buffer)) != -1)
            zos.write(buffer, 0, read);

        zos.closeEntry();
        bis.close();
    }
}
