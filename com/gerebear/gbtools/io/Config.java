package com.gerebear.gbtools.io;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
* Configuration Automation and Caching wrapper for Properties.
* Usage: Call Properties myConfigProp = getConfig("myconfig"); and use the returned Properties class.
* Usage: Call setConfig(myConfigProp,  "myconfig"); and it will be saved.
* 
* This class is (to my knowledge) ThreadSafe, and uses Caching to reduce disk i/o;
* 
* CC-BY-3.0 http://creativecommons.org/licenses/by/3.0/ 
* @author GereBear (Geremy R. Tilton 2013)
*/
public class Config
{
    static final String config_dir = "./config"; // the directory for config files. (change to your needs)
    static final String config_ext = ".cfg";	 // the file ending. (change to your needs)
    
	static ConcurrentHashMap<String, Properties> propCache = null;   
    static boolean _inited = false;
    
    
    /************************************************/
    /*******/ static { initConfigDir(); } /**********/
    /******* no matter what you call, it will work!**/
    /************************************************/
    
              
    static void initConfigDir()
    {
		if(propCache == null) { propCache = new ConcurrentHashMap<>(8,.9f,1); }
        try 
        {
            Files.createDirectories(Paths.get(config_dir));
            _inited = true;
        }   catch(IOException IOE0) 
        { 
            Log.Warn("Could not create/open "+config_dir.toString()+"."); 
            IOE0.printStackTrace();
            _inited = false;
        }
    }
         
    /**
     * Retrieves the requested config from cache (or if not cached, from ./config)
     * @param configName Name of the target config.
     * @return either the Properties file requested from cache or disk, or null if the file does not exist (or an error occurs).
     */
    public static Properties getConfig(String configName)
    {
        if(!_inited){initConfigDir();}
        
        if(configName == null) 
        {   Log.Warn("trying to get a config without supplying a name, a new instance of properties was returned.");
            return new Properties();
        }
        if(propCache.containsKey(configName))
        {
            return (Properties)propCache.get(configName).clone();
        }
        Properties props = new Properties();
        
        Path cfgfile = Paths.get(config_dir+"/"+configName + config_ext);
        Log.Trace(configName+"\n"+cfgfile.toString());
        if(Files.exists(cfgfile))
        {
        
            try (FileInputStream fis = new FileInputStream(cfgfile.toString()))
            {
                props.load(fis);
                Log.Trace(cfgfile.toString() + " loaded.");
            }   catch (IOException IOE0)
            { 
                Log.Warn(cfgfile.toString() + " exists but will not open or is currupt, delete for a fresh copy.");
                IOE0.printStackTrace();
            }   
        }
        return props;    
    }
    
    
    /**
     * Cache and Save a property file for later use.
     * @param prop The property file you want to cache and save.
     * @param configName The name you want this property file saved as "blah" would become blah.cfg, under default circumstances.
     * @return true on success. false on failure.
     */
    public static boolean setConfig(Properties prop, String configName)
    {
        if(configName == null || configName.length() < 1) { Log.Warn("config name was not passed... nothing saved."); return false; }
        if(prop == null) { Log.Warn("No properties file was passed... nothing saved."); return false; }
        if(prop.isEmpty()) { Log.Warn("Empty properties file was passed... nothing saved."); return false; }
        
        //if we have gotten this far, we have a properties file with at least one entry, and a configname..
        Path cfgfile = Paths.get(config_dir+"/"+configName + config_ext);           
        try(FileOutputStream fos = new FileOutputStream(cfgfile.toString()))
        {
            
            if(propCache.containsKey(configName))
            {                
                Properties oldprops = propCache.get(configName);
                propCache.remove(configName, oldprops);
                oldprops.clear();
                
            }            
            propCache.putIfAbsent(configName, prop);
            prop.store(fos, null);
            Log.Trace(cfgfile.toString() + " saved.");
            return true;
        } catch (IOException IOE0)
        {
            Log.Warn(cfgfile.toString() + " can not be created or written to... do you have permissions?");
        }
        return false;
    }
}
