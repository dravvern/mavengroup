package cn.dravvern.thread;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import cn.dravvern.excel.ExcelUtil;
import cn.dravvern.excel.ExeclWriter;
import cn.dravvern.util.Public;
import cn.dravvern.util.ZipUtil;

public class FileMergeThread extends Thread {
    private File zipFile;
    private File targetFile;
    

    public FileMergeThread(File zipFile, File targetFile) {
        super("文件合并");
        this.zipFile = zipFile;
        this.targetFile = targetFile;
    }

    public void run() {

        String path = "d:/tmpfile/";

        List<String> filenamelist;
        try {
            Public.addLog("**开始处理 Execl压缩文件" + this.zipFile.getAbsolutePath());
            long ct1 = System.currentTimeMillis();
            filenamelist = ZipUtil.unZipFiles(this.zipFile, path);
            Object[] head = null ;
            
            ThreadPoolExecutor tPoolExecutor = new ThreadPoolExecutor(10, 15, 200, TimeUnit.MILLISECONDS,
                    new ArrayBlockingQueue<Runnable>(filenamelist.size()));
            List<Future<List<Object[]>>> results = new ArrayList<Future<List<Object[]>>>();
            for (int i = 0; i < filenamelist.size(); i++) {
                FileMerge fileMerge = new FileMerge(filenamelist.get(i));
                Future<List<Object[]>> result = tPoolExecutor.submit(fileMerge);
                results.add(result);
            }
            if (filenamelist.size() >0) {
                ExcelUtil excelUtil = new ExcelUtil();
                File file = new File(filenamelist.get(0));
                head = excelUtil.getSheetHead(file);
            }
            tPoolExecutor.shutdown();
            while (true) {
                TimeUnit.MILLISECONDS.sleep(1000);
                System.out.println("线程池中线程数目：" + tPoolExecutor.getPoolSize() + "，队列中等待执行的任务数目："
                        + tPoolExecutor.getQueue().size() + "，已执行完成的任务数目：" + tPoolExecutor.getCompletedTaskCount()
                        + ",正在执行线程数：" + tPoolExecutor.getActiveCount());
                if (tPoolExecutor.getActiveCount() == 0) {
                    break;
                }
            }
            
            List<List<Object[]>> oLists = new ArrayList<List<Object[]>>();
            int commitCnt = 0;
            for (int i = 0; i < results.size(); i++) {
                List<Object[]> oList = results.get(i).get();
                oLists.add(oList);
                commitCnt = commitCnt + oList.size();
            }
            long ct2 = System.currentTimeMillis();
            Public.addLog("读取完毕,共" + commitCnt + "条,用时" + ((ct2 - ct1) / 1000.0) + "秒，正进行合并文件.");
            ExeclWriter.WriteSheet(targetFile, head, oLists);
            long ct3 = System.currentTimeMillis();
            Public.addLog("文件合并完毕,生成" + targetFile.getAbsolutePath() + "时间" + ((ct3 - ct2) / 1000.0) + "秒");
        } catch (Exception e) {
            Public.addLog(e.getMessage());
            e.printStackTrace();
        }
    }
}

class FileMerge implements Callable<List<Object[]>> {
    private final String filename;

    public FileMerge(String filename) {
        this.filename = filename;
    }
    
    private List<Object[]> rebuildList(List<Object[]> list) {
        List<Object[]> oList = new ArrayList<Object[]>();
        for (int i = 0; i < list.size(); i++) {
            Object[] tmpObjs = list.get(i);
            Object[] newObjs = new Object[tmpObjs.length];
            for (int j = 0; j < tmpObjs.length; j++) {
                newObjs[j] = Public.strToBigDecimal(tmpObjs[j]);
            }
            oList.add(newObjs);
        }
        return oList;
    }
    
    public List<Object[]> call() {
        try {
            ExcelUtil excelUtil = new ExcelUtil();
            File file = new File(filename);
            List<Object[]> list = rebuildList(excelUtil.getSheetList(file));
            Public.addLog("文件" + filename + "共" + list.size() + "条.");
            Public.deleteFile(file);
            return list;
        } catch (Exception e) {
            Public.addLog(e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
