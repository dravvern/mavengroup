package cn.dravvern.thread;

import java.io.IOException;

import cn.dravvern.base.ExportNLInfo;
import cn.dravvern.frame.MainFrame;

public class ExportReportThread extends Thread {
    
    String filename;
    
    public ExportReportThread(String filename) {
        super("报表导出");
        this.filename = filename;
    }
    
    public void run() {
        
        try {
            MainFrame.processResult.append("(" + Thread.currentThread().getName() + ")开始生成文件(" 
                    + this.filename + ")\n");
            long ct1 = System.currentTimeMillis();
            
            ExportNLInfo.writeExecl(filename);
            
            long ct3 = System.currentTimeMillis();
            MainFrame.processResult.append("(" + Thread.currentThread().getName() + ")名单报表生成完毕,共" 
                    + ((ct3 - ct1) / 1000.0) + "秒\n");
        } catch (IOException e) {
            MainFrame.processResult.append("(" + Thread.currentThread().getName() + ")" + e.getMessage() + "\n");
            e.printStackTrace();
        }
    }

}
