package fr.flowarg.flowcompat;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Platform
{
    public static final String OS = System.getProperty("os.name").toLowerCase();

    public static boolean isOnMac()
    {
        final EnumOS os = EnumOS.MAC;
        final AtomicBoolean bool = new AtomicBoolean(false);
        os.getNames().forEach(alias ->
        {
            if (OS.contains(alias)) bool.set(true);
        });
        return bool.get();
    }

    public static boolean isOnWindows()
    {
        final EnumOS os = EnumOS.WINDOWS;
        final AtomicBoolean bool = new AtomicBoolean(false);
        os.getNames().forEach(alias ->
        {
            if (OS.contains(alias)) bool.set(true);
        });
        return bool.get();
    }

    public static boolean isOnLinux()
    {
        final EnumOS os = EnumOS.LINUX;
        final AtomicBoolean bool = new AtomicBoolean(false);
        os.getNames().forEach(alias ->
        {
            if (OS.contains(alias)) bool.set(true);
        });
        return bool.get();
    }
    
    public static EnumOS getCurrentPlatform()
    {
        for(EnumOS en : EnumOS.values())
        {
            if(en.getNames().contains(OS))
                return en;
        }
        
        return null;
    }

    public static String getArch()
    {
        return System.getProperty("sun.arch.data.model");
    }

    public enum EnumOS
    {
        MAC(Arrays.asList("mac", "osx", "macos", "darwin")),
        WINDOWS(Arrays.asList("windows", "win")),
        LINUX(Arrays.asList("linux", "unix"));

        private final List<String> names;

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
