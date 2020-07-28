package fr.flowarg.flowlogger;

import java.io.File;

public interface ILogger
{
	void err(String message);
	void info(String message);
	void warn(String message);
	void printStackTrace(String errorName, Throwable cause);
	void printStackTrace(Throwable cause);
	File getLogFile();
	String getPrefix();
}
