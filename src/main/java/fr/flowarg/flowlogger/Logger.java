package fr.flowarg.flowlogger;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger implements ILogger
{
    private final String prefix;
    private File logFile;
    private PrintWriter writer;

    public Logger(String prefix, File logFile, boolean append)
    {
        this.prefix = prefix.endsWith(" ") ? prefix : prefix + " ";
        this.logFile = logFile;
        if(this.logFile != null)
        {
            try
            {
                if(!this.logFile.exists())
                {
                    this.logFile.getParentFile().mkdirs();
                    this.logFile.createNewFile();
                }
                if(append)
                    this.writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(this.logFile, true))));
                else this.writer = new PrintWriter(this.logFile);
                Runtime.getRuntime().addShutdownHook(new Thread(this.writer::close));
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public Logger(String prefix, File logFile)
    {
        this(prefix, logFile, false);
    }

    public void message(boolean err, String toWrite)
    {
        final String date = String.format("[%s] ", new SimpleDateFormat("hh:mm:ss").format(new Date()));
        final String msg = date + prefix + (err ? "[ERROR]: " : "[INFO]: ") + toWrite;
        if (err) System.out.printf("%s\n", EnumLogColor.RED + msg + EnumLogColor.RESET);
        else System.out.printf("%s\n", msg);
        this.writeToTheLogFile(msg);
    }

    @Override
    public void infoColor(EnumLogColor color, String toWrite)
    {
        final String date = String.format("[%s] ", new SimpleDateFormat("hh:mm:ss").format(new Date()));
        final String msg = date + prefix + "[INFO]: " + toWrite;
        final String coloredMessage = color + msg + EnumLogColor.RESET;
        System.out.printf("%s\n", coloredMessage);
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
        final String date = String.format("[%s] ", new SimpleDateFormat("hh:mm:ss").format(new Date()));
        final String msg = date + prefix + "[WARN]: " + message;
        final String coloredWarn = EnumLogColor.YELLOW + msg + EnumLogColor.RESET;
        System.out.printf("%s\n", coloredWarn);
        this.writeToTheLogFile(msg);
    }
    
    @Override
    public void debug(String message)
    {
        final String date = String.format("[%s] ", new SimpleDateFormat("hh:mm:ss").format(new Date()));
        final String msg = date + prefix + "[DEBUG]: " + message;
        final String coloredMessage = EnumLogColor.CYAN + msg + EnumLogColor.RESET;
        System.out.printf("%s\n", coloredMessage);
        this.writeToTheLogFile(msg);
    }

    @Override
    public void writeToTheLogFile(String toLog)
    {
        if(this.logFile != null)
        {
            try
            {
                if(!this.logFile.exists())
                {
                    this.logFile.getParentFile().mkdirs();
                    this.logFile.createNewFile();
                }

                if(this.writer != null)
                {
                    this.writer.printf("%s\n", toLog);
                    this.writer.flush();
                }
            } catch (IOException e)
            {
                this.printStackTrace(e);
            }
        }
    }

    @Override
    public void printStackTrace(Throwable cause)
    {
        this.printStackTrace("An error occurred : ", cause);
    }

    @Override
    public void printStackTrace(String errorName, Throwable cause)
    {
        this.err(errorName + cause.toString());
        for (StackTraceElement trace : cause.getStackTrace())
        {
            final String toPrint = "\tat " + trace.toString();
            this.writeToTheLogFile(toPrint);
            System.out.printf("%s\n", EnumLogColor.RED + toPrint + EnumLogColor.RESET);
        }
    }

    @Override
    public void close()
    {
        this.writer.close();
    }
    
    @Override
    public File getLogFile()
    {
        return this.logFile;
    }

    @Override
    public void setLogFile(File logFile)
    {
        this.logFile = logFile;
    }

    @Override
    public String getPrefix()
    {
        return this.prefix;
    }
}
