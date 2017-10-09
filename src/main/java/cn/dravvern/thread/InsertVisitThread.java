package cn.dravvern.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.dravvern.base.Dao;
import cn.dravvern.excel.ExcelUtil;
import cn.dravvern.util.Public;

public class InsertVisitThread extends Thread {
    String filename;

    public InsertVisitThread(String filename) {
        super("拜访记录导入");
        this.filename = filename;
    }
    
    private List<List<Object[]>> rebuildList(List<Object[]> list) {
        List<List<Object[]>> oLists = new ArrayList<List<Object[]>>();
        List<Object[]> oList = new ArrayList<Object[]>();
        for (int i = 0; i < list.size(); i++) {
            Object[] tmpObjs = list.get(i);
            Object[] newObjs = new Object[19];
            for (int j = 0; j < tmpObjs.length; j++) {
                newObjs[0] = tmpObjs[0];
                newObjs[1] = tmpObjs[1];
                newObjs[2] = tmpObjs[2];
                newObjs[3] = tmpObjs[3];
                newObjs[4] = tmpObjs[4];
                newObjs[5] = tmpObjs[5];
                newObjs[6] = Public.strToDate(tmpObjs[6]);
                newObjs[7] = Public.strToDate(tmpObjs[7]);
                newObjs[8] = tmpObjs[8];
                newObjs[9] = tmpObjs[9];
                
                newObjs[10] = tmpObjs[10];
                newObjs[11] = tmpObjs[11];
                newObjs[12] = tmpObjs[12];
                newObjs[13] = tmpObjs[13];
                newObjs[14] = tmpObjs[14];
                newObjs[15] = Public.strToDate(tmpObjs[15]);
                newObjs[16] = tmpObjs[16];
                newObjs[17] = Public.strToDate(tmpObjs[17]);
                newObjs[18] = tmpObjs[18];
            }
            oList.add(newObjs);
            if ((i + 1) % Public.getExportpagenum() == 0) {
                oLists.add(oList);
                oList = new ArrayList<Object[]>();
            }
        }
        oLists.add(oList);
        return oLists;
    }
    
    public void run() {
        try {
            Public.addLog("开始导入拜访记录(" + filename + ")");
            long ct1 = System.currentTimeMillis();
            ExcelUtil excelUtil = new ExcelUtil();
            List<Object[]> list = excelUtil.getSheetList(filename);
            long ct2 = System.currentTimeMillis();
            Public.addLog("文件读取完毕,共" + list.size() + "条记录,用时" + ((ct2 - ct1) / 1000.0) + "秒,准备入库");

            List<List<Object[]>> oLists = rebuildList(list);
            ThreadPoolExecutor executor = new ThreadPoolExecutor(8, 15, 200, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<Runnable>(oLists.size()));
            for (int i = 0; i < oLists.size(); i++) {
                InsertVisit iv = new InsertVisit(oLists.get(i));
                executor.execute(iv);
            }
            executor.shutdown();
            while (true) {
                sleep(1000);
                System.out.println("线程池中线程数目：" + executor.getPoolSize() + "，队列中等待执行的任务数目：" + executor.getQueue().size()
                        + "，已执行完成的任务数目：" + executor.getCompletedTaskCount() + ",正在执行线程数：" + executor.getActiveCount());
                if (executor.getActiveCount() == 0) {
                    break;
                }
            }
            long ct3 = System.currentTimeMillis();
            Public.addLog("入库完毕, 用时" + ((ct3 - ct2) / 1000.0) + "秒");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class InsertVisit implements Runnable {
    private final List<Object[]> list;

    public InsertVisit(List<Object[]> list) {
        this.list = list;
    }
    
    public void run() {
        long ct1 = System.currentTimeMillis();
        String sql = " insert into jk_nl_visit ( "
                + "visit_code, nl_name, cust_type, area_name, county_name, "
                + "team_name, pre_eff_time, pre_exp_time, visit_target, visit_content, "
                + "visit_person, visit_tel, visit_staff, visit_type, visit_status, "
                + "visit_time, visit_addr, create_time, create_staff) "
                + "values ( "
                + "?,?,?,?,?,?,?,?,?,?, "
                + "?,?,?,?,?,?,?,?,?) " ;
        int cnt = Dao.getInstance().insertTableBatch(sql, list);
        long ct2 = System.currentTimeMillis();
        String msg = "共" + cnt + "条记录,入库" + ((ct2 - ct1) / 1000.0) + "秒";
        Public.addLog(msg);
    }
}
