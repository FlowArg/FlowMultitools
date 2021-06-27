package fr.flowarg.flowcompat;

public final class Platform
{
    public static final String OS = System.getProperty("os.name", "").toLowerCase();

    public static void exit(int exitCode, boolean halt)
    {
        if (halt) Runtime.getRuntime().halt(exitCode);
        else System.exit(exitCode);
    }

    public static boolean isOn(EnumOS platform)
    {
        for (String alias : platform.getNames())
        {
            if (OS.contains(alias))
                return true;
        }
        return false;
    }

    public static boolean isOnMac()
    {
        return isOn(EnumOS.MAC);
    }

    public static boolean isOnWindows()
    {
        return isOn(EnumOS.WINDOWS);
    }

    public static boolean isOnLinux()
    {
        return isOn(EnumOS.LINUX);
    }
    
    public static EnumOS getCurrentPlatform()
    {
        for(EnumOS en : EnumOS.values())
        {
	        for (int i = 0; i < en.getNames().length; i++)
            {
                if(en.getNames()[i].equalsIgnoreCase(OS))
                    return en;
            }

	        for (String s : en.getNames())
	        {
	            if(OS.contains(s))
	                return en;
	        }
        }
        
        return null;
    }

    public static String getArch()
    {
        return System.getProperty("sun.arch.data.model");
    }

    public enum EnumOS
    {
        MAC(new String[]{"mac", "osx", "macos", "darwin"}),
        WINDOWS(new String[]{"windows", "win"}),
        LINUX(new String[]{"linux", "unix"});

        private final String[] names;

        EnumOS(String[] names)
        {
            this.names = names;
        }

        public String[] getNames()
        {
            return this.names;
        }
    }
}
