package cn.dravvern.test;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

import cn.dravvern.excel.ExcelUtil;

public class ExcelReadExample {
    
    public static void printList(List<String[]> list) {
        System.out.println(list.size());
        for (int i = 0, size = list.size(); i < size; i++) {
            System.out.print("row" + i + "|");
            String[] str = list.get(i);
            for (int j = 0, length = str.length; j < length; j++) {
                System.out.print(str[j] + ", ");
            }
            System.out.println("");
        }
    }
    
    public static void printobj(Object[] str) {
        if (str == null) {
            System.out.println("null");
            return;
        }
        for (int i = 0; i < str.length; i++) {
            System.out.print(str[i] + ",");
        }
        System.out.println("");
    }
    
    public static void checkObject(Object object) {
        if (object instanceof Integer) {
            System.out.println("Integer" + object);
        } else if (object instanceof BigDecimal) {
            System.out.println("BigDecimal" + object);
        } else if (object instanceof Timestamp) {
            System.out.println("Timestamp" + object);
        } else if (object instanceof String) {
            System.out.println("String" + object);
        } else {
            System.out.println("BigDecimal" + object);
        }
    }
    
    public static void printObjectList(List<Object[]> list) {
        if(list == null || list.size() == 0) {
            System.out.println("list 为空");
            return ;
        }
        for (int i = 0;  i < list.size(); i++) {
            System.out.print("row" + i + "|");
            Object[] str = list.get(i);
            for (int j = 0; j < str.length; j++) {
                System.out.print(str[j] + ", ");
//                checkObject(str[j]);
            }
            System.out.println("");
        }
    }

    public static void main(String[] arg) {
        String fileName = "C:\\Users\\dravvern\\Documents\\46904.xls";
//        fileName = "C:\\Users\\dravvern\\Documents\\a.xlsx";
//        fileName = "f:/tmpfile/第1部分1000020170728655.csv";
        
        try {
            ExcelUtil excelUtil = new ExcelUtil();
            List<Object[]> list = excelUtil.getSheetList(fileName);
            printObjectList(list);
            Object[] str = excelUtil.getSheetHead(fileName, 0);
            printobj(str);
            String sheetName = excelUtil.getSheetName(fileName, 0);
            System.out.println(sheetName);
            int sheetNumber = excelUtil.getSheetNumber(fileName);
            System.out.println(sheetNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
//        File zipFile = new File("C:/Users/dravvern/Documents/20170919165902_集团业绩报表.zip");
//        File targetFile = new File("C:/Users/dravvern/Documents/1.xlsx");
//        FileMergeThread mfThread = new FileMergeThread(zipFile, targetFile);
//        mfThread.start();
        
    }
}
