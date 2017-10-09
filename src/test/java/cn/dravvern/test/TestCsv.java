package cn.dravvern.test;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.tree.TreePath;

import cn.dravvern.mwing.MDefaultMutableTreeNode;
import cn.dravvern.mwing.MDefaultTreeCellRenderer;

public class TestCsv extends javax.swing.JFrame {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    JFrame frame;

    public TestCsv() {
        try {
            UIManager.setLookAndFeel("com.jtattoo.plaf.bernstein.BernsteinLookAndFeel");
            SwingUtilities.updateComponentTreeUI(frame);
        } catch (Exception e) {
        }

        frame = new JFrame("树");
        frame.setSize(150, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        MDefaultMutableTreeNode root1 = new MDefaultMutableTreeNode("高中同学");
        MDefaultMutableTreeNode root2 = new MDefaultMutableTreeNode("初中同学");

        root1.add(new MDefaultMutableTreeNode("雅君"));
        root1.add(new MDefaultMutableTreeNode("伟旭"));
        root1.add(new MDefaultMutableTreeNode("宜群"));
        root2.add(new MDefaultMutableTreeNode("彬强"));
        root2.add(new MDefaultMutableTreeNode("小强"));

        MDefaultMutableTreeNode Root = new MDefaultMutableTreeNode(null);// 定义根节点
        Root.add(root1);// 定义二级节点
        Root.add(root2);// 定义二级节点

        final JTree tree = new JTree(Root);// 定义树
        tree.setCellRenderer(new MDefaultTreeCellRenderer()); // 设置单元格描述
        tree.setEditable(false); // 设置树是否可编辑
        tree.setRootVisible(false);// 设置树的根节点是否可视
        tree.setToggleClickCount(1);// 设置单击几次展开数节点

//        DefaultTreeCellRenderer cellRenderer = (DefaultTreeCellRenderer) tree.getCellRenderer();// 获取该树的Renderer

        // 测试事件
        tree.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2)// 双击节点
                {
                    TreePath path = tree.getSelectionPath();// 获取选中节点路径
                    MDefaultMutableTreeNode node = (MDefaultMutableTreeNode) path.getLastPathComponent();// 通过路径将指针指向该节点
                    if (node.isLeaf())// 如果该节点是叶子节点
                    {
                        // DefaultTreeModel model=(DefaultTreeModel)tree.getModel();//获取该树的模型
                        // model.removeNodeFromParent(node);//从本树删除该节点
                        node.setText("双击");// 修改该节点的文本
                        tree.repaint();// 重绘更新树
                        System.out.println(node.getText());
                    } else// 不是叶子节点
                    {
                    }

                }
            }
        });

        JScrollPane sp = new JScrollPane(tree);
        frame.getContentPane().add(sp, BorderLayout.CENTER);
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new TestCsv();
    }

}
