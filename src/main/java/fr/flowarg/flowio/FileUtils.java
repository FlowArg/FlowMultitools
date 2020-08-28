package fr.flowarg.flowio;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.CRC32;
import java.util.zip.Checksum;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;

import org.codehaus.plexus.archiver.tar.TarGZipUnArchiver;
import org.codehaus.plexus.logging.console.ConsoleLoggerManager;

public final class FileUtils
{  
    public static String getFileExtension(final File file)
    {
        final String fileName = file.getName();
        final int dotIndex = fileName.lastIndexOf(46);
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

    public static String removeExtension(final String fileName)
    {
        if (fileName == null)
            return "";    
        if (!getFileExtension(new File(fileName)).isEmpty())
            return fileName.substring(0, fileName.lastIndexOf(46));
        return fileName;
    }

    public static void createFile(final File file) throws IOException
    {
        if (!file.exists())
        {
            file.getParentFile().mkdirs();
            file.createNewFile();
        }
    }

    public static void saveFile(File file, String text) throws IOException
    {
        final BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(text);
        writer.flush();
        writer.close();
    }
    
    public static String loadFile(final File file) throws IOException
    {
        if (file.exists())
        {
            final BufferedReader reader = new BufferedReader(new FileReader(file));
            final StringBuilder text = new StringBuilder();

            String line;

            while ((line = reader.readLine()) != null)
                text.append(line);
            
            reader.close();
            return text.toString();
        }
        return "";
    }

    public static void deleteDirectory(final File folder)
    {
        if (folder.exists() && folder.isDirectory())
        {
            final ArrayList<File> files = listRecursive(folder);
            if (files.isEmpty())
            {
                folder.delete();
                return;
            }
            for (final File f : files)
                f.delete();
            
            folder.delete();
        }
    }
    
    public static void deleteExclude(File toDelete, File... excludes)
    {
        boolean flag = true;
        for (File exclude : excludes)
        {
            if(exclude.getAbsolutePath().equals(toDelete.getAbsolutePath()))
            {
                flag = false;
                break;
            }
        }
        if(flag)
            toDelete.delete();
    }

    public static ArrayList<File> listRecursive(final File directory)
    {
        final ArrayList<File> files = new ArrayList<>();
        final File[] fs = directory.listFiles();
        if (fs == null) return files;

        for (final File f : fs)
        {
            if (f.isDirectory())
            	files.addAll(listRecursive(f));
            files.add(f);
        }
        return files;
    }

    public static void createDirectories(String location, String... dirsToCreate) throws IOException
    {
        for (String s : dirsToCreate)
        {
            final File f = new File(location, s);

            if (!f.exists()) Files.createDirectory(Paths.get(location + s));
        }
    }

    public static long getFileSizeMegaBytes(File file)
    {
        return file.length() / (1024 * 1024);
    }
    public static long getFileSizeKiloBytes(File file)
    {
        return  file.length() / 1024;
    }
    public static long getFileSizeBytes(File file)
    {
        return file.length();
    }

    public static String getStringPathOfClass(Class<?> classToGetPath)
    {
        return classToGetPath.getProtectionDomain().getCodeSource().getLocation().getPath();
    }

    public static File getFilePathOfClass(Class<?> classToGetPath)
    {
        return new File(classToGetPath.getProtectionDomain().getCodeSource().getLocation().getPath());
    }
    
    public static String getFileChecksum(MessageDigest digest, File file) throws IOException
    {
        final FileInputStream fis = new FileInputStream(file);

        final byte[] byteArray = new byte[1024];
        int bytesCount;

        while ((bytesCount = fis.read(byteArray)) != -1)
            digest.update(byteArray, 0, bytesCount);

        fis.close();

        final byte[] bytes = digest.digest();

        final StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes)
            sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        
        return sb.toString();
    }

    
    public static String getMD5ofFile(final File file) throws NoSuchAlgorithmException, IOException
    {
        final MessageDigest md5Digest = MessageDigest.getInstance("MD5");
        return getFileChecksum(md5Digest, file);
    }

    public static void unzipJar(String destinationDir, String jarPath) throws IOException
    {
        final File file = new File(jarPath);
        final JarFile jar = new JarFile(file);

        for (Enumeration<JarEntry> enums = jar.entries(); enums.hasMoreElements();)
        {
            final JarEntry entry = enums.nextElement();

            final String fileName = destinationDir + File.separator + entry.getName();
            final File f = new File(fileName);

            if (fileName.endsWith("/")) f.mkdirs();
        }

        for (Enumeration<JarEntry> enums = jar.entries(); enums.hasMoreElements();)
        {
            final JarEntry entry = enums.nextElement();

            final String fileName = destinationDir + File.separator + entry.getName();
            final File f = new File(fileName);

            if (!fileName.endsWith("/"))
            {
                final InputStream is = jar.getInputStream(entry);
                final FileOutputStream fos = new FileOutputStream(f);

                while (is.available() > 0)
                    fos.write(is.read());
                
                fos.close();
                is.close();
            }
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
		
		private String destination;
        private String jarPath;

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

    public static String getSHA1(final File file)
    {
        try
        {
            try (final InputStream input = new FileInputStream(file))
            {
                final MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
                final byte[] buffer = new byte[8192];
                for (int len = input.read(buffer); len != -1; len = input.read(buffer))
                    sha1.update(buffer, 0, len);
                return new HexBinaryAdapter().marshal(sha1.digest()).toLowerCase();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    
    public static File[] list(final File dir)
    {
        final File[] files = dir.listFiles();
        return files == null ? new File[0] : files;
    }

    public static void decompressTarArchive(final File tarGzFile, final File destinationDir)
    {
        final TarGZipUnArchiver unArchiver = new TarGZipUnArchiver();
        final ConsoleLoggerManager console = new ConsoleLoggerManager();
        console.initialize();
        unArchiver.enableLogging(console.getLoggerForComponent("[Launcher - Guns of Chickens]"));
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
            final FileInputStream fileInputStream = new FileInputStream(baseFile);
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
    
    public static long getCRC32(File file) throws IOException
    {
    	final Checksum checksum = new CRC32();
    	final byte[] bytes = Files.readAllBytes(file.toPath());
    	checksum.update(bytes, 0, bytes.length);
    	return checksum.getValue();
    }
}
