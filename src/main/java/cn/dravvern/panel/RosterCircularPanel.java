package cn.dravvern.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import cn.dravvern.thread.ExportReportThread;
import cn.dravvern.thread.InsertRosterThread;
import cn.dravvern.thread.InsertVisitThread;

public class RosterCircularPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public static JComboBox<String> settcycle;

    public RosterCircularPanel(final JPanel rightPanel) {

        JPanel upPanel = new JPanel();
        upPanel.setPreferredSize(new Dimension(0, 140));
        upPanel.setBorder(BorderFactory.createLineBorder(Color.green));
        rightPanel.add(upPanel, BorderLayout.NORTH);
        JScrollPane downScrollPane = new JScrollPane();
        downScrollPane.setBorder(BorderFactory.createLineBorder(Color.red));
        rightPanel.add(downScrollPane, BorderLayout.CENTER);

        JLabel label3 = new JLabel("账期：");
        JButton chmodjkbutton = new JButton("导入压缩文件");
        JButton backbutton = new JButton("导入拜访记录文件");
        JButton exportbutton = new JButton("导出报告文件");

        settcycle = new JComboBox<String>();
        
        LocalDate curr = LocalDate.now();
        DateTimeFormatter formate = DateTimeFormatter.ofPattern("yyyyMM");
        for (int i = -1; i <= 10; i++) {
            LocalDate dt = curr.minusMonths(i);
            settcycle.addItem(dt.format(formate));
        }
        settcycle.setSelectedIndex(1);

        upPanel.add(label3);
        upPanel.add(settcycle);
        upPanel.add(chmodjkbutton);
        upPanel.add(backbutton);
        upPanel.add(exportbutton);

        chmodjkbutton.addActionListener(new ChmodJkButtonActionListener());
        backbutton.addActionListener(new BackButtonActionListener());
        exportbutton.addActionListener(new ExportButtonActionListener());
        
        
    }

    class ChmodJkButtonActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            
            JFileChooser fcDlg = new JFileChooser();
            fcDlg.setDialogTitle("请选择待导入名单制压缩文件...");
            FileNameExtensionFilter filter = new FileNameExtensionFilter("压缩文件文件(*.zip)", "zip");
            fcDlg.setFileFilter(filter);
            int returnVal = fcDlg.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = fcDlg.getSelectedFile();
//                String zipfilename = file.getAbsolutePath();
                
                InsertRosterThread irt = new InsertRosterThread(file, settcycle.getSelectedItem().toString());
                irt.start();
            }
        }
    }

    class BackButtonActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            JFileChooser fcDlg = new JFileChooser();
            fcDlg.setDialogTitle("请选择待导入拜访记录文件...");
            FileNameExtensionFilter filter = new FileNameExtensionFilter("execl文件(*.xls)", "xls");
            fcDlg.setFileFilter(filter);
            int returnVal = fcDlg.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String filepath = fcDlg.getSelectedFile().getAbsolutePath();
//                System.out.println(filepath);
                
                InsertVisitThread ivt = new InsertVisitThread(filepath);
                ivt.start();
            }
        }
    }

    class ExportButtonActionListener implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("execl文件(*.xls)", "xls");
            chooser.setFileFilter(filter);

            int option = chooser.showSaveDialog(null);
            if (option == JFileChooser.APPROVE_OPTION) {// 假如用户选择了保存
                File file = chooser.getSelectedFile();
                String fname = chooser.getName(file); // 从文件名输入框中获取文件名
                if (fname.indexOf(".xls") == -1) {
                    file = new File(chooser.getCurrentDirectory(), fname + ".xls");
                }
                
                ExportReportThread ert = new ExportReportThread(file.getAbsolutePath());
                ert.start();
            }
        }
    }

}
