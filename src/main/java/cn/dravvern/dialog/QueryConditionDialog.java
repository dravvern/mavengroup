package cn.dravvern.dialog;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class QueryConditionDialog extends JDialog {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public QueryConditionDialog() {
        super();
        setModal(true);
        setTitle("自定义查询结果");
        setBounds(100, 100, 510, 400);

        final JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(90, 0));
        leftPanel.setBorder(BorderFactory.createLineBorder(Color.green));
        getContentPane().add(leftPanel, BorderLayout.WEST);

        final JPanel midPanel = new JPanel();
        midPanel.setBorder(BorderFactory.createLineBorder(Color.blue));
        getContentPane().add(midPanel, BorderLayout.CENTER);

        final JPanel rightPanel = new JPanel();
        rightPanel.setPreferredSize(new Dimension(140, 0));
        rightPanel.setBorder(BorderFactory.createLineBorder(Color.red));
        getContentPane().add(rightPanel, BorderLayout.EAST);

        leftPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        JComboBox<String> comboBox = new JComboBox<String>();
        comboBox.addItem("日表");
        LocalDate curr = LocalDate.now();
        DateTimeFormatter formate = DateTimeFormatter.ofPattern("yyyyMM");
        for (int i = 1; i <= 6; i++) {
            LocalDate dt = curr.minusMonths(i);
            comboBox.addItem(dt.format(formate));
        }
//        comboBox.setSelectedIndex(0);
        
        JLabel label1 = new JLabel("地    市");
        JLabel label2 = new JLabel("用 户 ID");
        JLabel label3 = new JLabel("用户号码");
        JLabel label4 = new JLabel("套餐ID");
        JLabel label5 = new JLabel("套餐名称");
        JLabel label6 = new JLabel("入网时间");
        JLabel label7 = new JLabel("销户时间");
        JLabel label8 = new JLabel("省内客户分群");
        leftPanel.add(comboBox);
        leftPanel.add(label1);
        leftPanel.add(label2);
        leftPanel.add(label3);
        leftPanel.add(label4);
        leftPanel.add(label5);
        leftPanel.add(label6);
        leftPanel.add(label7);
        leftPanel.add(label8);

        int[] i = { 3, 2, 4, 1 };
        initalCondition(midPanel, i);

        // buttonPanel.setLayout(flowLayout);

        final JButton addColumnButton = new JButton("增加导出字段");
        final JButton saveButton = new JButton("保存设置");
        addColumnButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addCondition(midPanel);
            }
        });
        saveButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                boolean b = getSelectComponent(midPanel);
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

    public void initalCondition(JPanel midPanel, int[] number) {
//        for (int i = 0; i < number.length; i++) {
//            MComboBox mcb = new MComboBox(number[i]);
//            JButton button1 = new JButton("删除");
//            button1.addActionListener(new ActionListener() {
//                public void actionPerformed(ActionEvent e) {
//                    midPanel.remove(mcb);
//                    midPanel.remove(button1);
//                    midPanel.updateUI();
//                    midPanel.repaint();
//                }
//            });
//            midPanel.add(mcb);
//            midPanel.add(button1);
//        }
    }

    public void addCondition(JPanel midPanel) {
//        MComboBox mcb = new MComboBox();
//        JButton button1 = new JButton("删除");
//        button1.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                midPanel.remove(mcb);
//                midPanel.remove(button1);
//                midPanel.updateUI();
//                midPanel.repaint();
//            }
//        });
//        midPanel.add(mcb);
//        midPanel.add(button1);
//        midPanel.updateUI();
//        midPanel.repaint();
    }

    public boolean getSelectComponent(JPanel panel) {
//        int count = panel.getComponentCount();
//        boolean ret = true;
//        int j = 0;
//        int[] saveIndex = new int[count];
//        String columnsql = "";
//        for (int i = 0; i < count; i++) {
//            Object obj = panel.getComponent(i);
//            if (obj instanceof MComboBox) {
//                MComboBox mcb = (MComboBox) obj;
//                String item = mcb.getSelectedItem().toString();
//                columnsql = fillColumnSql(columnsql, item);
//                saveIndex[j] = mcb.getSelectedIndex();
//                j++;
//            }
//        }
//        int[] clone = new int[j];
//        System.arraycopy(saveIndex, 0, clone, 0, j);
//        saveIndex = clone;
//        if (!cheakIsRepeat(saveIndex)) {
//            JOptionPane.showMessageDialog(null, "字段重复,请重新设置", "友情提示", JOptionPane.INFORMATION_MESSAGE);
//            ret = false;
//        } else {
////            Public.setQueryConditionIndex(saveIndex);
////            Public.setQueryConditionSql(QUERYSQL + columnsql);
//        }
        return true;
    }

    public static boolean cheakIsRepeat(int[] array) {
        HashSet<Integer> hashSet = new HashSet<Integer>();
        for (int i = 0; i < array.length; i++) {
            hashSet.add(array[i]);
        }
        if (hashSet.size() == array.length) {
            return true;
        } else {
            return false;
        }
    }

    public String fillColumnSql(String sql, String column) {
        switch (column) {
        case "套餐ID":

        case "是否移动宽带用户":

        case "停机时间":

        case "销户时间":

        case "发展渠道业绩归属部门":

        case "发展渠道ID":

        case "用户状态":

        case "省内客户分群":

        default:
            sql = sql + ", " + column;
            break;
        }
        return sql;
    }

    // public static void main(String args[]) {
    // try {
    // QueryConditionDialog dialog = new QueryConditionDialog();
    // dialog.setVisible(true);
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }

}
