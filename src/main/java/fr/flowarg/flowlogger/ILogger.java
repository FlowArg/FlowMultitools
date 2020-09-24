package fr.flowarg.flowlogger;

import java.io.File;

public interface ILogger
{
	void err(String message);
	void info(String message);
	void warn(String message);
	void debug(String message);
	void infoColor(EnumLogColor color, String message);
	void printStackTrace(String errorName, Throwable cause);
	void printStackTrace(Throwable cause);
	File getLogFile();
	String getPrefix();
	
	default void writeToTheLogFile(String toLog) {}
	
    enum EnumLogColor
    {
        RESET("\u001B[0m"),
        BLACK("\u001B[30m"),
        RED("\u001B[31m"),
        GREEN("\u001B[32m"),
        YELLOW("\u001B[33m"),
        BLUE("\u001B[34m"),
        PURPLE("\u001B[35m"),
        CYAN("\u001B[36m"),
        WHITE("\u001B[37m");

        private final String color;

        EnumLogColor(String color)
        {
            this.color = color;
        }

        public String getColor()
        {
            return color;
        }
    }
}
