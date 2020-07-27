package fr.flowarg.flowio;

import org.codehaus.plexus.archiver.tar.TarGZipUnArchiver;
import org.codehaus.plexus.logging.console.ConsoleLoggerManager;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.GZIPOutputStream;

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
            file.mkdirs();
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
            final ArrayList<File> files = listFilesForFolder(folder);
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
    
    public static String getMD5FromURL(String input)
    {
        try
        {
            final MessageDigest md = MessageDigest.getInstance("MD5");
            InputStream is = new URL(input).openStream();

            try
            {
                is = new DigestInputStream(is, md);

                final byte[] ignoredBuffer = new byte[8 * 1024];

                while (is.read(ignoredBuffer) > 0) ;

            }
            finally
            {
                is.close();
            }
            final byte[] digest = md.digest();
            final StringBuffer sb = new StringBuffer();

            for (byte b : digest)
                sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            
            return sb.toString();

        }
        catch (Exception ex)
        {
            throw new RuntimeException(ex);
        }
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

    
    public static ArrayList<File> listFilesForFolder(final File folder)
    {
        final ArrayList<File> files = new ArrayList<>();
        File[] listFiles;
        for (int length = (listFiles = folder.listFiles()).length, i = 0; i < length; ++i)
        {
            final File fileEntry = listFiles[i];
            if (fileEntry.isDirectory())
                files.addAll(listFilesForFolder(fileEntry));           
            files.add(fileEntry);
        }
        return files;
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
}
