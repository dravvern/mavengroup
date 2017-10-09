package cn.dravvern.test;

import java.util.Vector;

import cn.dravvern.util.Public;

public class TestThreadPool {
    public static void main(String[] args) {
//        ThreadPoolExecutor executor = new ThreadPoolExecutor(10, 15, 200, TimeUnit.MILLISECONDS,
//                new ArrayBlockingQueue<Runnable>(40));
//        long start = System.currentTimeMillis();
//        System.out.println("activeCountMain1 : " + Thread.activeCount());
//        for (int i = 1; i <= 23; i++) {
//            MySQL mysql = new MySQL(i);
//            executor.execute(mysql);
//            System.out.println("线程池中线程数目：" + executor.getPoolSize() + "，队列中等待执行的任务数目：" 
//                    + executor.getQueue().size() + "，已执行完成的任务数目：" + executor.getCompletedTaskCount() 
//                    + ",正在执行线程数：" + executor.getActiveCount());
//        }
//        executor.shutdown();
//        while (true) {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            System.out.println("线程池中线程数目：" + executor.getPoolSize() + "，队列中等待执行的任务数目：" 
//                    + executor.getQueue().size() + "，已执行完成的任务数目：" + executor.getCompletedTaskCount() 
//                    + ",正在执行线程数：" + executor.getActiveCount());
//            if (executor.getActiveCount() == 0) {
//                break;
//            }
//        }
//        System.out.println("activeCountMain2 : " + Thread.activeCount() + "size=" + Public.getVector().size());
//        long end = System.currentTimeMillis();
//        System.out.println("平均每秒可输出: " + 100000 * 1000.0 / (end - start) + " 条,用" + (end - start)/1000.0 + "秒");
//        
//        String sql = "select b.rn, in_user_id, 用户地市 地市, 用户ID, 用户号码, 套餐ID, 套餐名称, 入网时间, "
//                + "销户时间, 省内客户分群 , 发展渠道ID, 发展渠道业绩归属部门, 用户状态, 停机时间 "
//                + "from st.dw_is_mvuser_di a, "
//                + "(select * from (select xx.*, rownum rn from jk_input xx where rownum<=?) where rn>=?) b "
//                + "where b.in_user_id=a.用户ID(+) and in_opt_user='11' " ;
//        Vector<String> head = Dao.getInstance().selectColumnNameWithArg(sql, 1, 1);
//        List<Vector<Vector<Object>>> list = Public.getList();
//        int j = 0;
//        for (int i = 0; i < list.size(); i++) {
//            j = j + list.get(i).size();
//        }
//        long ct3 = System.currentTimeMillis();
//        System.out.println("读取表头完毕, 用时" + ((ct3 - end) / 1000.0) + "秒,共" + j + "条");
//        
//        try {
//            ExeclWriter.WriteFromList("C:\\Users\\dravvern\\Documents/2.xlsx", head, Public.getList());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        long ct4 = System.currentTimeMillis();
//        System.out.println("文件生成完毕, 用时" + ((ct4 - ct3) / 1000.0) + "秒");
    }
    
}

class MySQL implements Runnable {
//    private final int taskNum;

    public MySQL(int taskNum) {
//        this.taskNum = taskNum;
    }
    
    public void run() {
//        long ct1 = System.currentTimeMillis();
//        String sql = "select b.rn, in_user_id, 用户地市 地市, 用户ID, 用户号码, 套餐ID, 套餐名称, 入网时间, "
//                + "销户时间, 省内客户分群 , 发展渠道ID, 发展渠道业绩归属部门, 用户状态, 停机时间 "
//                + "from st.dw_is_mvuser_di a, "
//                + "(select * from (select xx.*, rownum rn from jk_input xx where rownum<=?) where rn>=?) b "
//                + "where b.in_user_id=a.用户ID(+) and in_opt_user='11' " ;
//        int start = (this.taskNum - 1) * 10000 + 1;
//        int end = this.taskNum * 10000;
//        Vector<Vector<Object>> vector = Dao.getInstance().selectPageNote(sql, start, end);
//        add(vector);
//        long ct2 = System.currentTimeMillis();
//        System.out.println("task " + taskNum + "执行完毕,=" + vector.get(0).get(1).toString() + ".用时" 
//                + (ct2 - ct1)/1000.0 + " 秒" + Public.getVector().size());
    }
    
    public synchronized static void add(Vector<Vector<Object>> vec) {
        Public.addList(vec);
    }
}
