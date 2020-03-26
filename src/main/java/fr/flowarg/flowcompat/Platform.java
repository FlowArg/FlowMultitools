package fr.flowarg.flowcompat;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Platform
{
    public static final String OS = System.getProperty("os.name").toLowerCase();

    public static boolean isOnMac()
    {
        EnumOS os = EnumOS.MAC;
        AtomicBoolean bool = new AtomicBoolean(false);
        os.getNames().forEach(alias ->
        {
            if (OS.contains(alias)) bool.set(true);
        });
        return bool.get();
    }

    public static boolean isOnWindows()
    {
        EnumOS os = EnumOS.WINDOWS;
        AtomicBoolean bool = new AtomicBoolean(false);
        os.getNames().forEach(alias ->
        {
            if (OS.contains(alias)) bool.set(true);
        });
        return bool.get();
    }

    public static boolean isOnLinux()
    {
        EnumOS os = EnumOS.LINUX;
        AtomicBoolean bool = new AtomicBoolean(false);
        os.getNames().forEach(alias ->
        {
            if (OS.contains(alias)) bool.set(true);
        });
        return bool.get();
    }

    public static String getArch()
    {
        return System.getProperty("sun.arch.data.model");
    }

    enum EnumOS
    {
        MAC(Arrays.asList("mac", "osx", "macos", "darwin")),
        WINDOWS(Arrays.asList("windows", "win")),
        LINUX(Arrays.asList("linux", "unix"));

        private List<String> names;

        EnumOS(List<String> names)
        {
            this.names = names;
        }

        public List<String> getNames()
        {
            return this.names;
        }
    }
}
