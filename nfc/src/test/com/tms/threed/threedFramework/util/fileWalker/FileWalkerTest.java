package com.tms.threed.threedFramework.util.fileWalker;

import junit.framework.TestCase;

import java.io.File;
import java.io.IOException;

public class FileWalkerTest extends TestCase {

    int count;

    public void test() throws Exception {

        File startDir = new File("/Users/dford/p-java/apache-tomcat-6.0.10/webapps/threed/jpgs/2010/venza/interior");
        FileWalker fileWalker = new FileWalker(startDir);

        fileWalker.setDirProcessor(new DefaultDirProcessor(fileWalker) {
            @Override public void processDir(File dir) {
                for (File child : dir.listFiles()) {
                    if(child.isDirectory() && child.getName().equals("LA")) {
                        try {
                            String oldName = child.getCanonicalPath();
                            String newName = oldName.replace("/LA","/Leather");
//                            child.renameTo(new File(newName));
                            System.out.println("oldName = [" + oldName + "]");
//                            System.out.println("newName = [" + newName + "]");
//                            System.out.println();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        count++;
                    }
                    else walker.processNode(child);
                }
            }
        });


        fileWalker.start();


        System.out.println(count);
    }
}
