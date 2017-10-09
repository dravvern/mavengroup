package cn.dravvern.thread;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.dravvern.base.Dao;
import cn.dravvern.excel.ExcelUtil;
import cn.dravvern.util.Public;
import cn.dravvern.util.ZipUtil;

public class InsertRosterThread extends Thread {
    private File file;
    private String settcycle;

    public InsertRosterThread(File file, String settcycle) {
        super("名单制导入");
        this.file = file;
        this.settcycle = settcycle;
    }

    public void run() {

        String path = "f:/tmpfile/";

        List<String> filenamelist;
        try {
            Public.addLog("******开始处理 名单制压缩文件****");
            long ct1 = System.currentTimeMillis();
            filenamelist = ZipUtil.unZipFiles(this.file, path);
            
            ThreadPoolExecutor executor = new ThreadPoolExecutor(8, 15, 200, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<Runnable>(filenamelist.size()));
            
            for (int i = 0; i < filenamelist.size(); i++) {
                InsertRoster ir = new InsertRoster(filenamelist.get(i), settcycle);
                executor.execute(ir);
            }
            executor.shutdown();
            while (true) {
                TimeUnit.MILLISECONDS.sleep(1000);
                System.out.println("线程池中线程数目：" + executor.getPoolSize() + "，队列中等待执行的任务数目：" 
                        + executor.getQueue().size() + "，已执行完成的任务数目：" + executor.getCompletedTaskCount() 
                        + ",正在执行线程数：" + executor.getActiveCount());
                if (executor.getActiveCount() == 0) {
                    break;
                }
            }
            long ct2 = System.currentTimeMillis();
            Public.addLog("***************数据导入完毕*****************总时间" + ((ct2 - ct1) / 1000.0) + "秒");
        } catch (IOException e) {
            Public.addLog(e.getMessage());
            e.printStackTrace();
        } catch (InterruptedException e) {
            Public.addLog(e.getMessage());
            e.printStackTrace();
        }
    }
}

class InsertRoster implements Runnable {
    private final String filename;
    private final String settcycle;

    public InsertRoster(String filename, String settcycle) {
        this.filename = filename;
        this.settcycle = settcycle;
    }
    
    public List<Object[]> rebuildList(List<Object[]> list, String settcycle) {
        List<Object[]> oList = new ArrayList<Object[]>();
        for (int i = 0; i < list.size(); i++) {
            Object[] tmpObjs = list.get(i);
            Object[] newObjs = new Object[24];
            for (int j = 0; j < tmpObjs.length; j++) {
                newObjs[0] = settcycle;
                newObjs[1] = tmpObjs[0];
                newObjs[2] = tmpObjs[1];
                newObjs[3] = tmpObjs[2];
                newObjs[4] = tmpObjs[3];
                newObjs[5] = tmpObjs[4];
                newObjs[6] = tmpObjs[5];
                newObjs[7] = tmpObjs[6];
                newObjs[8] = tmpObjs[7];
                newObjs[9] = tmpObjs[8];
                
                newObjs[10] = tmpObjs[9];
                newObjs[11] = tmpObjs[10];
                newObjs[12] = tmpObjs[11];
                newObjs[13] = tmpObjs[12];
                newObjs[14] = tmpObjs[13];
                newObjs[15] = tmpObjs[14];
                newObjs[16] = tmpObjs[15];
                newObjs[17] = tmpObjs[16];
                newObjs[18] = tmpObjs[17];
                newObjs[19] = tmpObjs[18];
                
                newObjs[20] = Public.strToDate(tmpObjs[19]);
                newObjs[21] = tmpObjs[20];
                newObjs[22] = tmpObjs[21];
                newObjs[23] = Public.strToDate(tmpObjs[22]);
            }
            oList.add(newObjs);
        }
        return oList;
    }
    
    public void run() {
        try {
            Public.addLog("开始处理文件" + filename);
            long ct1 = System.currentTimeMillis();
            ExcelUtil excelUtil = new ExcelUtil();
            List<Object[]> list = excelUtil.getSheetList(filename);
            List<Object[]> oList = rebuildList(list, settcycle);
            String sql = " insert into jk_nl_info (sett_cycle, nl_code, nl_name, group_code, team_name, "
                    + "respon_name, link_name, link_tel, staff_num, gps_addr, "
                    + "cust_type, cust_status, area_name, county_name, nl_type_l1, "
                    + "nl_type_l2, nl_rel, cust_property, cust_level, expand_type, "
                    + "promotion_time, nl_status, audit_remark, audit_time) " 
                    + "values ( " + "?,?,?,?,?,?,?,?,?,?, ?,?,?,?,?,?,?,?,?,?, ?,?,?,?) ";
            long ct2 = System.currentTimeMillis();
            int cnt = Dao.getInstance().insertTableBatch(sql, oList);
            long ct3 = System.currentTimeMillis();
            String msg = "处理文件" + filename + ",共" + cnt + "条记录,读取文件用时" + ((ct2 - ct1) / 1000.0) 
                    + "秒, 入库" + ((ct3 - ct2) / 1000.0) + "秒";
            Public.addLog(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
