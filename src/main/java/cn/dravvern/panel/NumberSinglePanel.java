package cn.dravvern.panel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import cn.dravvern.base.Dao;
import cn.dravvern.dialog.SetExportColumnDialog;
import cn.dravvern.dialog.SetQueryColumnDialog;
import cn.dravvern.mwing.MTextField;
import cn.dravvern.util.AreaInfo;
import cn.dravvern.util.Public;

public class NumberSinglePanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    JTextField tableNameField = new JTextField(20);
    JPanel midPanel = new JPanel();
    List<String> exportList = new ArrayList<String>();
    List<String> QueryList = new ArrayList<String>();
    String partitionName = "";
    
    public NumberSinglePanel(final JPanel rightPanel) {
        setBounds(200, 200, 600, 400);
        JPanel upPanel = new JPanel();
        upPanel.setPreferredSize(new Dimension(0, 36));
        upPanel.setBorder(BorderFactory.createLineBorder(Color.green));
        rightPanel.add(upPanel, BorderLayout.NORTH);
        
        midPanel.setBorder(BorderFactory.createLineBorder(Color.blue));
        rightPanel.add(midPanel, BorderLayout.CENTER);
        FlowLayout fl=new FlowLayout(FlowLayout.LEFT);
        midPanel.setLayout(fl);
        
        JPanel downPanel = new JPanel();
        downPanel.setPreferredSize(new Dimension(0, 200));
        downPanel.setBorder(BorderFactory.createLineBorder(Color.red));
        rightPanel.add(downPanel, BorderLayout.SOUTH);

        JLabel jLabel = new JLabel("表名",10);
        upPanel.add(jLabel);
        tableNameField.setText("st.dw_is_mvuser_mi_201708");
        upPanel.add(tableNameField);

        JButton settingButton = new JButton("查询字段设置");
        settingButton.addActionListener(new SettingExportButtonActionListener());
        upPanel.add(settingButton);

        JButton queryButton = new JButton("检索字段设置");
        queryButton.addActionListener(new SettingQueryButtonActionListener());
        upPanel.add(queryButton);
    }
    
    class SettingExportButtonActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String tableName = tableNameField.getText();
            if (tableName == null || tableName.length() <= 0) {
                JOptionPane.showMessageDialog(null, "请先设置查询的表名", "友情提示", JOptionPane.INFORMATION_MESSAGE);
                return ;
            }

            SetExportColumnDialog secdialog = new SetExportColumnDialog(tableName);
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int w = 850;
            int h = 400;
            int x = (screenSize.width - w) / 2;
            int y = (screenSize.height - h) / 2;
            secdialog.setBounds(x, y, w, h);
            secdialog.setVisible(true);
            exportList = secdialog.getReturnList();
            System.out.println(Public.listToString(exportList));
        }
    }
    
    class SettingQueryButtonActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String tableName = tableNameField.getText();
            if (tableName == null || tableName.length() <= 0) {
                JOptionPane.showMessageDialog(null, "请先设置查询的表名", "友情提示", JOptionPane.INFORMATION_MESSAGE);
                return ;
            }

            SetQueryColumnDialog sqcdialog = new SetQueryColumnDialog(tableName);
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int w = 450;
            int h = 400;
            int x = (screenSize.width - w) / 2;
            int y = (screenSize.height - h) / 2;
            sqcdialog.setBounds(x, y, w, h);
            sqcdialog.setVisible(true);
            QueryList = sqcdialog.getReturnList();
            
            System.out.println(Public.listToString(QueryList));
            if (QueryList != null && QueryList.size() > 0 ) {
                midPanel.removeAll();
                updateMidPanel(QueryList);
            }
        }
    }

    public void updateMidPanel(List<String> list) {
        for (int i = 0; i < list.size() / 2; i++) {
            JLabel jLabel = new JLabel(list.get(i * 2));
            MTextField jTextField = new MTextField(list.get(i * 2 + 1));
            jTextField.setName(list.get(i * 2));
            jTextField.setColumns(8);
            jLabel.setPreferredSize(new Dimension(80, 26));
            midPanel.add(jLabel);
            midPanel.add(jTextField);
        }
        JButton queryButton = new JButton("查询");
        queryButton.addActionListener(new QueryButtonActionListener());
        midPanel.add(queryButton);
        SwingUtilities.updateComponentTreeUI(midPanel);
    }
    
    class QueryButtonActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            String wheresql = "where ";
            int count = midPanel.getComponentCount();
            int j = 0;
            for (int i = 0; i < count; i++) {
                Object obj = midPanel.getComponent(i);
                if (obj instanceof MTextField) {
                    MTextField mTextField = (MTextField) obj;
                    if (mTextField.getText() != null && mTextField.getText().length() > 0) {
                        if (j > 0) {
                            wheresql = wheresql + "and ";
                        }
                        wheresql = addWhereSql(wheresql, mTextField);
                        j++;
                        if (wheresql.length() == 0) {
                            break;
                        }
                    }
                }
            }
            String sql = "select " + Public.listToString(exportList) + " from " + tableNameField.getText() + wheresql;
            System.out.println(sql);
        }
    }
    
    public void getPartitionName(String text, String name) {
        if ("用户地市".equals(name)) {
            AreaInfo areaInfo = Dao.getInstance().getAreaByName(text);
            if (areaInfo != null) {
                partitionName = "partition(P_" + areaInfo.getAreaqh() + ")" ;
            }
        }
    }
    
    public String addWhereSql(String wheresql, MTextField mTextField) {
        String text = mTextField.getText();
        String name = mTextField.getName();
        String type = mTextField.getDatatype();
//        System.out.println(name + "|" + text + "|" + type + " ");
        if ("NUMBER".equals(type)) {
            wheresql = wheresql + name + "=" + text + " ";
        } else if ("VARCHAR2".equals(type) || "CHAR".equals(type)) {
            wheresql = wheresql + name + "='" + text + "' ";
            getPartitionName(text, name);
        } else if ("DATE".equals(type)) {
            switch (text.length()) {
            case 4:
                wheresql = wheresql + "to_char(" + name + ",'yyyy')='" + text + "' ";
                break;
                
            case 6:
                wheresql = wheresql + "to_char(" + name + ",'yyyymm')='" + text + "' ";
                break;
                
            case 8:
                wheresql = wheresql + "to_char(" + name + ",'yyyymmdd')='" + text + "' ";
                break;

            case 14:
                wheresql = wheresql + "to_char(" + name + ",'yyyymmddhh24miss')='" + text + "' ";
                break;
                
            default:
                String errmsg = name + "格式不对，仅支持yyyy或yyyymm或yyyymmdd 格式";
                JOptionPane.showMessageDialog(null, errmsg, "友情提示", JOptionPane.ERROR_MESSAGE);
                wheresql = "";
                break;
            }
        }
        return wheresql;
    }
}
