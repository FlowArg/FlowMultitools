package fr.flowarg.flowutils;

import java.nio.charset.Charset;

public class Utils
{
	public static final Charset UTF8 = Charset.forName("UTF-8");
	
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
    
    public static byte[] fromString(String str)
    {
    	return str.getBytes(UTF8);
    }
    
    public static String toString(byte[] bytes)
    {
    	return new String(bytes, 0, bytes.length, UTF8);
    }
    
    public static boolean checkString(String str)
    {
    	return str != null && !str.trim().equals("");
    }
}
