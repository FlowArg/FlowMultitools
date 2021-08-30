package fr.flowarg.flowlogger;

import java.nio.file.Path;

public interface ILogger
{
    void err(String message);
    void info(String message);
    void warn(String message);
    void debug(String message);
    void infoColor(EnumLogColor color, String message);
    void printStackTrace(String errorName, Throwable cause);
    void printStackTrace(Throwable cause);
    Path getLogPath();
    void setLogPath(Path logPath);
    String getPrefix();
    
    default void writeToTheLogFile(String toLog) {}
    default void close() {}

    /**
     * A POSIX-friendly color enumeration
      */
    enum EnumLogColor
    {
        RESET("\u001B[0m"),
        BOLD("\u001B[1m"),
        UNDERLINE("\u001B[4m"),
        BLACK_ON_WHITE("\u001B[7m"),
        CROSSED_OUT("\u001B[9m"),
        BOLD_UNDERLINE("\u001B[21m"),
        BLACK("\u001B[30m"),
        RED("\u001B[31m"),
        GREEN("\u001B[32m"),
        YELLOW("\u001B[33m"),
        BLUE("\u001B[34m"),
        PURPLE("\u001B[35m"),
        CYAN("\u001B[36m"),
        WHITE("\u001B[37m"),
        BLACK_BACKGROUND("\u001B[40m"),
        RED_BACKGROUND("\u001B[41m"),
        GREEN_BACKGROUND("\u001B[42m"),
        YELLOW_BACKGROUND("\u001B[43m"),
        BLUE_BACKGROUND("\u001B[44m"),
        PURPLE_BACKGROUND("\u001B[45m"),
        CYAN_BACKGROUND("\u001B[46m"),
        WHITE_BACKGROUND("\u001B[47m"),
        BORDER_1("\u001B[51m"),
        BORDER_2("\u001B[52m"),
        FLASH_BLACK("\u001B[90m"),
        FLASH_RED("\u001B[91m"),
        FLASH_GREEN("\u001B[92m"),
        FLASH_YELLOW("\u001B[93m"),
        FLASH_BLUE("\u001B[94m"),
        FLASH_PURPLE("\u001B[95m"),
        FLASH_CYAN("\u001B[96m"),
        FLASH_WHITE("\u001B[97m"),
        FLASH_BLACK_BACKGROUND("\u001B[100m"),
        FLASH_RED_BACKGROUND("\u001B[101m"),
        FLASH_GREEN_BACKGROUND("\u001B[102m"),
        FLASH_YELLOW_BACKGROUND("\u001B[103m"),
        FLASH_BLUE_BACKGROUND("\u001B[104m"),
        FLASH_PURPLE_BACKGROUND("\u001B[105m"),
        FLASH_CYAN_BACKGROUND("\u001B[106m"),
        FLASH_WHITE_BACKGROUND("\u001B[107m");

        private final String color;

        EnumLogColor(String color)
        {
            this.color = color;
        }

        public String getColor()
        {
            return this.color;
        }

        @Override
        public String toString()
        {
            return this.getColor();
        }
    }
}
