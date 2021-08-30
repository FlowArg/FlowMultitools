package fr.flowarg.flowstringer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class StringUtilsTest
{
    private String baseStr;

    @BeforeEach
    public void setup()
    {
        this.baseStr = "This is a test";
    }

    @Test
    public void testEmpty()
    {
        assertEquals("This is a functional method!", StringUtils.empty(this.baseStr, "test") + "functional method!");
    }

    @Test
    public void testGetFirstChar()
    {
        assertEquals('T', StringUtils.getFirstChar(this.baseStr));
    }

    @Test
    public void testGetLastChar()
    {
        assertEquals('t', StringUtils.getLastChar(this.baseStr));
    }

    @Test
    public void testReplace()
    {
        assertEquals("This is a good test.", StringUtils.replace(this.baseStr, "test", "good test."));
    }

    @Test
    public void testFromString()
    {
        assertEquals("[84, 104, 105, 115, 32, 105, 115, 32, 97, 32, 116, 101, 115, 116]", Arrays.toString(StringUtils.fromString(this.baseStr)));
    }

    @Test
    public void testToStringFromBytes()
    {
        assertEquals(this.baseStr, StringUtils.toString(new byte[]{84, 104, 105, 115, 32, 105, 115, 32, 97, 32, 116, 101, 115, 116}));
    }

    @Test
    public void testCheckString()
    {
        assertFalse(StringUtils.checkString(null));
        assertFalse(StringUtils.checkString(""));
        assertFalse(StringUtils.checkString("     "));
        assertFalse(StringUtils.checkString("  \n   "));
        assertTrue(StringUtils.checkString(this.baseStr));
    }

    @Test
    public void testToStringFromList()
    {
        assertEquals("Thisisatest", StringUtils.toString(Arrays.asList(this.baseStr.split(" "))));
    }
}
