package cn.dravvern.base;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.swing.JOptionPane;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;

import cn.dravvern.panel.RosterCircularPanel;

public class ExportNLInfo {

    public static void setFirstSheetStyle(HSSFWorkbook wkb, HSSFSheet sheet, int rowsn, int column) {
        HSSFCellStyle cellstylehead = createHeadCellStyle(wkb);
        HSSFCellStyle cellstylebody = createBodyCellStyle(wkb);
        HSSFCellStyle cellstyleperc = createPercentCellStyle(wkb);

        for (int x = 0; x < rowsn; x++) {
            sheet.createRow(x);
            for (int y = 0; y < column; y++) {
                sheet.getRow(x).createCell(y);
                if (x <= 1) {
                    sheet.getRow(x).getCell(y).setCellStyle(cellstylehead);
                } else if (y == 6 || y == 9 || y == 16 || y == 19) {
                    sheet.getRow(x).getCell(y).setCellStyle(cellstyleperc);
                } else {
                    sheet.getRow(x).getCell(y).setCellStyle(cellstylebody);
                }
            }
        }
    }

    private static HSSFCellStyle createCellStyle(HSSFWorkbook wkb) {
        HSSFCellStyle cellStyle = wkb.createCellStyle();
        cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); // 下边框
        cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);// 左边框
        cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);// 上边框
        cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);// 右边框
        HSSFFont font = wkb.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short) 11);
        cellStyle.setFont(font);
        cellStyle.setWrapText(true);
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直居中
        return cellStyle;
    }

    private static HSSFCellStyle createHeadCellStyle(HSSFWorkbook wkb) {
        HSSFCellStyle cellStyle = createCellStyle(wkb);
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
        return cellStyle;
    }

    private static HSSFCellStyle createBodyCellStyle(HSSFWorkbook wkb) {
        HSSFCellStyle cellStyle = createCellStyle(wkb);
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_RIGHT); // 居中
        return cellStyle;
    }

    private static HSSFCellStyle createPercentCellStyle(HSSFWorkbook wkb) {
        HSSFCellStyle cellStyle = createCellStyle(wkb);
        cellStyle.setDataFormat(HSSFDataFormat.getBuiltinFormat("0%"));
        return cellStyle;
    }

    private static void setWidth(HSSFSheet sheet, int length) {
        for (int i = 0; i < length; i++) {
            sheet.setColumnWidth(i, (int) ((7 + 0.72) * 256));
        }
    }

    private static void writeFirstSheet(HSSFWorkbook wkb) throws IOException {
        HSSFSheet sheet = wkb.createSheet("名单数量情况");
        setFirstSheetStyle(wkb, sheet, 21, 21);

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 10));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 11, 20));
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 0));

        HSSFRow rowhead1 = sheet.getRow(0);
        rowhead1.getCell(0).setCellValue("市县");
        rowhead1.getCell(1).setCellValue("大客名单");
        rowhead1.getCell(11).setCellValue("商企名单");

        HSSFRow rowhead2 = sheet.getRow(1);
        rowhead2.getCell(1).setCellValue("存量名单");
        rowhead2.getCell(2).setCellValue("环比");
        rowhead2.getCell(3).setCellValue("潜在名单数量");
        rowhead2.getCell(4).setCellValue("环比");
        rowhead2.getCell(5).setCellValue("已分配的存量名单数");
        rowhead2.getCell(6).setCellValue("占比");
        rowhead2.getCell(7).setCellValue("环比");
        rowhead2.getCell(8).setCellValue("已分配的潜在名单数");
        rowhead2.getCell(9).setCellValue("占比");
        rowhead2.getCell(10).setCellValue("环比");

        rowhead2.getCell(11).setCellValue("存量名单数量");
        rowhead2.getCell(12).setCellValue("环比");
        rowhead2.getCell(13).setCellValue("潜在名单数量");
        rowhead2.getCell(14).setCellValue("环比");
        rowhead2.getCell(15).setCellValue("已分配的存量名单数");
        rowhead2.getCell(16).setCellValue("占比");
        rowhead2.getCell(17).setCellValue("环比");
        rowhead2.getCell(18).setCellValue("已分配的潜在名单数");
        rowhead2.getCell(19).setCellValue("占比");
        rowhead2.getCell(20).setCellValue("环比");
        setWidth(sheet, 21);
        rowhead2.setHeight((short) (15.625 * 70));

        // String sql = "select c17, sum(case when cust_type='大客' and cust_status='存量'
        // then 1 else 0 end) a1, "
        // + "sum(case when cust_type='大客' and cust_status='潜在' then 1 else 0 end) a2, "
        // + "sum(case when cust_type='大客' and cust_status='存量' and x.respon_name is not
        // null then 1 else 0 end) a3, "
        // + "sum(case when cust_type='大客' and cust_status='潜在' and x.respon_name is not
        // null then 1 else 0 end) a4, "
        // + "sum(case when cust_type='中小企业' and cust_status='存量' then 1 else 0 end) b1,
        // "
        // + "sum(case when cust_type='中小企业' and cust_status='潜在' then 1 else 0 end) b2,
        // "
        // + "sum(case when cust_type='中小企业' and cust_status='存量' and x.respon_name is
        // not null then 1 else 0 end) b3, "
        // + "sum(case when cust_type='中小企业' and cust_status='潜在' and x.respon_name is
        // not null then 1 else 0 end) b4 "
        // + "from (select "
        // + "translate((case when county_name in
        // ('长乐','福清','连江','平潭','晋江市','石狮市','南安市','惠安县') "
        // + "then county_name else area_name end), '#市县', '#') c17, a.* from "
        // + "jk_nl_info a where sett_cycle='" + getComboMonth() + "' ) x "
        // + "group by c17 "
        // + "order by
        // decode(c17,'省公司',0,'福州',1,'厦门',2,'泉州',3,'漳州',4,'宁德',5,'莆田',6,'南平',7,'三明',8,'龙岩',9,
        // "
        // + "'长乐',11,'福清',12,'连江',13,'平潭',14,'晋江',15,'石狮',16,'南安',17,'惠安',18) " ;
        String sql = "select m.*,n.* from "
                + "(select c17,  sum(case when cust_type='大客' and cust_status='存量' then 1 else 0 end) a1, "
                + " sum(case when cust_type='大客' and cust_status='潜在' then 1 else 0 end) a2, "
                + "                sum(case when cust_type='大客' and cust_status='存量' and x.respon_name is not null then 1 else 0 end) a3, "
                + "                sum(case when cust_type='大客' and cust_status='潜在' and x.respon_name is not null then 1 else 0 end) a4, "
                + "                sum(case when cust_type='中小企业' and cust_status='存量' then 1 else 0 end) b1, "
                + "                sum(case when cust_type='中小企业' and cust_status='潜在' then 1 else 0 end) b2, "
                + "                sum(case when cust_type='中小企业' and cust_status='存量' and x.respon_name is not null then 1 else 0 end) b3, "
                + "                sum(case when cust_type='中小企业' and cust_status='潜在' and x.respon_name is not null then 1 else 0 end) b4  "
                + "                from  (select  "
                + "                translate((case when county_name in ('长乐','福清','连江','平潭','晋江市','石狮市','南安市','惠安县') "
                + "                then county_name else area_name end), '#市县', '#') c17, a.* from "
                + "                jk_nl_info  a where sett_cycle='" + getComboMonth() + "'  ) x "
                + "                group by c17 "
                + "                order by decode(c17,'省公司',0,'福州',1,'厦门',2,'泉州',3,'漳州',4,'宁德',5,'莆田',6,'南平',7,'三明',8,'龙岩',9, "
                + "                '长乐',11,'福清',12,'连江',13,'平潭',14,'晋江',15,'石狮',16,'南安',17,'惠安',18) ) m,"
                + "(select c17,  sum(case when cust_type='大客' and cust_status='存量' then 1 else 0 end) a1, "
                + "                sum(case when cust_type='大客' and cust_status='潜在' then 1 else 0 end) a2, "
                + "                sum(case when cust_type='大客' and cust_status='存量' and x.respon_name is not null then 1 else 0 end) a3, "
                + "                sum(case when cust_type='大客' and cust_status='潜在' and x.respon_name is not null then 1 else 0 end) a4, "
                + "                sum(case when cust_type='中小企业' and cust_status='存量' then 1 else 0 end) b1, "
                + "                sum(case when cust_type='中小企业' and cust_status='潜在' then 1 else 0 end) b2, "
                + "                sum(case when cust_type='中小企业' and cust_status='存量' and x.respon_name is not null then 1 else 0 end) b3, "
                + "                sum(case when cust_type='中小企业' and cust_status='潜在' and x.respon_name is not null then 1 else 0 end) b4  "
                + "                from  (select  "
                + "                translate((case when county_name in ('长乐','福清','连江','平潭','晋江市','石狮市','南安市','惠安县') "
                + "                then county_name else area_name end), '#市县', '#') c17, a.* from "
                + "                jk_nl_info  a where sett_cycle='" + getComboLastMonth() + "'  ) x "
                + "                group by c17 "
                + "                order by decode(c17,'省公司',0,'福州',1,'厦门',2,'泉州',3,'漳州',4,'宁德',5,'莆田',6,'南平',7,'三明',8,'龙岩',9, "
                + "                '长乐',11,'福清',12,'连江',13,'平潭',14,'晋江',15,'石狮',16,'南安',17,'惠安',18) ) n"
                + "                where m.c17=n.c17";

        List<Object[]> queryret = Dao.getInstance().QuerySomeNote(sql);
        int i1 = 0, i2 = 0, i3 = 0, i4 = 0;
        int i5 = 0, i6 = 0, i7 = 0, i8 = 0;
        int n1 = 0, n2 = 0, n3 = 0, n4 = 0;
        int n5 = 0, n6 = 0, n7 = 0, n8 = 0;
        HSSFCellStyle percentcellStyle = createPercentCellStyle(wkb);
        for (int i = 0; i < queryret.size(); i++) {
            int nlcnt1 = Integer.valueOf(queryret.get(i)[1].toString());
            int nlcnt2 = Integer.valueOf(queryret.get(i)[2].toString());
            int nlcnt3 = Integer.valueOf(queryret.get(i)[3].toString());
            int nlcnt4 = Integer.valueOf(queryret.get(i)[4].toString());
            int nlcnt5 = Integer.valueOf(queryret.get(i)[5].toString());
            int nlcnt6 = Integer.valueOf(queryret.get(i)[6].toString());
            int nlcnt7 = Integer.valueOf(queryret.get(i)[7].toString());
            int nlcnt8 = Integer.valueOf(queryret.get(i)[8].toString());
            int nncnt1 = Integer.valueOf(queryret.get(i)[10].toString());
            int nncnt2 = Integer.valueOf(queryret.get(i)[11].toString());
            int nncnt3 = Integer.valueOf(queryret.get(i)[12].toString());
            int nncnt4 = Integer.valueOf(queryret.get(i)[13].toString());
            int nncnt5 = Integer.valueOf(queryret.get(i)[14].toString());
            int nncnt6 = Integer.valueOf(queryret.get(i)[15].toString());
            int nncnt7 = Integer.valueOf(queryret.get(i)[16].toString());
            int nncnt8 = Integer.valueOf(queryret.get(i)[17].toString());

            i1 += nlcnt1;
            i2 += nlcnt2;
            i3 += nlcnt3;
            i4 += nlcnt4;
            i5 += nlcnt5;
            i6 += nlcnt6;
            i7 += nlcnt7;
            i8 += nlcnt8;
            n1 += nncnt1;
            n2 += nncnt2;
            n3 += nncnt3;
            n4 += nncnt4;
            n5 += nncnt5;
            n6 += nncnt6;
            n7 += nncnt7;
            n8 += nncnt8;

            double percent1 = (double) nlcnt3 / nlcnt1;
            double percent2 = (double) nlcnt4 / nlcnt2;
            double percent3 = (double) nlcnt7 / nlcnt5;
            double percent4 = (double) nlcnt8 / nlcnt6;

            double percent5 = (double) nncnt3 / nncnt1;
            double percent6 = (double) nncnt4 / nncnt2;
            double percent7 = (double) nncnt7 / nncnt5;
            double percent8 = (double) nncnt8 / nncnt6;

            HSSFRow row = sheet.getRow(i + 2);
            row.getCell(0).setCellValue((String) queryret.get(i)[0]);
            row.getCell(1).setCellValue(nlcnt1);
            row.getCell(2).setCellValue(nlcnt1 - nncnt1);
            row.getCell(3).setCellValue(nlcnt2);
            row.getCell(4).setCellValue(nlcnt2 - nncnt2);
            row.getCell(5).setCellValue(nlcnt3);
            row.getCell(6).setCellValue(percent1);
            row.getCell(7).setCellValue(percent1 - percent5);
            row.getCell(8).setCellValue(nlcnt4);
            row.getCell(9).setCellValue(percent2);
            row.getCell(10).setCellValue(percent2 - percent6);

            row.getCell(11).setCellValue(nlcnt5);
            row.getCell(12).setCellValue(nlcnt5 - nncnt5);
            row.getCell(13).setCellValue(nlcnt6);
            row.getCell(14).setCellValue(nlcnt6 - nncnt6);
            row.getCell(15).setCellValue(nlcnt7);
            row.createCell(16).setCellValue(percent3);
            row.getCell(17).setCellValue(percent3 - percent7);
            row.getCell(18).setCellValue(nlcnt8);
            row.createCell(19).setCellValue(percent4);
            row.getCell(20).setCellValue(percent4 - percent8);

            row.getCell(6).setCellStyle(percentcellStyle);
            row.getCell(7).setCellStyle(percentcellStyle);
            row.getCell(9).setCellStyle(percentcellStyle);
            row.getCell(10).setCellStyle(percentcellStyle);
            row.getCell(16).setCellStyle(percentcellStyle);
            row.getCell(17).setCellStyle(percentcellStyle);
            row.getCell(19).setCellStyle(percentcellStyle);
            row.getCell(20).setCellStyle(percentcellStyle);
        }

        double percent1 = (double) i3 / i1;
        double percent2 = (double) i4 / i2;
        double percent3 = (double) i7 / i5;
        double percent4 = (double) i8 / i6;
        double percent5 = (double) n3 / n1;
        double percent6 = (double) n4 / n2;
        double percent7 = (double) n7 / n5;
        double percent8 = (double) n8 / n6;

        HSSFRow row = sheet.getRow(queryret.size() + 2);
        row.getCell(0).setCellValue("全省");
        row.getCell(1).setCellValue(i1);
        row.getCell(2).setCellValue(i1 - n1);
        row.getCell(3).setCellValue(i2);
        row.getCell(4).setCellValue(i2 - n2);
        row.getCell(5).setCellValue(i3);
        row.getCell(6).setCellValue(percent1);
        row.getCell(7).setCellValue(percent1 - percent5);
        row.getCell(8).setCellValue(i4);
        row.getCell(9).setCellValue(percent2);
        row.getCell(10).setCellValue(percent2 - percent6);

        row.getCell(11).setCellValue(i5);
        row.getCell(12).setCellValue(i5 - n5);
        row.getCell(13).setCellValue(i6);
        row.getCell(14).setCellValue(i6 - n6);
        row.getCell(15).setCellValue(i7);
        row.getCell(16).setCellValue(percent3);
        row.getCell(17).setCellValue(percent3 - percent7);
        row.getCell(18).setCellValue(i8);
        row.getCell(19).setCellValue(percent4);
        row.getCell(20).setCellValue(percent4 - percent8);

        row.getCell(6).setCellStyle(percentcellStyle);
        row.getCell(7).setCellStyle(percentcellStyle);
        row.getCell(9).setCellStyle(percentcellStyle);
        row.getCell(10).setCellStyle(percentcellStyle);
        row.getCell(16).setCellStyle(percentcellStyle);
        row.getCell(17).setCellStyle(percentcellStyle);
        row.getCell(19).setCellStyle(percentcellStyle);
        row.getCell(20).setCellStyle(percentcellStyle);
        System.out.println("名单数量情况完成.");

    }

    public static void setSecondSheetStyle(HSSFWorkbook wkb, HSSFSheet sheet) {
        HSSFCellStyle cellstylehead = createHeadCellStyle(wkb);
        HSSFCellStyle cellstylebody = createBodyCellStyle(wkb);

        for (int x = 0; x < 21; x++) {
            sheet.createRow(x);
            for (int y = 0; y < 13; y++) {
                sheet.getRow(x).createCell(y);
                if (x <= 1) {
                    sheet.getRow(x).getCell(y).setCellStyle(cellstylehead);
                } else {
                    sheet.getRow(x).getCell(y).setCellStyle(cellstylebody);
                }
            }
        }
    }

    private static void writeSecondSheet(HSSFWorkbook wkb) throws IOException {
        HSSFSheet sheet = wkb.createSheet("存量名单完整性");
        setSecondSheetStyle(wkb, sheet);
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 6));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 7, 12));
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 0));

        HSSFRow rowhead1 = sheet.getRow(0);
        rowhead1.getCell(0).setCellValue("市县");
        rowhead1.getCell(1).setCellValue("大客名单");
        rowhead1.getCell(7).setCellValue("商企名单");
        HSSFRow rowhead2 = sheet.getRow(1);
        rowhead2.getCell(1).setCellValue("存量名单数量");
        rowhead2.getCell(2).setCellValue("员工数异常数");
        rowhead2.getCell(3).setCellValue("大于千人员工名单数");
        rowhead2.getCell(4).setCellValue("联系人异常数");
        rowhead2.getCell(5).setCellValue("GPS地址异常数");
        rowhead2.getCell(6).setCellValue("名单资料异常数");

        rowhead2.getCell(7).setCellValue("存量名单数量");
        rowhead2.getCell(8).setCellValue("员工数异常数");
        rowhead2.getCell(9).setCellValue("大于千人员工名单数");
        rowhead2.getCell(10).setCellValue("联系人异常数");
        rowhead2.getCell(11).setCellValue("GPS地址异常数");
        rowhead2.getCell(12).setCellValue("名单资料异常数");
        setWidth(sheet, 21);
        rowhead2.setHeight((short) (15.625 * 70));

        String sql = "select c17,  " + "sum(case when cust_type='大客' and cust_status='存量' then 1 else 0 end) a1, "
                + "sum(case when cust_type='大客' and cust_status='存量' and (staff_num is null or "
                + "  trim(translate(rtrim(ltrim(staff_num)), '#0123456789', '#')) is not null)  "
                + "  then 1 else 0 end) a2, " + "sum(case when cust_type='大客' and cust_status='存量' and "
                + "  to_number(translate(staff_num,staff_num||'0123456789','0123456789'))>=1000  "
                + "  then 1 else 0 end) a3,  " + "sum(case when cust_type='大客' and cust_status='存量' and "
                + "  asciistr(link_name) not like '%\\%'  " + "  then 1 else 0 end) a4, "
                + "sum(case when cust_type='大客' and cust_status='存量' and "
                + "  gps_addr is null  then 1 else 0 end) a5, "
                + "sum(case when cust_type='大客' and cust_status='存量' and "
                + "  ((staff_num is null or trim(translate(rtrim(ltrim(staff_num)), '#0123456789', '#')) is not null) "
                + "  or asciistr(link_name) not like '%\\%' or  gps_addr is null)  " + "  then 1 else 0 end) a6, "
                + "sum(case when cust_type='中小企业' and cust_status='存量' then 1 else 0 end) a1, "
                + "sum(case when cust_type='中小企业' and cust_status='存量' and (staff_num is null or "
                + "  trim(translate(rtrim(ltrim(staff_num)), '#0123456789', '#')) is not null)  "
                + "  then 1 else 0 end) a2, " + "sum(case when cust_type='中小企业' and cust_status='存量' and "
                + "  to_number(translate(staff_num,staff_num||'0123456789','0123456789'))>=1000  "
                + "  then 1 else 0 end) a3,  " + "sum(case when cust_type='中小企业' and cust_status='存量' and "
                + "  asciistr(link_name) not like '%\\%'  " + "  then 1 else 0 end) a4, "
                + "sum(case when cust_type='中小企业' and cust_status='存量' and "
                + "  gps_addr is null  then 1 else 0 end) a5, "
                + "sum(case when cust_type='中小企业' and cust_status='存量' and "
                + "  ((staff_num is null or trim(translate(rtrim(ltrim(staff_num)), '#0123456789', '#')) is not null) "
                + "  or asciistr(link_name) not like '%\\%' or  gps_addr is null)  " + "  then 1 else 0 end) a6 "
                + "from  " + "(select  "
                + "translate((case when county_name in ('长乐','福清','连江','平潭','晋江市','石狮市','南安市','惠安县') "
                + "then county_name else area_name end), '#市县', '#') c17, a.* " + "from jk_nl_info a where sett_cycle='"
                + getComboMonth() + "' ) x " + "group by c17 "
                + "order by decode(c17,'省公司',0,'福州',1,'厦门',2,'泉州',3,'漳州',4,'宁德',5,'莆田',6,'南平',7,'三明',8,'龙岩',9, "
                + "'长乐',11,'福清',12,'连江',13,'平潭',14,'晋江',15,'石狮',16,'南安',17,'惠安',18) ";

        List<Object[]> queryret = Dao.getInstance().QuerySomeNote(sql);
        int i1 = 0, i2 = 0, i3 = 0, i4 = 0, i5 = 0, i6 = 0;
        int i7 = 0, i8 = 0, i9 = 0, i10 = 0, i11 = 0, i12 = 0;
        for (int i = 0; i < queryret.size(); i++) {
            int nlcnt1 = Integer.valueOf(queryret.get(i)[1].toString());
            int nlcnt2 = Integer.valueOf(queryret.get(i)[2].toString());
            int nlcnt3 = Integer.valueOf(queryret.get(i)[3].toString());
            int nlcnt4 = Integer.valueOf(queryret.get(i)[4].toString());
            int nlcnt5 = Integer.valueOf(queryret.get(i)[5].toString());
            int nlcnt6 = Integer.valueOf(queryret.get(i)[6].toString());
            int nlcnt7 = Integer.valueOf(queryret.get(i)[7].toString());
            int nlcnt8 = Integer.valueOf(queryret.get(i)[8].toString());
            int nlcnt9 = Integer.valueOf(queryret.get(i)[9].toString());
            int nlcnt10 = Integer.valueOf(queryret.get(i)[10].toString());
            int nlcnt11 = Integer.valueOf(queryret.get(i)[11].toString());
            int nlcnt12 = Integer.valueOf(queryret.get(i)[12].toString());

            i1 += nlcnt1;
            i2 += nlcnt2;
            i3 += nlcnt3;
            i4 += nlcnt4;
            i5 += nlcnt5;
            i6 += nlcnt6;
            i7 += nlcnt7;
            i8 += nlcnt8;
            i9 += nlcnt9;
            i10 += nlcnt10;
            i11 += nlcnt11;
            i12 += nlcnt12;

            HSSFRow row = sheet.getRow(i + 2);
            row.getCell(0).setCellValue((String) queryret.get(i)[0]);
            row.getCell(1).setCellValue(nlcnt1);
            row.getCell(2).setCellValue(nlcnt2);
            row.getCell(3).setCellValue(nlcnt3);
            row.getCell(4).setCellValue(nlcnt4);
            row.getCell(5).setCellValue(nlcnt5);
            row.getCell(6).setCellValue(nlcnt6);

            row.getCell(7).setCellValue(nlcnt7);
            row.getCell(8).setCellValue(nlcnt8);
            row.getCell(9).setCellValue(nlcnt9);
            row.getCell(10).setCellValue(nlcnt10);
            row.getCell(11).setCellValue(nlcnt11);
            row.getCell(12).setCellValue(nlcnt12);
        }

        HSSFRow row = sheet.getRow(queryret.size() + 2);
        row.getCell(0).setCellValue("全省");
        row.getCell(1).setCellValue(i1);
        row.getCell(2).setCellValue(i2);
        row.getCell(3).setCellValue(i3);
        row.getCell(4).setCellValue(i4);
        row.getCell(5).setCellValue(i5);
        row.getCell(6).setCellValue(i6);
        row.getCell(7).setCellValue(i7);
        row.getCell(8).setCellValue(i8);
        row.getCell(9).setCellValue(i9);
        row.getCell(10).setCellValue(i10);

        row.getCell(11).setCellValue(i11);
        row.getCell(12).setCellValue(i12);
        System.out.println("存量名单完整性完成.");
    }

    public static void setThirdSheetStyle(HSSFWorkbook wkb, HSSFSheet sheet) {
        HSSFCellStyle cellstylehead = createHeadCellStyle(wkb);
        HSSFCellStyle cellstylebody = createBodyCellStyle(wkb);
        HSSFCellStyle cellstyleperc = createPercentCellStyle(wkb);

        for (int x = 0; x < 21; x++) {
            sheet.createRow(x);
            for (int y = 0; y < 19; y++) {
                sheet.getRow(x).createCell(y);
                if (x <= 1) {
                    sheet.getRow(x).getCell(y).setCellStyle(cellstylehead);
                } else if (y == 4 || y == 6 || y == 9 || y == 13 || y == 15 || y == 18) {
                    sheet.getRow(x).getCell(y).setCellStyle(cellstyleperc);
                } else {
                    sheet.getRow(x).getCell(y).setCellStyle(cellstylebody);
                }
            }
        }
    }

    private static void writeThirdSheet(HSSFWorkbook wkb) throws IOException {
        HSSFSheet sheet = wkb.createSheet("存量名单渗透率");
        setThirdSheetStyle(wkb, sheet);

        sheet.addMergedRegion(new CellRangeAddress(0, 0, 1, 9));
        sheet.addMergedRegion(new CellRangeAddress(0, 0, 10, 18));
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 0));

        HSSFRow rowhead1 = sheet.getRow(0);
        rowhead1.getCell(0).setCellValue("市县");
        rowhead1.getCell(1).setCellValue("大客名单");
        rowhead1.getCell(10).setCellValue("商企名单");
        HSSFRow rowhead2 = sheet.getRow(1);
        rowhead2.getCell(1).setCellValue("存量名单数量");
        rowhead2.getCell(2).setCellValue("有固网收入存量名单数量");
        rowhead2.getCell(3).setCellValue("有专租线收入存量名单数量");
        rowhead2.getCell(4).setCellValue("固网渗透率");
        rowhead2.getCell(5).setCellValue("一个月未拜访存量名单数");
        rowhead2.getCell(6).setCellValue("未拜访名单率");
        rowhead2.getCell(7).setCellValue("潜在名单数量");
        rowhead2.getCell(8).setCellValue("三个月未拜访潜在名单数");
        rowhead2.getCell(9).setCellValue("未拜访名单率");

        rowhead2.getCell(10).setCellValue("存量名单数量");
        rowhead2.getCell(11).setCellValue("有固网收入存量名单数量");
        rowhead2.getCell(12).setCellValue("有专租线收入存量名单数量");
        rowhead2.getCell(13).setCellValue("固网渗透率");
        rowhead2.getCell(14).setCellValue("一个月未拜访存量名单数");
        rowhead2.getCell(15).setCellValue("未拜访名单率");
        rowhead2.getCell(16).setCellValue("潜在名单数量");
        rowhead2.getCell(17).setCellValue("三个月未拜访潜在名单数");
        rowhead2.getCell(18).setCellValue("未拜访名单率");

        setWidth(sheet, 21);
        rowhead2.setHeight((short) (15.625 * 70));

        String sql = "select c17, " + "sum(case when cust_type='大客' and cust_status='存量' then 1 else 0 end) a1,  "
                + "sum(case when cust_type='大客' and cust_status='存量' and nvl(y.cnc,0)>0  then 1 else 0 end) a2,  "
                + "sum(case when cust_type='大客' and cust_status='存量' and nvl(y.专租线,0)>0  then 1 else 0 end) a3, "
                + "sum(case when cust_type='大客' and cust_status='存量' and nvl(z.cnt,0)>0 then 1 else 0 end) a4,   "
                + "sum(case when cust_type='大客' and cust_status='潜在' then 1 else 0 end) a5,   "
                + "sum(case when cust_type='大客' and cust_status='潜在' and nvl(l.cnt,0)>0 then 1 else 0 end) a6,     "
                + "sum(case when cust_type='中小企业' and cust_status='存量' then 1 else 0 end) a1,  "
                + "sum(case when cust_type='中小企业' and cust_status='存量' and nvl(y.cnc,0)>0  then 1 else 0 end) a2,  "
                + "sum(case when cust_type='中小企业' and cust_status='存量' and nvl(y.专租线,0)>0  then 1 else 0 end) a3, "
                + "sum(case when cust_type='中小企业' and cust_status='存量' and nvl(z.cnt,0)>0 then 1 else 0 end) a4,   "
                + "sum(case when cust_type='中小企业' and cust_status='潜在' then 1 else 0 end) a5,   "
                + "sum(case when cust_type='中小企业' and cust_status='潜在' and nvl(l.cnt,0)>0 then 1 else 0 end) a6     "
                + "from      "
                + "  (select translate((case when county_name in ('长乐','福清','连江','平潭','晋江市','石狮市','南安市','惠安县') "
                + "  then county_name else area_name end), '#市县', '#') c17,   "
                + "  a.* from jk_nl_info a where sett_cycle = '" + getComboMonth() + "' ) x,    "
                + "  (select 名单制客户编码,      "
                + "  sum(case when 用户业务分类 in ('CBSS固话','CBSS宽带','IDC','固话','宽带','网元','专线') then 出账收入_元 else 0 end) cnc,  "
                + "  sum(case when 用户业务分类 in ('网元','专线') then 出账收入_元 else 0 end) 专租线 "
                + "  from st.rep_user_develop_del_m_" + getComboLastMonth() + "  " + "  where 名单制客户编码 is not null "
                + "  group by 名单制客户编码 having sum(case when "
                + "  用户业务分类 in ('CBSS固话','CBSS宽带','IDC','固话','宽带','网元','专线') then 出账收入_元 else 0 end)>0) y, "
                + "  (select nl_name,count(*) cnt from jk_nl_visit " + "  where to_char(visit_time, 'yyyymm')='"
                + getComboLastMonth() + "' group by nl_name) z,  "
                + "  (select nl_name,count(*) cnt from jk_nl_visit where to_char(visit_time, 'yyyymm') " + "  between '"
                + getComboLLLMonth() + "' and '" + getComboLastMonth() + "'   group by nl_name) l "
                + "where x.nl_code=y.名单制客户编码(+) and x.nl_name=z.nl_name(+) and x.nl_name=l.nl_name(+)  "
                + "group by c17 "
                + "order by decode(c17,'省公司',0,'福州',1,'厦门',2,'泉州',3,'漳州',4,'宁德',5,'莆田',6,'南平',7,'三明',8,'龙岩',9, "
                + "'长乐',11,'福清',12,'连江',13,'平潭',14,'晋江',15,'石狮',16,'南安',17,'惠安',18) ";

        List<Object[]> queryret = Dao.getInstance().QuerySomeNote(sql);
        int i1 = 0, i2 = 0, i3 = 0, i4 = 0, i5 = 0, i6 = 0;
        int i7 = 0, i8 = 0, i9 = 0, i10 = 0, i11 = 0, i12 = 0;
        for (int i = 0; i < queryret.size(); i++) {
            int nlcnt1 = Integer.valueOf(queryret.get(i)[1].toString());
            int nlcnt2 = Integer.valueOf(queryret.get(i)[2].toString());
            int nlcnt3 = Integer.valueOf(queryret.get(i)[3].toString());
            int nlcnt4 = Integer.valueOf(queryret.get(i)[4].toString());
            int nlcnt5 = Integer.valueOf(queryret.get(i)[5].toString());
            int nlcnt6 = Integer.valueOf(queryret.get(i)[6].toString());
            int nlcnt7 = Integer.valueOf(queryret.get(i)[7].toString());
            int nlcnt8 = Integer.valueOf(queryret.get(i)[8].toString());
            int nlcnt9 = Integer.valueOf(queryret.get(i)[9].toString());
            int nlcnt10 = Integer.valueOf(queryret.get(i)[10].toString());
            int nlcnt11 = Integer.valueOf(queryret.get(i)[11].toString());
            int nlcnt12 = Integer.valueOf(queryret.get(i)[12].toString());

            i1 += nlcnt1;
            i2 += nlcnt2;
            i3 += nlcnt3;
            i4 += nlcnt4;
            i5 += nlcnt5;
            i6 += nlcnt6;
            i7 += nlcnt7;
            i8 += nlcnt8;
            i9 += nlcnt9;
            i10 += nlcnt10;
            i11 += nlcnt11;
            i12 += nlcnt12;

            double percent1 = (double) nlcnt3 / nlcnt1;
            double percent2 = (double) (nlcnt1 - nlcnt4) / nlcnt1;
            double percent3 = (double) (nlcnt5 - nlcnt6) / nlcnt5;
            double percent4 = (double) nlcnt9 / nlcnt7;
            double percent5 = (double) (nlcnt7 - nlcnt10) / nlcnt7;
            double percent6 = (double) (nlcnt11 - nlcnt12) / nlcnt11;

            HSSFRow row = sheet.getRow(i + 2);
            row.getCell(0).setCellValue((String) queryret.get(i)[0]);
            row.getCell(1).setCellValue(nlcnt1);
            row.getCell(2).setCellValue(nlcnt2);
            row.getCell(3).setCellValue(nlcnt3);
            row.getCell(4).setCellValue(percent1);
            row.getCell(5).setCellValue(nlcnt1 - nlcnt4);
            row.getCell(6).setCellValue(percent2);
            row.getCell(7).setCellValue(nlcnt5);
            row.getCell(8).setCellValue(nlcnt5 - nlcnt6);
            row.getCell(9).setCellValue(percent3);

            row.getCell(10).setCellValue(nlcnt7);
            row.getCell(11).setCellValue(nlcnt8);
            row.getCell(12).setCellValue(nlcnt9);
            row.getCell(13).setCellValue(percent4);
            row.getCell(14).setCellValue(nlcnt7 - nlcnt10);
            row.getCell(15).setCellValue(percent5);
            row.getCell(16).setCellValue(nlcnt11);
            row.getCell(17).setCellValue(nlcnt11 - nlcnt12);
            row.getCell(18).setCellValue(percent6);
        }

        double percent1 = (double) i3 / i1;
        double percent2 = (double) (i1 - i4) / i1;
        double percent3 = (double) (i5 - i6) / i5;
        double percent4 = (double) i9 / i7;
        double percent5 = (double) (i7 - i10) / i7;
        double percent6 = (double) (i11 - i12) / i11;

        HSSFRow row = sheet.getRow(queryret.size() + 2);
        row.getCell(0).setCellValue("全省");
        row.getCell(1).setCellValue(i1);
        row.getCell(2).setCellValue(i2);
        row.getCell(3).setCellValue(i3);
        row.getCell(4).setCellValue(percent1);
        row.getCell(5).setCellValue(i1 - i4);
        row.getCell(6).setCellValue(percent2);
        row.getCell(7).setCellValue(i5);
        row.getCell(8).setCellValue(i5 - i6);
        row.getCell(9).setCellValue(percent3);

        row.getCell(10).setCellValue(i7);
        row.getCell(11).setCellValue(i8);
        row.getCell(12).setCellValue(i9);
        row.getCell(13).setCellValue(percent4);
        row.getCell(14).setCellValue(i7 - i10);
        row.getCell(15).setCellValue(percent5);
        row.getCell(16).setCellValue(i11);
        row.getCell(17).setCellValue(i11 - i12);
        row.getCell(18).setCellValue(percent6);
        System.out.println("名单渗透率完成.");
    }

    private static String getComboLastMonth() {
        DateTimeFormatter formate = DateTimeFormatter.ofPattern("yyyyMM");
        int year = Integer.parseInt(RosterCircularPanel.settcycle.getSelectedItem().toString().substring(0, 4));
        int month = Integer.parseInt(RosterCircularPanel.settcycle.getSelectedItem().toString().substring(4, 6));
        return LocalDate.of(year, month, 1).minusMonths(1).format(formate);
    }

    private static String getComboMonth() {
        return RosterCircularPanel.settcycle.getSelectedItem().toString();
    }

    private static String getComboLLLMonth() {
        DateTimeFormatter formate = DateTimeFormatter.ofPattern("yyyyMM");
        int year = Integer.parseInt(RosterCircularPanel.settcycle.getSelectedItem().toString().substring(0, 4));
        int month = Integer.parseInt(RosterCircularPanel.settcycle.getSelectedItem().toString().substring(4, 6));
        return LocalDate.of(year, month, 1).minusMonths(3).format(formate);
    }

    public static void writeExecl(File file) throws IOException {
        FileOutputStream fileOut = new FileOutputStream(file);
        HSSFWorkbook wkb = new HSSFWorkbook();

        writeFirstSheet(wkb);
        writeSecondSheet(wkb);
        writeThirdSheet(wkb);

        wkb.write(fileOut);
//        wkb.close();
        fileOut.close();

        String msg = "文件已生成";
        JOptionPane.showMessageDialog(null, msg, "友情提示", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void writeExecl(String filename) throws IOException {

        FileOutputStream fileOut = new FileOutputStream(filename);

        HSSFWorkbook wkb = new HSSFWorkbook();

        writeFirstSheet(wkb);
        writeSecondSheet(wkb);
        writeThirdSheet(wkb);

        wkb.write(fileOut);
        
        fileOut.close();

        String msg = "文件download.xls 已导出成功到" + filename;
        JOptionPane.showMessageDialog(null, msg, "友情提示", JOptionPane.INFORMATION_MESSAGE);
    }

}
