package fr.flowarg.flowlogger;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger implements ILogger
{
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("hh:mm:ss");

    private final String prefix;
    private Path logPath;
    private PrintWriter writer;

    public Logger(String prefix, Path logPath, boolean append)
    {
        this.prefix = prefix.endsWith(" ") ? prefix : prefix + " ";

        if(logPath == null)
            return;

        this.logPath = logPath.toAbsolutePath();

        try
        {
            if (Files.notExists(this.logPath))
            {
                Files.createDirectories(this.logPath.getParent());
                Files.createFile(this.logPath);
            }
            if (append) this.writer = new PrintWriter(Files.newBufferedWriter(this.logPath, StandardCharsets.UTF_8, StandardOpenOption.APPEND));
            else this.writer = new PrintWriter(Files.newBufferedWriter(this.logPath, StandardCharsets.UTF_8));
            Runtime.getRuntime().addShutdownHook(new Thread(this.writer::close));
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public Logger(String prefix, Path logFile)
    {
        this(prefix, logFile, false);
    }

    public void message(boolean err, String toWrite)
    {
        final String date = String.format("[%s] ", SIMPLE_DATE_FORMAT.format(new Date()));
        final String msg = date + this.prefix + (err ? "[ERROR]: " : "[INFO]: ") + toWrite;
        if (err) System.out.println(EnumLogColor.RED + msg + EnumLogColor.RESET);
        else System.out.println(msg);
        this.writeToTheLogFile(msg);
    }

    @Override
    public void infoColor(EnumLogColor color, String toWrite)
    {
        final String date = String.format("[%s] ", SIMPLE_DATE_FORMAT.format(new Date()));
        final String msg = date + this.prefix + "[INFO]: " + toWrite;
        final String coloredMessage = color + msg + EnumLogColor.RESET;
        System.out.println(coloredMessage);
        this.writeToTheLogFile(msg);
    }

    @Override
    public void info(String message)
    {
        this.message(false, message);
    }
    
    @Override
    public void err(String message)
    {
        this.message(true, message);
    }

    @Override
    public void warn(String message)
    {
        final String date = String.format("[%s] ", SIMPLE_DATE_FORMAT.format(new Date()));
        final String msg = date + this.prefix + "[WARN]: " + message;
        final String coloredWarn = EnumLogColor.YELLOW + msg + EnumLogColor.RESET;
        System.out.println(coloredWarn);
        this.writeToTheLogFile(msg);
    }
    
    @Override
    public void debug(String message)
    {
        final String date = String.format("[%s] ", SIMPLE_DATE_FORMAT.format(new Date()));
        final String msg = date + this.prefix + "[DEBUG]: " + message;
        final String coloredMessage = EnumLogColor.CYAN + msg + EnumLogColor.RESET;
        System.out.println(coloredMessage);
        this.writeToTheLogFile(msg);
    }

    @Override
    public void writeToTheLogFile(String toLog)
    {
        if(this.logPath == null)
            return;

        try
        {
            if (Files.notExists(this.logPath))
            {
                Files.createDirectories(this.logPath.getParent());
                Files.createFile(this.logPath);
            }

            if (this.writer != null)
            {
                this.writer.println(toLog);
                this.writer.flush();
            }
        } catch (IOException e)
        {
            this.printStackTrace(e);
        }
    }

    @Override
    public void printStackTrace(Throwable error)
    {
        this.printStackTrace0(error, 0);
    }

    private void printStackTrace0(Throwable error, int n)
    {
        if(n > 10)
        {
            System.out.println(EnumLogColor.RED + "..." + EnumLogColor.RESET);
            return;
        }

        if(n == 0)
            this.err(error.toString());
        else
        {
            final String toPrint = "Caused by: ";
            System.out.println(EnumLogColor.RED + toPrint + error.toString() + EnumLogColor.RESET);
            this.writeToTheLogFile(toPrint);
        }

        for (StackTraceElement trace : error.getStackTrace())
        {
            final String toPrint = "\tat " + trace.toString();
            System.out.println(EnumLogColor.RED + toPrint + EnumLogColor.RESET);
            this.writeToTheLogFile(toPrint);
        }

        if (error.getCause() != null)
            this.printStackTrace0(error.getCause(), n + 1);
    }

    @Deprecated
    @Override
    public void printStackTrace(String errorName, Throwable error)
    {
        this.printStackTrace(error);
    }

    @Override
    public void close()
    {
        this.writer.close();
    }

    @Override
    public Path getLogPath()
    {
        return this.logPath;
    }

    @Override
    public void setLogPath(Path logPath)
    {
        this.logPath = logPath;
    }

    @Override
    public String getPrefix()
    {
        return this.prefix;
    }
}
