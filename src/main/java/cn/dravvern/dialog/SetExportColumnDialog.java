package cn.dravvern.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import cn.dravvern.base.Dao;
import cn.dravvern.base.UserInfo;
import cn.dravvern.mwing.MComboBox;
import cn.dravvern.util.Public;

public class SetExportColumnDialog extends JDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private List<String> list = new ArrayList<String>();
    private String tableName;
    private Object index;
    private boolean haveConfig = true;
    
    public SetExportColumnDialog(String tableName) {
        super();
        this.tableName = tableName;
        setModal(true);
        setTitle("自定义查询字段");
        setBounds(100, 100, 850, 400);
        FlowLayout fl = new FlowLayout(FlowLayout.LEFT);
        
        String headsql = "select * from " + tableName + " where 1=0 ";
        String cfgsql = "select column_index from tg_user_query where user_login_name=? and upper(table_name)=? "
                + "and config_type=3 ";
        String defaultsql = "select column_index from tg_user_query where user_login_name='*' and upper(table_name)=? "
                + "and config_type=3 ";
        Object[] heads = Dao.getInstance().QueryHead(headsql).get(0);
        index = Dao.getInstance().QueryOnlyValue(cfgsql, UserInfo.getInstance().getUserLoginName(),
                tableName.toUpperCase());
        if (index == null) {
            haveConfig = false;
            index = Dao.getInstance().QueryOnlyValue(defaultsql, tableName.toUpperCase());
        }
        
        final JPanel columnPanel = new JPanel();
        columnPanel.setLayout(fl);
        JScrollPane leftScrollPane = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        leftScrollPane.setViewportView(columnPanel);
        columnPanel.setPreferredSize(new Dimension(220, 36 + 31 * heads.length / 3));
        columnPanel.revalidate();
        
        leftScrollPane.setBorder(BorderFactory.createLineBorder(Color.red));
        getContentPane().add(leftScrollPane, BorderLayout.CENTER);
        
        initalCondition(columnPanel, heads, index);
        
        final JPanel rightPanel = new JPanel();
        rightPanel.setPreferredSize(new Dimension(140, 0));
        rightPanel.setBorder(BorderFactory.createLineBorder(Color.green));
        getContentPane().add(rightPanel, BorderLayout.EAST);

        final JButton addColumnButton = new JButton("增加导出字段");
        addColumnButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addCondition(columnPanel, heads);
            }
        });
        final JButton saveButton = new JButton("保存设置");
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean b = getSelectComponent(columnPanel);
                if (b) {
                    dispose();
                }
            }
        });
        rightPanel.add(addColumnButton);
        rightPanel.add(saveButton);
        
        this.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }
    
    public boolean getSelectComponent(JPanel panel) {
        int count = panel.getComponentCount();
        boolean ret = true;
        int j = 0;
        int[] saveIndex = new int[count];
        for (int i = 0; i < count; i++) {
            Object obj = panel.getComponent(i);
            if (obj instanceof MComboBox) {
                MComboBox mcb = (MComboBox) obj;
                String item = mcb.getSelectedItem().toString();
                list.add(item);
                saveIndex[j] = mcb.getSelectedIndex();
                j++;
            }
        }
        int[] clone = new int[j];
        System.arraycopy(saveIndex, 0, clone, 0, j);
        saveIndex = clone;
        if (!Public.cheakIsRepeat(saveIndex)) {
            ret = false;
            list.clear();
            JOptionPane.showMessageDialog(null, "字段重复,请重新设置", "友情提示", JOptionPane.INFORMATION_MESSAGE);
        }
        String str = Public.intsToString(saveIndex);
        if(haveConfig) {
            String sql = "update tg_user_query set column_index=? where user_login_name=? and table_name=? "
                    + "and config_type=3 ";
            Dao.getInstance().updateTable(sql, str, UserInfo.getInstance().getUserLoginName(), this.tableName);
        } else {
            String sql = "insert into tg_user_query(user_login_name,table_name,column_index, config_type) "
                    + "values(?,?,?, 3)";
            Dao.getInstance().updateTable(sql, UserInfo.getInstance().getUserLoginName(), this.tableName, str);
        }
        return ret;
    }

    private void initalCondition(JPanel columnPanel, Object[] objects, Object index) {
        int length = 0;
        String[] strings = Public.objectsToStrings(objects);
        int[] is = Public.stringToInts((String) index);
        if (is == null || is.length == 0) {
            length = objects.length;
        } else {
            length = is.length;
        }
        for (int i = 0; i < length; i++) {
            MComboBox mcb ;
            if (is == null || is.length == 0) {
                mcb = new MComboBox(strings, i);
            } else {
                mcb = new MComboBox(strings, is[i]);
            }
            JButton button1 = new JButton("删除");
            button1.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    columnPanel.remove(mcb);
                    columnPanel.remove(button1);
                    columnPanel.updateUI();
                    columnPanel.repaint();
                }
            });
            columnPanel.add(mcb);
            columnPanel.add(button1);
        }
    }
    
    private void addCondition(JPanel columnPanel, Object[] objects) {
        String[] strings = Public.objectsToStrings(objects);
        MComboBox mcb = new MComboBox(strings);
        JButton button1 = new JButton("删除");
        button1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                columnPanel.remove(mcb);
                columnPanel.remove(button1);
                columnPanel.updateUI();
                columnPanel.repaint();
            }
        });
        columnPanel.add(mcb);
        columnPanel.add(button1);
        columnPanel.updateUI();
        columnPanel.repaint();
    }
    
    public List<String> getReturnList() {
        return list;
    }
}
