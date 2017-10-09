package cn.dravvern.excel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.csvreader.CsvReader;

public class ExeclCsvReader {
    private List<List<Object[]>> listSheet = new ArrayList<List<Object[]>>();
    private List<Object[]> list = null;

    private List<Object[]> heads = new ArrayList<Object[]>();

    private List<String> sheetNames = new ArrayList<String>();
    
    private int sheetNo = 1;
    
    public List<Object[]> getHeads() {
        return heads;
    }
    
    public List<List<Object[]>> getListSheet() {
        return listSheet;
    }

    public List<String> getSheetNames() {
        return sheetNames;
    }

    public int getSheetNo() {
        return sheetNo;
    }
    
    public void process(File file) throws IOException {
        CsvReader reader = null;
        list = new ArrayList<Object[]>();
        reader = new CsvReader(new FileInputStream(file), ',', Charset.forName("GBK"));
        int i = 0;
        while (reader.readRecord()) {
            if (i == 0) {
                heads.add(reader.getValues());
            } else {
                list.add(reader.getValues());
            }
            i++;
        }
        listSheet.add(list);
        sheetNo = 1;

        String getFilename = file.getName();
        sheetNames.add(getFilename.substring(0, getFilename.lastIndexOf(".")));
        reader.close();
    }
    
}
