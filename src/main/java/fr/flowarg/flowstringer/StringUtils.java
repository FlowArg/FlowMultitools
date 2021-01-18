package fr.flowarg.flowstringer;

import java.nio.charset.StandardCharsets;

public final class StringUtils
{
    public static String empty(String baseStr, String str)
    {
        return baseStr.replace(str, "");
    }

    public static char getFirstChar(String str)
    {
        return str.toCharArray()[0];
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
        return str.getBytes(StandardCharsets.UTF_8);
    }
    
    public static String toString(byte[] bytes)
    {
        return new String(bytes, 0, bytes.length, StandardCharsets.UTF_8);
    }
    
    public static boolean checkString(String str)
    {
        return str != null && !str.trim().equals("");
    }
}
