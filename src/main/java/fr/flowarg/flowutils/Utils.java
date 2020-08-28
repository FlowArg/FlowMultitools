package fr.flowarg.flowutils;

public class Utils
{
    public static String empty(String baseStr, String str)
    {
        return baseStr.replace(str, "");
    }

    public static void exit(int exitCode, boolean halt)
    {
        if (halt) Runtime.getRuntime().halt(exitCode);
        else System.exit(exitCode);
    }
    
    public static char getLastChar(String str)
    {
        return str.toCharArray()[str.length() - 1];
    }

    public static String replace(String str, String first, String second)
    {
        return str.replace(first, second);
    }
}
