package cn.dravvern.excel;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.eventusermodel.EventWorkbookBuilder.SheetRecordCollectingListener;
import org.apache.poi.hssf.eventusermodel.FormatTrackingHSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFEventFactory;
import org.apache.poi.hssf.eventusermodel.HSSFListener;
import org.apache.poi.hssf.eventusermodel.HSSFRequest;
import org.apache.poi.hssf.eventusermodel.MissingRecordAwareHSSFListener;
import org.apache.poi.hssf.eventusermodel.dummyrecord.LastCellOfRowDummyRecord;
import org.apache.poi.hssf.eventusermodel.dummyrecord.MissingCellDummyRecord;
import org.apache.poi.hssf.model.HSSFFormulaParser;
import org.apache.poi.hssf.record.BOFRecord;
import org.apache.poi.hssf.record.BlankRecord;
import org.apache.poi.hssf.record.BoolErrRecord;
import org.apache.poi.hssf.record.BoundSheetRecord;
import org.apache.poi.hssf.record.FormulaRecord;
import org.apache.poi.hssf.record.LabelRecord;
import org.apache.poi.hssf.record.LabelSSTRecord;
import org.apache.poi.hssf.record.NumberRecord;
import org.apache.poi.hssf.record.Record;
import org.apache.poi.hssf.record.SSTRecord;
import org.apache.poi.hssf.record.StringRecord;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

public class Excel2003Reader implements HSSFListener {
    private static final int INITIAL_COLUMN_COUNT = 10;
    private int columnNum = INITIAL_COLUMN_COUNT;

    private List<List<Object[]>> listSheet = new ArrayList<List<Object[]>>();
    private List<Object[]> list = null;
    private Object[] objArr = null;

    private List<Object[]> heads = new ArrayList<Object[]>();

    private List<String> sheetNames = new ArrayList<String>();

    private int curColumn = 0;
    private int curRow = 0;
    private int sheetNo = 0;

    private boolean processed = false;

    /* Should we output the formula, or the value it has? */
    private boolean outputFormulaValues = true;
    /* For parsing Formulas */
    private SheetRecordCollectingListener workbookBuildingListener;
    private FormatTrackingHSSFListener formatListener;
    // excel2003工作薄
    private HSSFWorkbook stubWorkbook;

    private BoundSheetRecord[] orderedBSRs;
    private List<BoundSheetRecord> boundSheetRecords = new ArrayList<BoundSheetRecord>();

    // Records we pick up as we process
    private SSTRecord sstRecord;

    public List<Object[]> readSheetContent(File file, int sheetNum) throws Exception {

        if (!processed) {
            process(file);
        }

        if (listSheet.size() > sheetNum) {
            return listSheet.get(sheetNum);
        } else {
            return null;
        }
    }

    public Object[] getSheetHead(File file, int sheetNum) throws Exception {
        if (!processed) {
            process(file);
        }
        if (heads.size() > sheetNum) {
            return heads.get(sheetNum);
        } else {
            return null;
        }
    }

    public String getSheetName(File file, int sheetNum) throws Exception {
        if (!processed) {
            process(file);
        }
        if (sheetNames.size() > sheetNum) {
            return sheetNames.get(sheetNum);
        } else {
            return null;
        }
    }
    
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

    public void process(File file) throws Exception {
        POIFSFileSystem fs = new POIFSFileSystem(new FileInputStream(file));
        MissingRecordAwareHSSFListener listener = new MissingRecordAwareHSSFListener(this);
        formatListener = new FormatTrackingHSSFListener(listener);
        HSSFEventFactory factory = new HSSFEventFactory();
        HSSFRequest request = new HSSFRequest();
        if (outputFormulaValues) {
            request.addListenerForAllRecords(formatListener);
        } else {
            workbookBuildingListener = new SheetRecordCollectingListener(formatListener);
            request.addListenerForAllRecords(workbookBuildingListener);
        }
        factory.processWorkbookEvents(request, fs);
        listSheet.add(list);
        processed = true;

//        fs.close();
    }

    /**
     * HSSFListener 监听方法，处理 Record
     */
    public void processRecord(Record record) {
        String value = null;

        // printObj(objArr, "disp1||" + record.getSid() + "||");

        if (!(record instanceof LastCellOfRowDummyRecord)) {
            if (objArr != null && objArr.length == curColumn + 1) {
                columnNum = objArr.length * 2;
                Object[] holder = new String[columnNum];
                System.arraycopy(objArr, 0, holder, 0, objArr.length);
                objArr = holder;
            }
        }

        switch (record.getSid()) {
        // 133 开始解析Sheet的信息,获取sheet的名称等信息
        case BoundSheetRecord.sid:
            BoundSheetRecord boundSheetRecord = (BoundSheetRecord) record;
            boundSheetRecords.add(boundSheetRecord);
            sheetNames.add(boundSheetRecord.getSheetname());
            break;
        // 2057
        case BOFRecord.sid:
            BOFRecord bofRecord = (BOFRecord) record;
            // printObj(objArr, "disp2||" + bofRecord.getType() + "||");
            // 顺序进入新的sheet页
            if (bofRecord.getType() == BOFRecord.TYPE_WORKSHEET) {
                // 如果有需要，则建立子工作薄
                if (workbookBuildingListener != null && stubWorkbook == null) {
                    stubWorkbook = workbookBuildingListener.getStubHSSFWorkbook();
                }
                if (orderedBSRs == null) {
                    orderedBSRs = BoundSheetRecord.orderByBofPosition(boundSheetRecords);
                }
                if (sheetNo > 0) {
                    listSheet.add(list);
                }
                // sheetName = orderedBSRs[sheetNo].getSheetname();
                list = new ArrayList<Object[]>();
                objArr = new Object[columnNum];
                sheetNo++;
                // printObj(objArr, "disp3||" + sheetName + "||" + orderedBSRs);
            } else if (bofRecord.getType() == BOFRecord.TYPE_WORKBOOK) { // 顺序进入新的Workbook

            }
            break;
        // 252 SSTRecords store a array of unique strings used in Excel.
        case SSTRecord.sid:
            sstRecord = (SSTRecord) record;
            break;
        // 513 空白记录的信息
        case BlankRecord.sid:
            BlankRecord blankRecord = (BlankRecord) record;
            curRow = blankRecord.getRow();
            curColumn = blankRecord.getColumn();
            objArr[curColumn] = "";
            break;
        // 517 解析boolean错误信息
        case BoolErrRecord.sid: // 单元格为布尔类型
            BoolErrRecord boolErrRecord = (BoolErrRecord) record;
            curRow = boolErrRecord.getRow();
            curColumn = boolErrRecord.getColumn();
            value = boolErrRecord.getBooleanValue() + "";
            objArr[curColumn] = value;
            break;
        // 6 单元格为公式类型
        case FormulaRecord.sid: // 单元格为公式类型
            FormulaRecord formulaRecord = (FormulaRecord) record;
            curRow = formulaRecord.getRow();
            curColumn = formulaRecord.getColumn();
            if (outputFormulaValues) {
                if (Double.isNaN(formulaRecord.getValue())) {
                    // Formula result is a string
                    // This is stored in the next record
                    // outputNextStringRecord = true;
                    // nextRow = frec.getRow();
                    // nextColumn = frec.getColumn();
                    // objArr[curColumn] = frec.getValue();
                    // printObj(objArr, "disp5");
                } else {
                    String stringValue = getValueFromString(formulaRecord.toString());
                    try {
                        Double.valueOf(stringValue);
                        value = formatListener.formatNumberDateCell(formulaRecord);
                        objArr[curColumn] = value;
                    } catch (Exception e) {
                        objArr[curColumn] = stringValue;
                    }
                    // printObj(objArr, "disp6" + "||" + frec.getValue() + "||" + frec.toString() +
                    // "||" + stringValue + "||" );

                }
            } else {
                // printObj(objArr, "disp7" + "||" + frec.getValue() + "||");
                value = '"' + HSSFFormulaParser.toFormulaString(stubWorkbook, formulaRecord.getParsedExpression())
                        + '"';
                objArr[curColumn] = value;
            }
            // rowlist.add(thisColumn, thisStr);
            break;
        // 519 单元格中公式的字符串
        case StringRecord.sid:
            // if (outputNextStringRecord) {
            StringRecord stringRecord = (StringRecord) record;
            objArr[curColumn] = stringRecord.getString();
            // outputNextStringRecord = false;
            // }
            break;
        // 516
        case LabelRecord.sid:
            LabelRecord labelRecord = (LabelRecord) record;
            curRow = labelRecord.getRow();
            curColumn = labelRecord.getColumn();
            value = labelRecord.getValue().trim();
            value = "".equals(value) ? "" : value;
            objArr[curColumn] = value;
            break;
        // 253 发现字符串类型，这儿要取字符串的值的话，跟据其index去字符串表里读取
        case LabelSSTRecord.sid: // 单元格为字符串类型
            LabelSSTRecord labelSSTRecord = (LabelSSTRecord) record;
            curRow = labelSSTRecord.getRow();
            curColumn = labelSSTRecord.getColumn();
            if (sstRecord == null) {
                objArr[curColumn] = "";
            } else {
                value = sstRecord.getString(labelSSTRecord.getSSTIndex()).toString().trim();
                value = "".equals(value) ? "" : value;
                objArr[curColumn] = value;
            }
            break;
        // 515 发现数字类型的cell
        case NumberRecord.sid: // 单元格为数字类型
            NumberRecord numberRecord = (NumberRecord) record;
            curRow = numberRecord.getRow();
            curColumn = numberRecord.getColumn();
            value = formatListener.formatNumberDateCell(numberRecord).trim();
            // printObj(objArr, "disp8" + "||" + value + "||");
            try {
                new BigDecimal(value);
                objArr[curColumn] = value;
            } catch (Exception e) {
                Date date = HSSFDateUtil.getJavaDate(numberRecord.getValue());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    objArr[curColumn] = sdf.format(date);
                } catch (Exception e2) {
                    objArr[curColumn] = 0;
                }
            }
            break;
        default:
            break;
        }

        // 空值的操作
        if (record instanceof MissingCellDummyRecord) {
            MissingCellDummyRecord mc = (MissingCellDummyRecord) record;
            curRow = mc.getRow();
            curColumn = mc.getColumn();
            objArr[curColumn] = "";
            // printObj(objArr, "disp11" + "||");
        }

        // printObj(objArr, "disp4" + "||" + curColumn + "||" + curRow + "||");
        if (record instanceof LastCellOfRowDummyRecord) {
            if (curColumn != 0 && checkNullRow(objArr)) {
                if (curRow == 0) {
                    String[] clone = new String[curColumn + 1];
                    System.arraycopy(objArr, 0, clone, 0, curColumn + 1);
                    objArr = clone;
                    this.columnNum = curColumn + 1;
                    
                    heads.add(objArr);
                } else {
                    list.add(objArr);
                }
                curColumn = 0;
            }
            objArr = new String[columnNum];
        }
    }

    private boolean checkNullRow(Object[] obj) {
        boolean bl = false;
        String temp;
        for (int i = 0, size = obj.length; i < size; i++) {
            temp = (String) obj[i];
            if (temp == null || temp.trim().length() == 0)
                continue;
            bl = true;
            break;
        }
        return bl;
    }

    private String getValueFromString(String s) {
        Pattern pattern = Pattern.compile("value\\s+=(.*)\\[");
        Matcher matcher = pattern.matcher(s);
        if (matcher.find()) {
            return matcher.group(1).trim();
        } else {
            Pattern pattern2 = Pattern.compile("value\\s+=(.*)");
            Matcher matcher2 = pattern2.matcher(s);
            if (matcher2.find()) {
                return matcher2.group(1);
            } else {
                return "";
            }
        }
    }
}
