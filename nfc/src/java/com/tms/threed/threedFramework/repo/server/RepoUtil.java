package com.tms.threed.threedFramework.repo.server;

import com.google.common.io.Files;

import java.io.File;
import java.io.IOException;

public class RepoUtil {

    public static File createDirNotExists(File dirName) {
        if (!dirName.exists()) {
            try {
                Files.createParentDirs(dirName);
                dirName.mkdir();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return dirName;
    }
}
