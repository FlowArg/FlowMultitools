package fr.flowarg.flowlogger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger implements ILogger
{
    private final String prefix;
    private final File logFile;

    public Logger(String prefix, File logFile)
    {
        this.prefix = prefix.endsWith(" ") ? prefix : prefix + " ";
        this.logFile = logFile;
    }

    private void message(boolean err, String toWrite)
    {
    	final String date = String.format("[%s] ", new SimpleDateFormat("hh:mm:ss").format(new Date()));
    	final String msg = new StringBuilder().append(date)
    			.append(prefix)
    			.append(err ? "[ERROR] " : "[INFO] ")
    			.append(toWrite)
    			.toString();
        if (err) System.err.println(msg);
        else System.out.println(msg);

        this.writeToTheLogFile(msg);
    }

    public void infoColor(EnumLogColor color, String toWrite)
    {
    	final String date = String.format("[%s] ", new SimpleDateFormat("hh:mm:ss").format(new Date()));
    	final String msg = new StringBuilder()
    			.append(color.getColor())
    			.append(date)
    			.append(prefix)
    			.append("[INFO] ")
    			.append(toWrite)
    			.append(EnumLogColor.RESET.getColor())
    			.toString();
        System.out.println(msg);
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
    	final String warn = new StringBuilder()
    			.append(EnumLogColor.YELLOW.getColor())
    			.append(date)
    			.append(prefix)
    			.append("[WARN] ")
    			.append(message)
    			.append(EnumLogColor.RESET.getColor())
    			.toString();
        System.out.println(warn);
        this.writeToTheLogFile(warn);
    }

    private void writeToTheLogFile(String toLog)
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
                final BufferedReader reader = new BufferedReader(new FileReader(this.logFile));
                final StringBuilder text = new StringBuilder();

                String line;

                while ((line = reader.readLine()) != null)
                {
                    text.append(line + "\n");
                }
                reader.close();

                final String toWrite = text.toString() + toLog;
                final BufferedWriter writer = new BufferedWriter(new FileWriter(this.logFile));

                writer.write(toWrite);
                writer.flush();
                writer.close();
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
            System.err.println(toPrint);
        }
    }
    
    @Override
    public File getLogFile()
    {
        return this.logFile;
    }
    
    @Override
    public String getPrefix()
    {
		return this.prefix;
	}

    public enum EnumLogColor
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

        private String color;

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
