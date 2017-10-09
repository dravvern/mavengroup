package cn.dravvern.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipUtil {

    public static void ZipFiles(File zip, String path, File... srcFiles) throws IOException {
        ZipOutputStream out = new ZipOutputStream(new FileOutputStream(zip));
        ZipFiles(out, path, srcFiles);
        out.close();
        System.out.println("*****************压缩完毕*******************");
    }

    public static void ZipFiles(File zip, String path, String[] filenames) throws IOException {
        File[] files = new File[filenames.length];
        for (int i = 0; i < filenames.length; i++) {
            files[i] = new File(filenames[i]);
        }
        ZipFiles(zip, path, files);
    }

    private static void ZipFiles(ZipOutputStream out, String path, File... srcFiles) {
        path = path.replaceAll("\\*", "/");
        if (!path.endsWith("/") && path.length() > 0) {
            path += "/";
        }
        byte[] buf = new byte[1024];
        try {
            for (int i = 0; i < srcFiles.length; i++) {
                if (srcFiles[i].isDirectory()) {
                    File[] files = srcFiles[i].listFiles();
                    String srcPath = srcFiles[i].getName();
                    srcPath = srcPath.replaceAll("\\*", "/");
                    if (!srcPath.endsWith("/")) {
                        srcPath += "/";
                    }
                    out.putNextEntry(new ZipEntry(path + srcPath));
                    ZipFiles(out, path + srcPath, files);
                } else {
                    FileInputStream in = new FileInputStream(srcFiles[i]);
//                    System.out.println("test2:" + path + srcFiles[i].getName());
                    out.putNextEntry(new ZipEntry(path + srcFiles[i].getName()));
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    out.closeEntry();
                    in.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 解压到指定目录
     * 
     * @param zipfile
     * @param descdir
     * @author
     */
    public static List<String> unZipFiles(String zipfilename, String descdir) throws IOException {
        return unZipFiles(new File(zipfilename), descdir);
    }

    /**
     * 解压文件到指定目录
     * 
     * @param zipFile
     * @param descDir
     * @author
     */
    public static List<String> unZipFiles(File zipFile, String descdir) throws IOException {
        List<String> filenamelist = new ArrayList<String>();
        File pathFile = new File(descdir);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }
        Charset gbk = Charset.forName("GBK");

        ZipFile zip = new ZipFile(zipFile, gbk);
        for (Enumeration<?> entries = zip.entries(); entries.hasMoreElements();) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            String zipEntryName = entry.getName();
            InputStream in = zip.getInputStream(entry);
            String outfilename = (descdir + zipEntryName).replaceAll("\\*", "/");
            // 判断路径是否存在,不存在则创建文件路径
            File file = new File(outfilename.substring(0, outfilename.lastIndexOf('/')));
            if (!file.exists()) {
                file.mkdirs();
            }
            // 判断文件全路径是否为文件夹,如果是上面已经上传,不需要解压
            if (new File(outfilename).isDirectory()) {
                continue;
            }
            OutputStream out = new FileOutputStream(outfilename);
            byte[] buf1 = new byte[1024];
            int len;
            while ((len = in.read(buf1)) > 0) {
                out.write(buf1, 0, len);
            }
            in.close();
            out.close();
            filenamelist.add(outfilename);
        }
        zip.close();
        System.out.println("******************解压完毕********************");
        return filenamelist;
    }

}
