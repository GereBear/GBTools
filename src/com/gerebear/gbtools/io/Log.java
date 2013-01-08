package com.gerebear.gbtools.io;

/**
* Simple static elevation logger, supports ansi console color coding.
* Usage: @see log_settings
* 
* CC-BY-3.0 http://creativecommons.org/licenses/by/3.0/ 
* @author GereBear (Geremy R. Tilton 2013)
*/
public class Log 
{
    /**
     * A selection of colors (and their respective ansi) for use in logging methods.
     */
    public enum ansicode 
    {
        LIGHT_RED    ((char)27+"[1;31m"),
        LIGHT_GREEN  ((char)27+"[1;32m"), 
        YELLOW       ((char)27+"[1;33m"), 
        LIGHT_BLUE   ((char)27+"[1;34m"), 
        MAGENTA      ((char)27+"[1;35m"), 
        LIGHT_CYAN   ((char)27+"[1;36m"), 
        WHITE        ((char)27+"[1;37m"), 
        NORMAL       ((char)27+"[0m"   ), 
        RED          ((char)27+"[0;31m"), 
        GREEN        ((char)27+"[0;32m"), 
        BROWN        ((char)27+"[0;33m"), 
        BLUE         ((char)27+"[0;34m"), 
        CYAN         ((char)27+"[0;36m"), 
        BLACK        ((char)27+"[0;30m"),
        BOLD         ((char)27+"[1m"   ),
        UNDERSCORE   ((char)27+"[4m"   ), 
        REVERSE      ((char)27+"[7m"   ); 

        private String code;
        ansicode(String code) { this.code = code; }
    }
    
    private static boolean color_mode = false;
    private static int log_level = 0;
    
    /**
     * Sets the log level, Turns on/off using ansi color codes.
     * Should be called at program initialization. 
     * May be called at any time, will only effect future messsgs.
     * @param level lowest allowed level, 0 = Verbose Logging, 14 = Logging off.
     * @param color Use ansi color codes? (can appear odd in some consoles esp. windows)
     * @see Trace, Debug, Info, Warn, Error, Fatal, Attention, Raw
     */
    public static void log_settings(int level, boolean color) 
    {
        log_level = level;
        color_mode = color;
    }
    
    //writes the log to the console (could be expanded to add stream logging.)
    private static void log_out(Log.ansicode code, String msg)
    {
        if(color_mode){ System.out.println(code.code + msg + Log.ansicode.NORMAL.code); }
        else          { System.out.println(msg); }
    }
    
    /**
     * Brown Text.
     * if log level is 0 or less, this message will be logged.
     * @param msg the trace message.
     */
    public static void Trace(String msg)    {if(log_level <= 0)             {log_out(Log.ansicode.BROWN,      "[Trace] "+msg);}}
    
    /**
     * Light Blue Text.
     * if log level is 1 or less, this message will be logged.
     * @param msg the debug message.
     */
    public static void Debug(String msg)    {if(log_level <= 1)             {log_out(Log.ansicode.LIGHT_BLUE, "[Debug] "+msg);}}
    
    /**
     * Light Cyan text.
     * if log level is 3 or less, this message will be logged.
     * @param msg the information message.
     */
    public static void Info (String msg)    {if(log_level <= 3)             {log_out(Log.ansicode.LIGHT_CYAN, "[Info] "+msg);}}
    
    /**
     * Yellow text.
     * if log level is 8 or less, this message will be logged.
     * @param msg the warning message.
     */
    public static void Warn (String msg)    {if(log_level <= 8)             {log_out(Log.ansicode.YELLOW,     "[Warn] "+msg);}}
    
    /**
     * Light Red text.
     * if log level is 9 or less, this message will be logged.
     * @param msg the error message.
     */
    public static void Error(String msg)    {if(log_level <= 9)             {log_out(Log.ansicode.LIGHT_RED,  "[Error] "+msg);}}
    
    /**
     * Deep Red text.
     * if log level is 10 or less, this message will be logged.
     * @param msg the fatal error message.
     */
    public static void Fatal(String msg)    {if(log_level <= 10)            {log_out(Log.ansicode.RED,        "[Fatal] "+msg);}}
    
    /**
     * Light Green Background, stands out from other text.
     * if log level is 11 or less, this message will be logged.
     * @param msg the attention message.
     */
    public static void Attention(String msg){if(log_level <= 11)            {log_out(Log.ansicode.REVERSE, Log.ansicode.LIGHT_GREEN.code+"[!!!!!] "+msg);}}
    
    /**
     * Allows for custom level and color selection
     * @param msg your log message.
     * @param color Log.ansicode color selection (ignored if color_mode setting is false)
     * @param level The level of escalation for this message (entire message is ignored if current log_level > level of message.
     */
    public static void Raw(String msg, Log.ansicode color, int level) {if(log_level <= level) {log_out(color, msg);}}
    
}
