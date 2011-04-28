package com.tms.threed.repoServlets.web;

import com.google.common.io.Files;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPOutputStream;

public class GzipTest {

    public static void main(String[] args) throws IOException {

        File fIn = new File("/Users/dford/Desktop/jsonStats/tundra-raw-json-7.txt");
        byte[] bytes = Files.toByteArray(fIn);


        File fOut = new File("/Users/dford/Desktop/jsonStats/tundra-raw-json-7.gzip");

        GZIPOutputStream out = new GZIPOutputStream(new FileOutputStream(fOut));

        out.write(bytes);

        out.close();


    }
}
