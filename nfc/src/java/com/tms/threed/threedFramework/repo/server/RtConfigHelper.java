package com.tms.threed.threedFramework.repo.server;

import com.google.common.io.Closeables;
import com.tms.threed.threedFramework.repo.shared.RtConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class RtConfigHelper {

    public static final String LOCAL_FILE_NAME = ".config.ser";


    private final File configFile;

    public RtConfigHelper(File repoBaseDir) {
        configFile = new File(repoBaseDir, LOCAL_FILE_NAME);
    }

    public RtConfig read() {
        if (!configFile.exists()) {
            return RtConfig.createDefault();
        }

        FileInputStream is = null;
        ObjectInputStream ois = null;

        try {
            is = new FileInputStream(configFile);
            ois = new ObjectInputStream(is);
            return (RtConfig) ois.readObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            Closeables.closeQuietly(is);
            Closeables.closeQuietly(ois);
        }

    }

    public void save(RtConfig repoConfig) {
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
