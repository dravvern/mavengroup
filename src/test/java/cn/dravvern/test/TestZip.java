package cn.dravvern.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.dravvern.util.ZipUtil;

public class TestZip {

    public static void main(String[] args) {
        
        String[] filenames = new String[3];
        filenames[0] = "e:/aa";
        filenames[1] = "e:/download.xls";
        filenames[2] = "e:/bank-import.xls";
        
        File zip = new File("e:/压缩.zip");
        // File zipFile = new File("e:/压缩.zip");
        String zipFilename = "e:/压缩.zip";
        String path = "e:/zipfile/";
        List<String> filenamelist = new ArrayList<String>();
        try {
            
            ZipUtil.ZipFiles(zip, "", filenames);
            
            filenamelist = ZipUtil.unZipFiles(zipFilename, path);
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (int i = 0; i < filenamelist.size(); i++) {
            System.out.println(filenamelist.get(i));
        }

    }

}
