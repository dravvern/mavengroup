package cn.dravvern.util;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

import javax.swing.JTextArea;

public class Public {
    private static final int EXPORTPAGENUM = 10000;
    private static JTextArea LOGTEXT = null;
    private static String queryTable = "st.dw_is_mvuser_di";

    public static int getExportpagenum() {
        return EXPORTPAGENUM;
    }

    private static Vector<Vector<Object>> vector = new Vector<Vector<Object>>();
    private static List<Vector<Vector<Object>>> list = new ArrayList<Vector<Vector<Object>>>();

    public static List<Vector<Vector<Object>>> getList() {
        return list;
    }

    public static void addList(Vector<Vector<Object>> vec) {
        list.add(vec);
    }

    public static Vector<Vector<Object>> getVector() {
        return vector;
    }

    public static void addResult(Vector<Vector<Object>> vec) {
        for (int i = 0; i < vec.size(); i++) {
            vector.add(vec.get(i));
        }
    }

    public static String getQueryTable() {
        return queryTable;
    }

    public static void setQueryTable(String queryTable) {
        Public.queryTable = queryTable;
    }

    public static int toInt(Object o) {
        return toInt(String.valueOf(o));
    }

    public static int toInt(String s) {
        try {
            return Integer.parseInt(s);
        } catch (Exception e) {
            System.out.println("string to int. errmsg=" + e.getMessage());
            return -1;
        }
    }

    public static void setLogFrame(JTextArea textArea) {
        LOGTEXT = textArea;
    }

    public static void addLog(String s) {
        String msg = Thread.currentThread().getName() + " " + LocalDateTime.now().toString() + " " + s
                + "\n";
        System.out.print(msg);
        if (LOGTEXT != null)
            LOGTEXT.append(msg);
    }

    public static List<List<String[]>> splitList(List<String[]> list, int len) {
        if (list == null || list.size() == 0 || len < 1) {
            return null;
        }

        List<List<String[]>> result = new ArrayList<List<String[]>>();

        int size = list.size();
        int count = (size + len - 1) / len;

        for (int i = 0; i < count; i++) {
            List<String[]> subList = list.subList(i * len, ((i + 1) * len > size ? size : len * (i + 1)));
            result.add(subList);
        }
        return result;
    }

    public static boolean cheakIsRepeat(int[] array) {
        HashSet<Integer> hashSet = new HashSet<Integer>();
        for (int i = 0; i < array.length; i++) {
            hashSet.add(array[i]);
        }
        if (hashSet.size() == array.length) {
            return true;
        } else {
            return false;
        }
    }

    public static String listToString(List<String> stringList) {
        if (stringList == null) {
            return "";
        }
        StringBuilder result = new StringBuilder();
        boolean flag = false;
        for (String string : stringList) {
            if (flag) {
                result.append(",");
            } else {
                flag = true;
            }
            result.append(string);
        }
        return result.toString();
    }
    
    public static Vector<String> stringToVector(String[] str) {
        if(str == null) 
            return null;
        Vector<String> vector = new Vector<String>();
        for (int i = 0; i < str.length; i++) {
            vector.add(str[i]);
        }
        return vector;
    }
    
    public static void printStr(String[] str, String disp) {
        System.out.print(disp);
        if(str == null) { 
            System.out.println("null");
            return ;
        }
        for (int i = 0; i < str.length; i++) {
            System.out.print(str[i] + ",");
        }
        System.out.println("");
    }
    
    public static void printInts(int[] str, String disp) {
        System.out.print(disp);
        if(str == null) { 
            System.out.println("null");
            return ;
        }
        for (int i = 0; i < str.length; i++) {
            System.out.print(str[i] + ",");
        }
        System.out.println("");
    }
    
    public static void printObjects(Object[] objects) {
        if (objects == null) {
            System.out.println("null");
            return;
        }
        for (int i = 0; i < objects.length; i++) {
            System.out.print(objects[i] + ",");
        }
        System.out.println("");
    }
    
    public static Object strToDate(Object object) {
        if (object == null) {
            return null;
        }
        final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            String str = (String) object;
            ParsePosition pos = new ParsePosition(0);
            Date date = formatter.parse(str, pos);
            return date;
        } catch (Exception e) {
            return null;
        }
    }
    
    public static String[] objectsToStrings(Object[] objects) {
        String[] strs = new String[objects.length];
        System.arraycopy(objects, 0, strs, 0, objects.length);
        return strs;
    }
    
    public static String intsToString(int[] is) {
        StringBuffer value = new StringBuffer();
        for (int i = 0; i < is.length; i++) {
            if (i != 0) {
                value.append(',');
            }
            value.append(is[i]);
        }
        return value.toString();
    }
    
    public static int[] stringToInts(String str) {
        if(str == null || str.length() == 0) {
            return null;
        }
        String[] strs = str.split(",");
        int[] is = new int[strs.length];
        for (int i = 0; i < strs.length; i++) {
            int value = toInt(strs[i]);
            value = value == -1 ? 0 : value; 
            is[i] = value;
        }
        return is;
    }
    
    public static Object strToBigDecimal(Object object) {
        if (object == null) {
            return null;
        }
        try {
            String str = (String) object;
            BigDecimal bigDecimal = new BigDecimal(str);
            return bigDecimal;
        } catch (Exception e) {
            return object;
        }
    }
    
    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        return deleteFile(file);
    }
    
    public static boolean deleteFile(File file) {
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
