package cn.dravvern.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.csvreader.CsvReader;


/**
 * javacsv是国外开发的一个比较好的操作csv文件的API，这里简单讲一下用法。
 * 先下载javacsv2.0.zip的文件，解压后，把javacsv.jar 添加到项目中。 本站下载地址：
 * http://www.cnitblog.com/Files/rd416/javacsv2.0.zip 官方下载地址：
 * http://sourceforge.net/project/showfiles.php?group_id=33066
 */

public class CsvUtil {
    
    public static List<String[]> getListFromCsv(String filename) throws IOException {
        List<String[]> csvlist = new ArrayList<String[]>();
        CsvReader reader = new CsvReader(filename, ',', Charset.forName("GBK"));
        reader.readHeaders(); // 跳过表头

//        if (filename.indexOf("第1部分") < 0) {
//            reader.readRecord();
//        }
        while (reader.readRecord()) {
            csvlist.add(reader.getValues());
        }
        reader.close();
        return csvlist;
    }

//    public static void setListToCsv(String filename, List<String[]> list) throws IOException {
//        CsvWriter writer = new CsvWriter(filename, ',', Charset.forName("GBK"));
//
//        for (int i = 0; i < list.size(); i++) {
//            writer.writeRecord(list.get(i));
//        }
//        writer.close();
//    }
}

