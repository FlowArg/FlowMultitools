package fr.flowarg.flowio;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

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
    
    public static File removeExtension(final File file) throws IOException
    {
        if(!getFileExtension(file).isEmpty())
            return Files.move(file.toPath(), new File(removeExtension(file.getName())).toPath(), StandardCopyOption.REPLACE_EXISTING).toFile();
        return file;
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
            final List<File> files = listRecursive(folder);
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

    public static List<File> listRecursive(final File directory)
    {
        final List<File> files = new ArrayList<>();
        final File[] fs = list(directory);

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
        final BufferedInputStream fis = new BufferedInputStream(new FileInputStream(file));

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
                if(fl.getAbsolutePath().contains("META-INF")) continue;
            if (fl.getName().endsWith("/")) fl.mkdirs();
            if(!fl.exists())
                fl.getParentFile().mkdirs();
            if(je.isDirectory())
                continue;
            final InputStream is = jar.getInputStream(je);
            final FileOutputStream fo = new FileOutputStream(fl);
            while(is.available() > 0)
                fo.write(is.read());
            fo.close();
            is.close();
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

    public static String getSHA1(final File file) throws NoSuchAlgorithmException, IOException
    {
        try(final FileInputStream fi = new FileInputStream(file);final BufferedInputStream input = new BufferedInputStream(fi))
        {
            final MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
            final byte[] data = new byte[8192];
            int read;
            while ((read = input.read(data)) != -1)
                sha1.update(data, 0, read);
            
            final byte[] hashBytes = sha1.digest();
            final StringBuilder sb = new StringBuilder();
            for (byte hashByte : hashBytes)
                sb.append(Integer.toString((hashByte & 0xff) + 0x100, 16).substring(1));
            
            return sb.toString();
        }
    }

    public static File[] list(final File dir)
    {
        final File[] files = dir.listFiles();
        return files == null ? new File[0] : files;
    }
    
    public static long getCRC32(File file) throws IOException
    {
        final Checksum checksum = new CRC32();
        final byte[] bytes = Files.readAllBytes(file.toPath());
        checksum.update(bytes, 0, bytes.length);
        return checksum.getValue();
    }
}
