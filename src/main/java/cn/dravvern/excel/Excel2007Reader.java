package cn.dravvern.excel;

import java.io.File;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.BuiltinFormats;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.model.SharedStringsTable;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class Excel2007Reader extends DefaultHandler {
    private static final int INITIAL_COLUMN_COUNT = 10;
    private int columnNum = INITIAL_COLUMN_COUNT;

    private List<List<Object[]>> listSheet = new ArrayList<List<Object[]>>();
    private List<Object[]> list;
    private Object[] objArr = new Object[this.columnNum];

    private List<Object[]> heads = new ArrayList<Object[]>();

    private List<String> sheetNames = new ArrayList<String>();

    private int curColumn = 0;
    private int curRow = 0;
    private int sheetNo = 0;

    // 上一次的内容
    private String lastContents;
    private StringBuffer value = new StringBuffer();

    private boolean processed = false;

    // Set when cell start element is seen;
    private String nextDataType;
    private StylesTable stylesTable;
    // Used to format numeric cell values.
    private short formatIndex;
    private String formatString;

    // 共享字符串表
    private SharedStringsTable sst;

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

    public void processOneSheet(String filename, int sheetId) throws Exception {
        OPCPackage pkg = OPCPackage.open(filename);
        XSSFReader r = new XSSFReader(pkg);
        SharedStringsTable sst = r.getSharedStringsTable();
        XMLReader parser = fetchSheetParser(sst);

        // 根据 rId# 或 rSheet# 查找sheet
        InputStream sheet2 = r.getSheet("rId" + sheetId);
        InputSource sheetSource = new InputSource(sheet2);
        parser.parse(sheetSource);
        sheet2.close();
    }

    public void process(File file) throws Exception {
        OPCPackage pkg = OPCPackage.open(file);
        XSSFReader xssfReader = new XSSFReader(pkg);
        stylesTable = xssfReader.getStylesTable();
        SharedStringsTable sst = xssfReader.getSharedStringsTable();
        XMLReader parser = fetchSheetParser(sst);
        // Iterator<InputStream> sheets = xssfReader.getSheetsData();
        XSSFReader.SheetIterator iter = (XSSFReader.SheetIterator) xssfReader.getSheetsData();
        while (iter.hasNext()) {
            list = new ArrayList<Object[]>();
            curRow = 0;
            sheetNo++;
            InputStream sheet = iter.next();
            String sheetName = iter.getSheetName();
            InputSource sheetSource = new InputSource(sheet);
            parser.parse(sheetSource);
            listSheet.add(list);
            sheetNames.add(sheetName);
            sheet.close();
        }
        processed = true;
    }

    public XMLReader fetchSheetParser(SharedStringsTable sst) throws Exception {
        SAXParserFactory saxFactory = SAXParserFactory.newInstance();
        SAXParser saxParser = saxFactory.newSAXParser();
        XMLReader parser = saxParser.getXMLReader();
        this.sst = sst;
        parser.setContentHandler(this);
        return parser;
    }

    public void startElement(String uri, String localName, String name, Attributes attributes) throws SAXException {

        // printObj(objArr, "disp1||" + localName + "||" + name + "||");

        if ("inlineStr".equals(name) || "v".equals(name)) { // 读取标签内容初始化
            value.setLength(0);
        }
        // c => 单元格
        if ("c".equals(name)) {
            String r = attributes.getValue("r");
            int firstDigit = -1;
            for (int c = 0; c < r.length(); ++c) {
                if (Character.isDigit(r.charAt(c))) {
                    firstDigit = c;
                    break;
                }
            }
            curColumn = nameToColumn(r.substring(0, firstDigit));

            // 设置默认值
            this.nextDataType = "NUMBER";
            this.formatIndex = -1;
            this.formatString = null;
            String cellType = attributes.getValue("t");
            String cellStyleStr = attributes.getValue("s");
            if ("b".equals(cellType))
                nextDataType = "BOOL";
            else if ("e".equals(cellType))
                nextDataType = "ERROR";
            else if ("inlineStr".equals(cellType))
                nextDataType = "INLINESTR";
            else if ("s".equals(cellType))
                nextDataType = "SSTINDEX";
            else if ("str".equals(cellType))
                nextDataType = "FORMULA";
            else if (cellStyleStr != null) {
                // It's a number, but almost certainly one
                // with a special style or format
                int styleIndex = Integer.parseInt(cellStyleStr);
                XSSFCellStyle style = stylesTable.getStyleAt(styleIndex);
                this.formatIndex = style.getDataFormat();
                this.formatString = style.getDataFormatString();
                if (this.formatString == null)
                    this.formatString = BuiltinFormats.getBuiltinFormat(this.formatIndex);
            }
        }
    }

    public void endElement(String uri, String localName, String name) throws SAXException {

        // printObj(objArr, "disp2||" + localName + "||" + name + "||");

        String thisStr = null;
        if ("t".equals(name) || "v".equals(name)) {
            if (objArr != null && objArr.length == curColumn) {
                this.columnNum = objArr.length * 2;
                Object[] holder = new String[this.columnNum];
                System.arraycopy(objArr, 0, holder, 0, objArr.length);
                objArr = holder;
            }
        }

        // printObj(objArr, "disp3" + "||" + localName + "||" + name + "||" +
        // nextDataType + "||");

        if ("t".equals(name)) {
            // printObj(objArr, "dispt," + localName + "," + name + "," + lastContents + ","
            // + value + ".");

            if (lastContents != null && lastContents.length() > 0) {
                int num = lastContents.length();
                XSSFRichTextString rtsi = new XSSFRichTextString(value.toString());
                lastContents = rtsi.toString();
                thisStr = lastContents.substring(num);
            } else {
                XSSFRichTextString rtsi = new XSSFRichTextString(value.toString());
                lastContents = rtsi.toString();
                thisStr = lastContents;
            }
            objArr[curColumn] = thisStr.trim();
            lastContents = "";
            value.setLength(0);
        } else if ("v".equals(name)) {
            // printObj(objArr, "dispv," + nextDataType + "," + name + "," + lastContents +
            // "," + value + "|");
            // Process the value contents as required.
            // Do now, as characters() may be called more than once
            switch (nextDataType) {

            case "BOOL":
                char first = value.charAt(0);
                thisStr = first == '0' ? "FALSE" : "TRUE";
                break;

            case "ERROR":
                thisStr = value.toString();
                break;

            case "FORMULA":
                // A formula could result in a string value,
                // so always add double-quote characters.
                thisStr = value.toString();
                break;

            case "INLINESTR":
                // TODO: have seen an example of this, so it's untested.
                XSSFRichTextString rtsi = new XSSFRichTextString(value.toString());
                thisStr = rtsi.toString();
                break;

            case "SSTINDEX":
                String sstIndex = value.toString();
                try {
                    int idx = Integer.parseInt(sstIndex);
                    thisStr = new XSSFRichTextString(sst.getEntryAt(idx)).toString();
                } catch (NumberFormatException ex) {
                    System.out.println("Failed to parse SST index '" + sstIndex + "': " + ex.toString());
                }
                break;

            case "NUMBER":
                String n = value.toString();
                // 判断是否是日期格式
                if (HSSFDateUtil.isADateFormat(this.formatIndex, n)) {
                    Double d = Double.parseDouble(n);
                    Date date = HSSFDateUtil.getJavaDate(d);
                    thisStr = formateDateToString(date);
                } else if (this.formatString != null) {
                    DecimalFormat df = new DecimalFormat("0.00");
                    thisStr = df.format(Double.parseDouble(n));
                } else {
                    thisStr = n;
                }
                break;

            default:
                thisStr = "(TODO: Unexpected type: " + nextDataType + ")";
                break;
            }
            // 置空
            lastContents = "";
            objArr[curColumn] = thisStr;
            // printObj(objArr, "disp2" + "||" + thisStr + "||");
        } else if ("row".equals(name)) {
            if (checkNullRow(objArr)) {
                // if (objArr.length != curColumn + 1) {
                // Object[] clone = new String[curColumn + 1];
                // System.arraycopy(objArr, 0, clone, 0, curColumn + 1);
                // objArr = clone;
                // this.columnNum = curColumn + 1;
                // }
                if (curRow == 0) {
                    Object[] clone = new String[curColumn + 1];
                    System.arraycopy(objArr, 0, clone, 0, curColumn + 1);
                    objArr = clone;
                    this.columnNum = curColumn + 1;
                    heads.add(objArr.clone());
                } else {
                    list.add(objArr.clone());
                }

                // if(curRow % 1000 ==0) {
                // ct2 = System.currentTimeMillis();
                // printObj(objArr, "disp5" + "||" + list.size() + "||" + objArr.length + "||" +
                // ((ct2-ct1)/1000.0) + "||");
                // ct1 = ct2 ;
                // }
                // objArr = new Object[columnNum];
            }
            for (int i = 0; i < objArr.length; i++) {
                objArr[i] = null;
            }
            curRow++;
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        value.append(ch, start, length);
    }

    public void endDocument() throws SAXException {

    }

    private String formateDateToString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");// 格式化日期
        return sdf.format(date);
    }

    public void printObj(Object[] str, String disp) {
        System.out.print(disp);
        if (str == null) {
            System.out.println("null");
            return;
        }
        for (int i = 0; i < str.length; i++) {
            System.out.print(str[i] + ",");
        }
        System.out.println("");
    }

    private int nameToColumn(String name) {
        int column = -1;
        for (int i = 0; i < name.length(); ++i) {
            int c = name.charAt(i);
            column = (column + 1) * 26 + c - 'A';
        }
        return column;
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
}
