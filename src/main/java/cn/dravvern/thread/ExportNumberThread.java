package cn.dravvern.thread;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.dravvern.base.Dao;
import cn.dravvern.util.Public;

public class ExportNumberThread extends Thread {
    private String filename;
    private String sql;
    private String cntsql;
    
    public ExportNumberThread(String filename, String sql, String cntsql) {
        super("号码信息导出");
        this.filename = filename;
        this.sql = sql;
        this.cntsql = cntsql;
    }
    
    public void run() {
        
        Public.addLog("开始导出用户资料到(" + this.filename +")");
        long ct1 = System.currentTimeMillis();
        int cnt = Dao.getInstance().getNumberCnt(cntsql);
        
        int len = Public.getExportpagenum();
        long ct2 = System.currentTimeMillis();
        Public.addLog("用时" + ((ct2 - ct1) / 1000.0) + "秒" + "共有" + cnt + "条记录,开始查询");
        
        int page = (cnt + len - 1) / len;
        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 15, 200, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<Runnable>(page));
        for (int i = 0; i < page; i++) {
            ExportNumber en = new ExportNumber(this.sql, i + 1);
            executor.execute(en);
        }
        executor.shutdown();
        int k = 0;
        while (true) {
            try {
                sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("线程池中线程数目：" + executor.getPoolSize() + "，队列中等待执行的任务数目：" 
                    + executor.getQueue().size() + "，已执行完成的任务数目：" + executor.getCompletedTaskCount() 
                    + ",正在执行线程数：" + executor.getActiveCount());
            
            k++;
            if (k > 10) {
                Public.addLog("正执行数量:" + executor.getPoolSize() + "，等待执行数量：" 
                        + executor.getQueue().size() + "，已完成的数量：" + executor.getCompletedTaskCount());
                k = 0;
            }
            
            if (executor.getActiveCount() == 0) {
                break;
            }
        }
        List<Vector<Vector<Object>>> list = Public.getList();
        int j = 0;
        for (int i = 0; i < list.size(); i++) {
            j = j + list.get(i).size();
        }
        long ct3 = System.currentTimeMillis();
        Public.addLog("读取数据库完毕, 用时" + ((ct3 - ct2) / 1000.0) + "秒,共" + j + "条,准备写入文件");
        
//        try {
//            ExeclWriter.WriteSheet(new File(this.filename), head, Public.getList());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        long ct4 = System.currentTimeMillis();
        Public.addLog("文件生成完毕, 用时" + ((ct4 - ct3) / 1000.0) + "秒,生成完毕");
    }
}

class ExportNumber implements Runnable {
    private final int taskNum;
    private final String sql;
    private final int pageNum;

    public ExportNumber(String sql, int taskNum) {
        this.taskNum = taskNum;
        this.sql = sql;
        this.pageNum = Public.getExportpagenum();
    }
    
    public void run() {
        int start = (this.taskNum - 1) * pageNum + 1;
        int end = this.taskNum * pageNum;
        
        long ct1 = System.currentTimeMillis();
        List<Object[]> list = Dao.getInstance().QuerySomeNote(sql, end, start);
        add(list);
        long ct2 = System.currentTimeMillis();
        String msg = "读取" + list.size() + "条记录," + ((ct2 - ct1) / 1000.0) + "秒";
        Public.addLog(msg);
    }
    
    public synchronized static void add(List<Object[]> list) {
//        Public.addList(vector);
    }
}

