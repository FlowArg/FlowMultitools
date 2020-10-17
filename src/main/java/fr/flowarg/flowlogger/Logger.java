package fr.flowarg.flowlogger;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger implements ILogger
{
    private final String prefix;
    private File logFile;
    private PrintWriter writer;

    public Logger(String prefix, File logFile)
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
                this.writer = new PrintWriter(this.logFile);
                Runtime.getRuntime().addShutdownHook(new Thread(this.writer::close));
            } catch (IOException e)
            {
                throw new RuntimeException(e);
            }
        }
    }

    public void message(boolean err, String toWrite)
    {
        final String date = String.format("[%s] ", new SimpleDateFormat("hh:mm:ss").format(new Date()));
        final String msg = date + prefix + (err ? "[ERROR] " : "[INFO] ") + toWrite;
        if (err) System.out.println(EnumLogColor.RED.getColor() + msg + EnumLogColor.RESET.getColor());
        else System.out.println(msg);

        this.writeToTheLogFile(msg);
    }

    @Override
    public void infoColor(EnumLogColor color, String toWrite)
    {
        final String date = String.format("[%s] ", new SimpleDateFormat("hh:mm:ss").format(new Date()));
        final String msg = date + prefix + "[INFO] " + toWrite;
        final String coloredMessage = color.getColor() + msg + EnumLogColor.RESET.getColor();
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
        final String date = String.format("[%s] ", new SimpleDateFormat("hh:mm:ss").format(new Date()));
        final String msg = date + prefix + "[WARN] " + message;
        final String coloredWarn = EnumLogColor.YELLOW.getColor() + msg + EnumLogColor.RESET.getColor();
        System.out.println(coloredWarn);
        this.writeToTheLogFile(msg);
    }
    
    @Override
    public void debug(String message)
    {
        final String date = String.format("[%s] ", new SimpleDateFormat("hh:mm:ss").format(new Date()));
        final String msg = date + prefix + "[DEBUG] " + message;
        final String coloredMessage = EnumLogColor.CYAN.getColor() + msg + EnumLogColor.RESET.getColor();
        System.out.println(coloredMessage);
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
                    this.writer.println(toLog);
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
        this.printStackTrace("An error as occurred : ", cause);
    }

    @Override
    public void printStackTrace(String errorName, Throwable cause)
    {
        this.err(errorName + cause.toString());
        for (StackTraceElement trace : cause.getStackTrace())
        {
            final String toPrint = "\tat " + trace.toString();
            this.writeToTheLogFile(toPrint);
            System.out.println(EnumLogColor.RED.getColor() + toPrint + EnumLogColor.RESET.getColor());
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
