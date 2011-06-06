package com.tms.threed.threedFramework.threedCore.server.config;

import com.tms.threed.threedFramework.util.lang.server.Sys;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

public class ConfigHelper {

    public static final String APP_NAME = "threed_framework";

    final private static String CONFIG_DIR_KEY = "configDir";
    final private static String DEFAULT_CONFIG_DIR_1 = "/temp/tmsConfig";
    final private static String DEFAULT_CONFIG_DIR_2 = Sys.getUserHome() + "/temp/tmsConfig";

    private static ReloadingConfig properties;

    public static Properties getAppProperties() {
        if (properties == null) {
            properties = new ReloadingConfig();
            properties.reload();
        }
        return properties;
    }

    public static String getProperty(String propertyName) {
        return getAppProperties().getProperty(propertyName);
    }

    public static File getConfigDir() {
        File configDir;

        configDir = configDirExists(System.getProperty(CONFIG_DIR_KEY));
        if (configDir != null) {
            return configDir;
        }

        configDir = configDirExists(DEFAULT_CONFIG_DIR_1);
        if (configDir != null) {
            return configDir;
        }

        configDir = configDirExists(DEFAULT_CONFIG_DIR_2);
        if (configDir != null) {
            return configDir;
        }


        throw new IllegalStateException("Could locate configDir. Tried: [System.getProperty(" + CONFIG_DIR_KEY + ")=" + System.getProperty(CONFIG_DIR_KEY) + "], " + DEFAULT_CONFIG_DIR_1 + ", " + DEFAULT_CONFIG_DIR_2);

    }

    private static File configDirExists(String configDir) {
        if (configDir == null) {
            return null;
        }
        File f = new File(configDir);
        if (f.exists()) {
            return f;
        } else {
            return null;
        }
    }

    public static File getLog4ConfigDir() {
        return new File(getConfigDir(), "log4j");
    }

    public static File getConfigFile() {
        File configDir = getConfigDir();
        File configFile = new File(configDir, APP_NAME + ".properties");
        if (!configFile.exists()) throw new IllegalStateException("ConfigFile[" + configFile + "] does not exist");
        return configFile;
    }

    public static File getLog4jConfigFile() {
        String localName = APP_NAME + "_log4j.properties";
        File f = new File(getLog4ConfigDir(), localName);
        return f;
    }

    public static class ReloadingConfig extends Properties {

        private static final long EXPIRE_TIME = 60 * 1000;
        private long lastReadTime;

        @Override
        public String getProperty(String key) {
            checkExpiry();
            return super.getProperty(key);
        }

        private void checkExpiry() {
            if (System.currentTimeMillis() - lastReadTime > EXPIRE_TIME) {
                reload();
            }
        }

        private void reload() {
            File configFile;
            try {
                configFile = getConfigFile();
            } catch (Exception e) {
                return;
            }


            if (!configFile.isFile())
                throw new IllegalStateException("Config file [" + configFile + "] is not a file.");

            if (!configFile.canRead())
                throw new IllegalStateException("Config file [" + configFile + "] is not readable.");

            Reader r = null;

            try {
                r = new FileReader(configFile);
                load(r);
                lastReadTime = System.currentTimeMillis();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                if (r != null) {
                    try {
                        r.close();
                    } catch (IOException ignore) {
                    }
                }
            }
        }
    }


    /**
     * <p/>
     * initializes the logger for the submitted application name
     * logfiles *must* be saved in the default location for
     * this application:  "${configDir}/log4j/{appName}{LOGGER_CONFIG_SUFFIX}"
     * (currently: _log4j.properties)
     * </p>
     *
     */
    public static void maybeInitLogger(String appName) {
        System.out.println("ConfigHelper.initLogger for app[" + appName + "]");
        File log4jFile = getLog4jConfigFile();
        System.out.println("Using log4jFile[" + log4jFile + "] for app[" + appName + "]");

        if (!log4jFile.exists()) {
            System.out.println("Log4j file[" + log4jFile + "] for app[" + appName + "]");
            return;
        }

        if (!log4jFile.canRead()) {
            System.out.println("Can't read log4j file[" + log4jFile + "] for app[" + appName + "]");
            return;
        }


        try {
            //set log4j configuration for this application using ${configDir}/appName/log4j/appName_log4j.properties

            PropertyConfigurator.configure(log4jFile.getAbsolutePath());
            System.out.println("initLogger complete for app[" + appName + "] using log4j file[" + log4jFile + "]");
        } catch (Exception e) {
            // if log4j.properties not found, use standard configuration
            System.err.println("Failed init logger for app[" + appName + "] using log4j file[" + log4jFile + "]");
            e.printStackTrace();
            BasicConfigurator.configure();
        }
    }

}
