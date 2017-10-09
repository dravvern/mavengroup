package cn.dravvern.test;


import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public class TestExeclWrite2 {

    public static void main(String[] args) throws IOException {
        long ct1 = System.currentTimeMillis();
        SXSSFWorkbook wb = new SXSSFWorkbook(100); // keep 100 rows in memory, exceeding rows will be flushed to disk
        Sheet sh = wb.createSheet();
        
        
        for(int rownum = 0; rownum < 100000; rownum++){
            Row row = sh.createRow(rownum);
            for(int cellnum = 0; cellnum < 18; cellnum++){
                Cell cell = row.createCell(cellnum);
                String address = new CellReference(cell).formatAsString();
                cell.setCellValue(address);
            }

        }
        long ct2 = System.currentTimeMillis();
        
        FileOutputStream out = new FileOutputStream("C:\\Users\\dravvern\\Documents/sxssf.xlsx");
        wb.write(out);
        out.close();

        // dispose of temporary files backing this workbook on disk
        wb.dispose();
        long ct3 = System.currentTimeMillis();
        System.out.println("***完毕*******************用时：" +((ct2-ct1)/1000.0) + "秒" +((ct3-ct2)/1000.0) + "秒");
        
    }


}
