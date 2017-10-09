package cn.dravvern.excel;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtil {

    // excel2003扩展名
    public static final String EXCEL03_EXTENSION = ".XLS";
    // excel2007扩展名
    public static final String EXCEL07_EXTENSION = ".XLSX";
    // CSV 扩展名 
    public static final String CSV_EXTENSION = ".CSV";
    
    private List<List<Object[]>> listSheet = new ArrayList<List<Object[]>>();
    private List<Object[]> heads = new ArrayList<Object[]>();
    private List<String> sheetNames  = new ArrayList<String>();
    private int sheetNo;
    private boolean loaded = false;
    
    private void readExeclContent(String fileName) throws Exception {
        File file = new File(fileName);
        readExeclContent(file);
    }
    
    private void readExeclContent(File file) throws Exception {
        if (file.getName().toUpperCase().endsWith(EXCEL03_EXTENSION)) {
            Excel2003Reader excelReader2003 = new Excel2003Reader();
            excelReader2003.process(file);
            this.listSheet = excelReader2003.getListSheet();
            this.heads = excelReader2003.getHeads();
            this.sheetNames = excelReader2003.getSheetNames();
            this.sheetNo = excelReader2003.getSheetNo();
        } else if (file.getName().toUpperCase().endsWith(EXCEL07_EXTENSION)) {
            Excel2007Reader excelReader2007 = new Excel2007Reader();
            excelReader2007.process(file);
            this.listSheet = excelReader2007.getListSheet();
            this.heads = excelReader2007.getHeads();
            this.sheetNames = excelReader2007.getSheetNames();
            this.sheetNo = excelReader2007.getSheetNo();
        } else if (file.getName().toUpperCase().endsWith(CSV_EXTENSION)) {
            ExeclCsvReader execlCsvReader = new ExeclCsvReader();
            execlCsvReader.process(file);
            this.listSheet = execlCsvReader.getListSheet();
            this.heads = execlCsvReader.getHeads();
            this.sheetNames = execlCsvReader.getSheetNames();
            this.sheetNo = execlCsvReader.getSheetNo();
        } else {
            throw new IOException("文件格式错误,fileName的扩展名只能是xls或xlsx或csv.");
        }
        loaded = true;
    }
    
    public String getSheetName(File file) throws Exception {
        return getSheetName(file, 0);
    }

    public String getSheetName(File file, int sheetNum) throws Exception {
        if (!loaded) {
            readExeclContent(file);
        }
        if (sheetNames.size() > sheetNum) {
            return sheetNames.get(sheetNum);
        } else {
            return null;
        }
    }
    
    public String getSheetName(String fileName) throws Exception {
        return getSheetName(fileName, 0);
    }

    public String getSheetName(String fileName, int sheetNum) throws Exception {
        if (!loaded) {
            readExeclContent(fileName);
        }
        if (sheetNames.size() > sheetNum) {
            return sheetNames.get(sheetNum);
        } else {
            return null;
        }
    }
    
    public List<Object[]> getSheetList(String fileName) throws Exception {
        return getSheetList(fileName, 0);
    }
    
    public List<Object[]> getSheetList(String fileName, int sheetNum) throws Exception {
        if (!loaded) {
            readExeclContent(fileName);
        }
        if (listSheet.size() > sheetNum) {
            return listSheet.get(sheetNum);
        } else {
            return null;
        }
    }
    
    public List<Object[]> getSheetList(File file) throws Exception {
        return getSheetList(file, 0);
    }
    
    public List<Object[]> getSheetList(File file, int sheetNum) throws Exception {
        if (!loaded) {
            readExeclContent(file);
        }
        if (listSheet.size() > sheetNum) {
            return listSheet.get(sheetNum);
        } else {
            return null;
        }
    }
    
    public Object[] getSheetHead(String fileName) throws Exception {
        return getSheetHead(fileName, 0);
    }
    
    public Object[] getSheetHead(String fileName, int sheetNum) throws Exception {
        if (!loaded) {
            readExeclContent(fileName);
        }
        if (heads.size() > sheetNum) {
            return heads.get(sheetNum);
        } else {
            return null;
        }
    }
    
    public Object[] getSheetHead(File file) throws Exception {
        return getSheetHead(file, 0);
    }
    
    public Object[] getSheetHead(File file, int sheetNum) throws Exception {
        if (!loaded) {
            readExeclContent(file);
        }
        if (heads.size() > sheetNum) {
            return heads.get(sheetNum);
        } else {
            return null;
        }
    }
    
    public int getSheetNumber(String fileName) throws Exception {
        if (!loaded) {
            readExeclContent(fileName);
        }
        return sheetNo;
    }
    
    public int getSheetNumber(File file) throws Exception {
        if (!loaded) {
            readExeclContent(file);
        }
        return sheetNo;
    }
}
