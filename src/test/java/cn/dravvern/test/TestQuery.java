package cn.dravvern.test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import cn.dravvern.base.Dao;

public class TestQuery {
    
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

    public static void main(String[] args) {
        String sql = "select * from (select xx.*, rownum rn from jk_input xx where rownum<=?) where rn>=? ";
        sql = "select xx.*, rownum rn from jk_input xx where rownum<5" ;
        Map<String, List<Object[]>> map = Dao.getInstance().QueryResult(sql);
        
        List<Object[]> head = Dao.getInstance().QueryHead(map);
        List<Object[]> list = Dao.getInstance().QuerySomeNote(map);
        
        printObjectList(list);
        printObjectList(head);
//        System.out.println(head.toString());
//        System.out.println(list.size());
        
        BigDecimal bg = new BigDecimal("2232323");
//        Object object = bg;
        Object[] objects = new Object[100];
        objects[0] = bg;
        
        System.out.println(objects[0]);

    }

}
