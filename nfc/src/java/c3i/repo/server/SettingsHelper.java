package c3i.repo.server;

import com.google.common.io.Closeables;
import c3i.repo.shared.Settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SettingsHelper {

    public static final String LOCAL_FILE_NAME = ".config.ser";


    private final File configFile;

    public SettingsHelper(File repoBaseDir) {
        configFile = new File(repoBaseDir, LOCAL_FILE_NAME);
    }

    public Settings read() {
        System.out.println("SettingsHelper.read");
        if (!configFile.exists()) {
            Settings settings = Settings.createDefault();
            System.out.println("settings = " + settings);
            return settings;
        }

        FileInputStream is = null;
        ObjectInputStream ois = null;

        try {
            is = new FileInputStream(configFile);
            ois = new ObjectInputStream(is);
            Settings settings = (Settings) ois.readObject();
            System.out.println("Settings: " + settings);
            return settings;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            Closeables.closeQuietly(is);
            Closeables.closeQuietly(ois);
        }

    }

    public void save(Settings repoConfig) {
        FileOutputStream os = null;
        ObjectOutputStream oos = null;

        try {
            os = new FileOutputStream(configFile);
            oos = new ObjectOutputStream(os);
            oos.writeObject(repoConfig);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            Closeables.closeQuietly(os);
            Closeables.closeQuietly(oos);
        }
    }



}
