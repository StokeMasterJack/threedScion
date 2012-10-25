package c3i.repo.server;

import c3i.core.common.shared.BrandKey;
import c3i.repo.shared.Settings;
import com.google.common.base.Preconditions;
import com.google.common.io.Closeables;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Map;

public class SettingsHelper {

    public static final String LOCAL_FILE_NAME = ".config.ser";


    private final Map<BrandKey, File> repoBaseDirMap;

    public SettingsHelper(final Map<BrandKey, File> repoBaseDirMap) {
        Preconditions.checkNotNull(repoBaseDirMap);
        this.repoBaseDirMap = repoBaseDirMap;
    }

    public Settings read(BrandKey brandKey) {
        File repoBaseDir = repoBaseDirMap.get(brandKey);
        File configFile = new File(repoBaseDir, LOCAL_FILE_NAME);
        if (!configFile.exists()) {
            Settings settings = Settings.createDefault();
            return settings;
        }

        FileInputStream is = null;
        ObjectInputStream ois = null;

        try {
            is = new FileInputStream(configFile);
            ois = new ObjectInputStream(is);
            Settings settings = (Settings) ois.readObject();
            return settings;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } finally {
            Closeables.closeQuietly(is);
            Closeables.closeQuietly(ois);
        }

    }

    public void save(BrandKey brandKey, Settings repoConfig) {
        File repoBaseDir = repoBaseDirMap.get(brandKey);
        File configFile = new File(repoBaseDir, LOCAL_FILE_NAME);

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
