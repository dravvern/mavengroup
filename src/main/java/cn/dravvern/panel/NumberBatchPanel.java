package cn.dravvern.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.filechooser.FileNameExtensionFilter;

import cn.dravvern.base.Dao;
import cn.dravvern.base.UserInfo;
import cn.dravvern.dialog.QueryConditionDialog;
import cn.dravvern.thread.ExportNumberThread;
import cn.dravvern.thread.InsertNumberThread;
import cn.dravvern.util.Public;

public class NumberBatchPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    public static JTextArea procresult;
    public static JComboBox<String> settcycle;
    public static JRadioButton jrb1, jrb2;

    public NumberBatchPanel(final JPanel rightPanel) {
        setBounds(200, 200, 600, 400);
        JPanel upPanel = new JPanel();
        upPanel.setPreferredSize(new Dimension(0, 140));
        upPanel.setBorder(BorderFactory.createLineBorder(Color.green));
        JPanel downPanel = new JPanel();

        rightPanel.add(upPanel, BorderLayout.NORTH);
        downPanel.setBorder(BorderFactory.createLineBorder(Color.red));
        rightPanel.add(downPanel);

        ButtonGroup bg = new ButtonGroup();// 创建按钮组
        jrb1 = new JRadioButton("用户号码", true);
        jrb2 = new JRadioButton("用户ID");
        bg.add(jrb1);
        bg.add(jrb2);

        JLabel label3 = new JLabel("选择导入用户号码或者用户ID");
        JButton impUserButton = new JButton("导入用户");

        settcycle = new JComboBox<String>();
        settcycle.addItem("201709");
        LocalDate curr = LocalDate.now();
        DateTimeFormatter formate = DateTimeFormatter.ofPattern("yyyyMM");
        for (int i = 0; i <= 10; i++) {
            LocalDate dt = curr.minusMonths(i);
            settcycle.addItem(dt.format(formate));
        }
        upPanel.add(settcycle);
        upPanel.add(label3);
        upPanel.add(jrb1);
        upPanel.add(jrb2);
        upPanel.add(impUserButton);

        impUserButton.addActionListener(new ImpUserButtonActionListener());

        JButton settingButton = new JButton("导出字段设置");
        settingButton.addActionListener(new SettingButtonActionListener());
        upPanel.add(settingButton);

        JButton queryButton = new JButton("导出文件");
        queryButton.addActionListener(new QueryButtonActionListener());
        upPanel.add(queryButton);
    }

    class SettingButtonActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            QueryConditionDialog dpDialog = new QueryConditionDialog();
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int w = 550;
            int h = 400;
            int x = (screenSize.width - w) / 2;
            int y = (screenSize.height - h) / 2;
            dpDialog.setBounds(x, y, w, h);
            dpDialog.setVisible(true);
        }
    }

    class QueryButtonActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String columnsql = "";
            String tableName = Public.getQueryTable();
            String sql = "";
            if (columnsql == null || columnsql.length() <= 0) {
                JOptionPane.showMessageDialog(null, "请先进行导出字段设置", "友情提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int numberType = Dao.getInstance().getNumberType();
            if (numberType == 0) {
                JOptionPane.showMessageDialog(null, "请先导入号码", "友情提示", JOptionPane.INFORMATION_MESSAGE);
                return;
            } else if (numberType == 1) {
                sql = "select in_acc_nbr, " + columnsql + " from " + tableName + " a, "
                        + "(select * from (select xx.*, rownum rn from jk_input xx where rownum<=?) where rn>=?) b "
                        + "where b.in_acc_nbr=a.用户号码(+) ";
            } else if (numberType == 2) {
                sql = "select b.rn, in_user_id, " + columnsql + " from " + tableName
                        + " a, jk_input b where b.in_user_id=a.用户ID(+) ";
                sql = "select b.rn, in_user_id, " + columnsql + " from " + tableName + " a, "
                        + "(select * from (select xx.*, rownum rn from jk_input xx where rownum<=?) where rn>=?) b "
                        + "where b.in_user_id=a.用户ID(+) ";
            }
            sql = sql + "and in_opt_user='" + UserInfo.getInstance().getUserLoginName() + "' ";
            System.out.println(sql);
            String cntsql = "select count(*) from st.dw_is_mvuser_di a, jk_input b "
                    + "where b.in_user_id=a.用户ID(+) and in_opt_user='11' " ;

            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Execl文件(*.xlsx)", "xlsx");
            chooser.setFileFilter(filter);

            int option = chooser.showSaveDialog(null);
            if (option == JFileChooser.APPROVE_OPTION) {// 假如用户选择了保存
                File file = chooser.getSelectedFile();
                String fname = chooser.getName(file); // 从文件名输入框中获取文件名
                if (fname.indexOf(".xlsx") == -1) {
                    file = new File(chooser.getCurrentDirectory(), fname + ".xlsx");
                }

                ExportNumberThread enit = new ExportNumberThread(file.getAbsolutePath(), sql, cntsql);
                enit.start();
            }

        }
    }

    class ImpUserButtonActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            JFileChooser fcDlg = new JFileChooser();
            boolean bjrb1 = jrb1.isSelected();
            if (bjrb1) {
                JOptionPane.showMessageDialog(null, "号码查询速度较慢且有重入网情况，尽量导入用户ID进行查询", "友情提示",
                        JOptionPane.INFORMATION_MESSAGE);
                fcDlg.setDialogTitle("请选择导入用户号码文件...");
            } else {
                fcDlg.setDialogTitle("请选择导入用户ID文件...");
            }
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Execl文件(*.xls, *.xlsx, *.csv)", "xls", "xlsx",
                    "csv");
            fcDlg.setFileFilter(filter);
            int returnVal = fcDlg.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                String filepath = fcDlg.getSelectedFile().getAbsolutePath();

                InsertNumberThread ist = new InsertNumberThread(filepath, bjrb1);
                ist.start();
            }
        }
    }

}
