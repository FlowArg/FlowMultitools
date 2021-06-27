package fr.flowarg.flowio;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public final class FileUtils
{
    /**
     * Get the extension of the given file.
     * @param file given file.
     * @return the extension of the given file.
     * @deprecated use {@link #getFileExtension(Path)} instead.
     */
    @Deprecated
    public static String getFileExtension(final File file)
    {
        final String fileName = file.getName();
        final int dotIndex = fileName.lastIndexOf(46);
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

    /**
     * Remove the extension of the given file.
     * @param file given file.
     * @return the given file without extension.
     * @throws IOException if an I/O error occurred.
     * @deprecated use {@link #removeExtension(Path)} instead.
     */
    @Deprecated
    public static File removeExtension(final File file) throws IOException
    {
        if(!getFileExtension(file).isEmpty())
            return Files.move(file.toPath(), new File(removeExtension(file.getName())).toPath(), StandardCopyOption.REPLACE_EXISTING).toFile();
        return file;
    }

    public static String removeExtension(final String fileName)
    {
        if (fileName == null)
            return "";
        if (!getFileExtension(Paths.get(fileName)).isEmpty())
            return fileName.substring(0, fileName.lastIndexOf(46));
        return fileName;
    }

    /**
     * Get the extension of the given path.
     * @param path given path.
     * @return the extension of the given path.
     */
    public static String getFileExtension(final Path path)
    {
        final String fileName = path.getFileName().toString();
        final int dotIndex = fileName.lastIndexOf(46);
        return (dotIndex == -1) ? "" : fileName.substring(dotIndex + 1);
    }

    /**
     * Remove the extension of the given path.
     * @param path given path.
     * @return the given path without extension.
     * @throws IOException if an I/O error occurred.
     */
    public static Path removeExtension(final Path path) throws IOException
    {
        if(!getFileExtension(path).isEmpty())
            return Files.move(path, Paths.get(removeExtension(path.getFileName().toString())), StandardCopyOption.REPLACE_EXISTING);
        return path;
    }

    /**
     * Delete the given directory
     * @param folder folder to delete.
     * @deprecated use {@link #deleteDirectory(Path)} instead.
     */
    @Deprecated
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

    /**
     * Delete the given directory
     * @param folder folder to delete.
     * @throws IOException if an I/O error occurred.
     */
    public static void deleteDirectory(final Path folder) throws IOException
    {
        if (Files.exists(folder) && Files.isDirectory(folder))
        {
            final List<Path> files = listRecursive(folder);
            for (final Path f : files)
                Files.delete(f);

            Files.delete(folder);
        }
    }

    /**
     * Delete the file if it isn't in excluded files.
     * @param toDelete the file to delete.
     * @param excludes whitelist
     * @deprecated use {@link #deleteExclude(Path, Path...)} instead.
     */
    @Deprecated
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
        if(flag) toDelete.delete();
    }

    /**
     * Delete the file if it isn't in excluded files.
     * @param toDelete the file to delete.
     * @param excludes whitelist
     * @throws IOException if an I/O error occurred.
     */
    public static void deleteExclude(Path toDelete, Path... excludes) throws IOException
    {
        boolean flag = true;
        for (Path exclude : excludes)
        {
            if(exclude.toString().equals(toDelete.toString()))
            {
                flag = false;
                break;
            }
        }
        if(flag) Files.deleteIfExists(toDelete);
    }

    /**
     * Return all files in the directory (recursively!)
     * @param directory the directory to process.
     * @return the list of all files.
     * @deprecated use {@link #listRecursive(Path)} instead.
     */
    @Deprecated
    public static List<File> listRecursive(final File directory)
    {
        final List<File> files = new ArrayList<>();
        final File[] fs = list(directory);

        for (final File f : fs)
        {
            if (f.isDirectory()) files.addAll(listRecursive(f));
            files.add(f);
        }
        return files;
    }

    /**
     * Return all files in the directory (recursively!)
     * @param directory the directory to process.
     * @return the list of all files.
     * @throws IOException if an I/O error occurred.
     */
    public static List<Path> listRecursive(final Path directory) throws IOException
    {
        final List<Path> files = new ArrayList<>();
        final List<Path> fs = list(directory);

        for (final Path f : fs)
        {
            if (Files.isDirectory(f)) files.addAll(listRecursive(f));
            files.add(f);
        }
        return files;
    }

    public static void createDirectories(String location, String... dirsToCreate) throws IOException
    {
        for (String s : dirsToCreate)
        {
            final Path path = Paths.get(location, s);

            if (Files.notExists(path)) Files.createDirectory(path);
        }
    }

    /**
     * Get the file size in MB
     * @param file the file to process.
     * @return the size in MB of the given file.
     * @deprecated Deprecated since 1.2.7. Use {@link #getFileSizeMegaBytes(Path)} instead.
     */
    @Deprecated
    public static long getFileSizeMegaBytes(File file)
    {
        return file.length() / (1024 * 1024);
    }

    /**
     * Get the file size in KB
     * @param file the file to process.
     * @return the size in KB of the given file.
     * @deprecated Deprecated since 1.2.7. Use {@link #getFileSizeKiloBytes(Path)} instead.
     */
    @Deprecated
    public static long getFileSizeKiloBytes(File file)
    {
        return  file.length() / 1024;
    }

    /**
     * Get the file size in bytes.
     * @param file the file to process.
     * @return the size in bytes of the given file.
     * @deprecated Deprecated since 1.2.7. Use {@link #getFileSizeBytes(Path)} instead.
     */
    @Deprecated
    public static long getFileSizeBytes(File file)
    {
        return file.length();
    }

    public static long getFileSizeMegaBytes(Path path) throws IOException
    {
        return getFileSizeBytes(path) / (1024 * 1024);
    }

    public static long getFileSizeKiloBytes(Path path) throws IOException
    {
        return getFileSizeBytes(path) / 1024;
    }
    
    public static long getFileSizeBytes(Path path) throws IOException
    {
        return Files.size(path);
    }

    public static String getStringPathOfClass(Class<?> classToGetPath)
    {
        return classToGetPath.getProtectionDomain().getCodeSource().getLocation().getPath();
    }

    @Deprecated
    public static File getFilePathOfClass(Class<?> classToGetPath)
    {
        return new File(classToGetPath.getProtectionDomain().getCodeSource().getLocation().getPath());
    }

    public static Path getPathOfClass(Class<?> classToGetPath)
    {
        return Paths.get(classToGetPath.getProtectionDomain().getCodeSource().getLocation().getPath());
    }

    public static String hashInput(InputStream input, String method) throws NoSuchAlgorithmException, IOException
    {
        final MessageDigest digest = MessageDigest.getInstance(method);
        final byte[] data = new byte[8192];

        int read;
        while((read = input.read(data)) != -1)
            digest.update(data, 0, read);

        final byte[] bytes = digest.digest();
        final StringBuilder sb = new StringBuilder();

        for (byte aByte : bytes)
            sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));

        return sb.toString();
    }

    /**
     * Get the MD5 of the given file.
     * @param file the file to process.
     * @return the md5 of the file.
     * @throws IOException is an I/O error occurred.
     * @deprecated use {@link #getMD5(Path)} instead.
     */
    @Deprecated
    public static String getMD5ofFile(final File file) throws IOException
    {
        try(final FileInputStream fi = new FileInputStream(file); final BufferedInputStream input = new BufferedInputStream(fi))
        {
            return hashInput(input, "MD5");
        } catch (NoSuchAlgorithmException e)
        {
            throw new IOException(e);
        }
    }

    /**
     * Get the MD5 of the given path.
     * @param path the path to process.
     * @return the md5 of the path.
     * @throws IOException is an I/O error occurred.
     */
    public static String getMD5(final Path path) throws IOException
    {
        try(InputStream in = Files.newInputStream(path); final BufferedInputStream input = new BufferedInputStream(in))
        {
            return hashInput(input, "MD5");
        } catch (NoSuchAlgorithmException e)
        {
            throw new IOException(e);
        }
    }

    /**
     * Get the SHA1 of the given file.
     * @param file the file to process.
     * @return the sha1 of the file.
     * @throws IOException is an I/O error occurred.
     * @deprecated use {@link #getSHA1(Path)} instead.
     */
    @Deprecated
    public static String getSHA1(final File file) throws IOException
    {
        try(final FileInputStream fi = new FileInputStream(file); final BufferedInputStream input = new BufferedInputStream(fi))
        {
            return hashInput(input, "SHA-1");
        } catch (NoSuchAlgorithmException e)
        {
            throw new IOException(e);
        }
    }

    /**
     * Get the SHA1 of the given path.
     * @param path the path to process.
     * @return the sha1 of the path.
     * @throws IOException is an I/O error occurred.
     */
    public static String getSHA1(final Path path) throws IOException
    {
        try(InputStream in = Files.newInputStream(path); final BufferedInputStream input = new BufferedInputStream(in))
        {
            return hashInput(input, "SHA-1");
        } catch (NoSuchAlgorithmException e)
        {
            throw new IOException(e);
        }
    }

    /**
     * Get the list of all files in this directory (not recursively!)
     * @param dir the dir to process.
     * @return the list of all files in this directory.
     * @deprecated use {@link #list(Path)} instead.
     */
    @Deprecated
    public static File[] list(final File dir)
    {
        final File[] files = dir.listFiles();
        return files == null ? new File[0] : files;
    }

    /**
     * Get the list of all files in this directory (not recursively!)
     * @param dir the dir to process.
     * @return the list of all files in this directory.
     * @throws IOException if an I/O error occurred.
     */
    public static List<Path> list(final Path dir) throws IOException
    {
        return Files.exists(dir) ? Files.list(dir).collect(Collectors.toList()) : new ArrayList<>();
    }

    /**
     * Get the CRC32 of a File.
     * @param file the file to process
     * @return the CRC32 of the file.
     * @throws IOException is an I/O error occurred.
     * @deprecated use {@link #getCRC32(Path)} instead.
     */
    @Deprecated
    public static long getCRC32(File file) throws IOException
    {
        final Checksum checksum = new CRC32();
        final byte[] bytes = Files.readAllBytes(file.toPath());
        checksum.update(bytes, 0, bytes.length);
        return checksum.getValue();
    }

    /**
     * Get the CRC32 of a File.
     * @param path the path to process
     * @return the CRC32 of the file.
     * @throws IOException is an I/O error occurred.
     */
    public static long getCRC32(Path path) throws IOException
    {
        final Checksum checksum = new CRC32();
        final byte[] bytes = Files.readAllBytes(path);
        checksum.update(bytes, 0, bytes.length);
        return checksum.getValue();
    }
}
