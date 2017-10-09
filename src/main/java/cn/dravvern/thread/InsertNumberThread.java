package cn.dravvern.thread;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.dravvern.base.Dao;
import cn.dravvern.base.UserInfo;
import cn.dravvern.excel.ExcelUtil;
import cn.dravvern.util.Public;

public class InsertNumberThread extends Thread {
    String filename;
    boolean numberType;

    public InsertNumberThread(String filename, boolean numberType) {
        super("号码导入");
        this.filename = filename;
        this.numberType = numberType;
    }

    private List<List<Object[]>> rebuildList(List<Object[]> list) {
        List<List<Object[]>> oLists = new ArrayList<List<Object[]>>();
        List<Object[]> oList = new ArrayList<Object[]>();
        for (int i = 0; i < list.size(); i++) {
            Object[] tmpObjs = list.get(i);
            Object[] newObjs = new Object[5];
            for (int j = 0; j < tmpObjs.length; j++) {
                String str = (String) tmpObjs[0];
                if (numberType) {
                    BigDecimal bigDecimal = null;
                    newObjs[0] = str;
                    newObjs[1] = bigDecimal;
                } else {
                    String nullstr = null;
                    BigDecimal bigDecimal = new BigDecimal(str);
                    newObjs[0] = nullstr;
                    newObjs[1] = bigDecimal;
                }
                newObjs[2] = UserInfo.getInstance().getUserLoginName();
                newObjs[3] = UserInfo.getInstance().getAcceptId();

//                LocalDateTime localDateTime = LocalDateTime.now();
//                ZoneId zone = ZoneId.systemDefault();
//                Instant instant = localDateTime.atZone(zone).toInstant();
//                java.util.Date date = java.util.Date.from(instant);
                newObjs[4] = new Date();
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
        if (this.numberType) {
            Public.addLog("开始导入用户号码(" + filename + ")");
        } else {
            Public.addLog("开始导入用户ID(" + filename + ")");
        }
        try {
            long ct1 = System.currentTimeMillis();
            ExcelUtil excelUtil = new ExcelUtil();
            List<Object[]> list = excelUtil.getSheetList(filename);
            long ct2 = System.currentTimeMillis();
            Public.addLog("文件读取完毕,共" + list.size() + "条记录,用时" + ((ct2 - ct1) / 1000.0) + "秒,准备删除临时表");
            String sql = "delete jk_input where in_create_date<trunc(sysdate) or in_opt_user=? ";
            Object object = UserInfo.getInstance().getUserLoginName();
            int cnt = Dao.getInstance().updateTable(sql, object);
            long ct3 = System.currentTimeMillis();
            Public.addLog("删除临时表" + cnt + "条记录,用时" + ((ct3 - ct2) / 1000.0) + "秒,准备入库");

            List<List<Object[]>> oLists = rebuildList(list);
            ThreadPoolExecutor tPoolExecutor = new ThreadPoolExecutor(8, 15, 200, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<Runnable>(oLists.size()));
            List<Future<Integer>> results = new ArrayList<Future<Integer>>();
            for (int i = 0; i < oLists.size(); i++) {
                InsertNumber insertNumber = new InsertNumber(oLists.get(i));
//                System.out.println("放入线程池：" + i + "|" + oLists.get(i).size());

                Future<Integer> result = tPoolExecutor.submit(insertNumber);
                results.add(result);
            }
            tPoolExecutor.shutdown();
            while (true) {
                TimeUnit.MILLISECONDS.sleep(1000);
//                System.out.println("线程池中线程数目：" + tPoolExecutor.getPoolSize() + "，队列中等待执行的任务数目："
//                        + tPoolExecutor.getQueue().size() + "，已执行完成的任务数目：" + tPoolExecutor.getCompletedTaskCount()
//                        + ",正在执行线程数：" + tPoolExecutor.getActiveCount());
                if (tPoolExecutor.getActiveCount() == 0) {
                    break;
                }
            }
            int commitCnt = 0;
            for (int i = 0; i < results.size(); i++) {
                commitCnt = commitCnt + results.get(i).get();
            }
            long ct4 = System.currentTimeMillis();
            Public.addLog("入库完毕,共" + commitCnt + "条,用时" + ((ct4 - ct3) / 1000.0) + "秒");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

class InsertNumber implements Callable<Integer> {
    private final List<Object[]> oList;

    public InsertNumber(List<Object[]> oList) {
        this.oList = oList;
    }

    public Integer call() throws Exception {
        long ct1 = System.currentTimeMillis();
        String sql = " insert into jk_input (in_acc_nbr, in_user_id, in_opt_user, in_accept_id, in_create_date) "
                + "values (?,?,?,?,?) ";
        int cnt = Dao.getInstance().insertTableBatch(sql, oList);
        long ct2 = System.currentTimeMillis();
        String msg = "共" + cnt + "条记录,入库" + ((ct2 - ct1) / 1000.0) + "秒";
        Public.addLog(msg);
        return cnt;
    }
}
