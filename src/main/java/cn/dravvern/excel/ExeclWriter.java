package cn.dravvern.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

public class ExeclWriter {

    private static void WriteSheetHead(Sheet sh, Object[] head) {
        Row rowhead = sh.createRow(0);
        for (int cellnum = 0; cellnum < head.length; cellnum++) {
            Cell cell = rowhead.createCell(cellnum);
            setCell(cell, head[cellnum]);
        }
    }

    private static void WriteSheetBody(Sheet sh, List<List<Object[]>> lists) {
        int currRownum = 0;
        for (int i = 0; i < lists.size(); i++) {
            List<Object[]> list = lists.get(i);
            for (int rownum = 0; rownum < list.size(); rownum++) {
                Row row = sh.createRow(rownum + 1 + currRownum);
                for (int cellnum = 0; cellnum < list.get(rownum).length; cellnum++) {
                    Cell cell = row.createCell(cellnum);
                    Object object = list.get(rownum)[cellnum];
                    setCell(cell, object);
                }
            }
            currRownum = currRownum + list.size();
        }
    }

    public static void WriteSheet(File file, Object[] head, List<List<Object[]>> lists) throws IOException {
        SXSSFWorkbook wb = new SXSSFWorkbook(100);
        Sheet sh = wb.createSheet("result");
        WriteSheetHead(sh, head);
        WriteSheetBody(sh, lists);
        FileOutputStream out = new FileOutputStream(file);
        wb.write(out);
        out.close();

        wb.dispose();
    }

    private static void setCell(Cell cell, Object object) {
        if (object instanceof Integer) {
            cell.setCellValue((Integer) object);
        } else if (object instanceof BigDecimal) {
            BigDecimal bigDecimal = (BigDecimal) object;
            BigDecimal longDecimal = new BigDecimal(bigDecimal.longValue());
            if (bigDecimal.subtract(longDecimal).compareTo(BigDecimal.ZERO) != 0) {
                if (longDecimal.toString().length() <= 11) {
                    cell.setCellValue(bigDecimal.doubleValue());
                } else {
                    cell.setCellValue(bigDecimal.toString());
                }
            } else if (bigDecimal.toString().length() <= 11) {
                cell.setCellValue(bigDecimal.longValue());
            } else {
                cell.setCellValue(bigDecimal.toString());
            }
        } else if (object instanceof Timestamp) {
            DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            cell.setCellValue(sdf.format(object));
        } else if (object instanceof String) {
            cell.setCellValue((String) object);
        } else {
            cell.setCellValue((String) object);
        }
    }
}
