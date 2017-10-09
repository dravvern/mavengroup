package cn.dravvern.frame;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.math.BigDecimal;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.TitledBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import cn.dravvern.panel.RosterCircularPanel;
import cn.dravvern.base.Dao;
import cn.dravvern.base.UserInfo;
import cn.dravvern.mwing.MDefaultMutableTreeNode;
import cn.dravvern.mwing.MDefaultTreeCellRenderer;
import cn.dravvern.panel.FileMergePanel;
import cn.dravvern.panel.NumberBatchPanel;
import cn.dravvern.panel.NumberSinglePanel;
import cn.dravvern.util.Public;

public class MainFrame extends JFrame {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    static JPanel rightPanel;
    JScrollPane bottomScrollPanel;
    public static JTextArea processResult;

    public MainFrame() {

//        setResizable(false);
        setTitle("主窗口");
        setBounds(200, 200, 800, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        final JPanel leftPanel = new JPanel();
        leftPanel.setBackground(new Color(90, 130, 189));
        leftPanel.setLayout(new BorderLayout());
        leftPanel.setBorder(new TitledBorder(null, "", TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, null, null));
        leftPanel.setPreferredSize(new Dimension(160, 0));
        getContentPane().add(leftPanel, BorderLayout.WEST);

        MDefaultMutableTreeNode root = initalTree();
        JTree tree = createTree(root);
        leftPanel.add(tree);

        tree.addTreeSelectionListener(new MenuTreeActionListerner());

        rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout());
        getContentPane().add(rightPanel, BorderLayout.CENTER);

        bottomScrollPanel = new JScrollPane();
        bottomScrollPanel.setPreferredSize(new Dimension(0, 120));
        getContentPane().add(bottomScrollPanel, BorderLayout.SOUTH);
        processResult = new JTextArea("处理日志：\n");
        processResult.setLineWrap(true);
        bottomScrollPanel.setViewportView(processResult);

        Public.setLogFrame(processResult);
    }

    class MenuTreeActionListerner implements TreeSelectionListener {
        public void valueChanged(TreeSelectionEvent e) {
            rightPanel.removeAll();
            TreePath treePath = e.getPath();
            if (treePath.getPathCount() == 2) {
                return;
            }
            MDefaultMutableTreeNode leafNode = (MDefaultMutableTreeNode) treePath.getLastPathComponent();
//            System.out.println(leafNode.toString() + "|" + leafNode.getMenuId());
            String selectedNode = leafNode.toString();
            // String parentNode =
            // treePath.getParentPath().getLastPathComponent().toString();

            if (selectedNode.equals("名单通报")) {
                new RosterCircularPanel(rightPanel);
            } else if (selectedNode.equals("批量数据查询")) {
                new NumberBatchPanel(rightPanel);
            } else if (selectedNode.equals("单用户查询")) {
                new NumberSinglePanel(rightPanel);
            } else if (selectedNode.equals("Excel文件合并")) {
                new FileMergePanel(rightPanel);
            }
//            leafNode.setText("双击");
            
            SwingUtilities.updateComponentTreeUI(rightPanel);
        }
    }
    
    public MDefaultMutableTreeNode initalTree() {
        String nodeSql = "select * from tg_menu "
                + "where menu_id in (select parent_id from tg_menu a  "
                + "where menu_id in (select menu_id from tg_user_menu  "
                + "where user_login_name='" + UserInfo.getInstance().getUserLoginName() + "')) order by menu_id";
        String leafSql = "select * from tg_menu where menu_id in (select menu_id from tg_user_menu "
                + "where user_login_name='" + UserInfo.getInstance().getUserLoginName() + "') order by menu_id";
        if ("admin".equals(UserInfo.getInstance().getUserLoginName()) || UserInfo.getInstance().getUserRoll() == 9) {
            nodeSql = "select * from tg_menu where menu_level=1 order by menu_id ";
            leafSql = "select * from tg_menu where menu_level=2 order by menu_id ";
        }
        
        List<Object[]>  nodelist = Dao.getInstance().QuerySomeNote(nodeSql);
        List<Object[]>  leaflist = Dao.getInstance().QuerySomeNote(leafSql);
        MDefaultMutableTreeNode rNode = new MDefaultMutableTreeNode("root");
        for (int i = 0; i < nodelist.size(); i++) {
            Object[] nodeobjects = nodelist.get(i);
            int nodeMenu = ((BigDecimal) nodeobjects[0]).intValue();
            MDefaultMutableTreeNode node = new MDefaultMutableTreeNode(nodeobjects[1], nodeMenu);
            for (int j = 0; j < leaflist.size(); j++) {
                Object[] leafobjects = leaflist.get(j);
                int leafMenu = ((BigDecimal) leafobjects[0]).intValue();
                if (nodeobjects[0].equals(leafobjects[3])) {
                    node.add(new MDefaultMutableTreeNode(leafobjects[1], leafMenu));
                }
                rNode.add(node);
            }
        }
        return rNode;
    }
    
    public JTree createTree(MDefaultMutableTreeNode root) {
        DefaultTreeModel treeModel = new DefaultTreeModel(root);// 通过树结点对象创建树模型对象
        JTree tree = new JTree(treeModel);// 通过树模型对象创建树对象
        tree.setBackground(Color.WHITE);// 设置树的背景色
        tree.setRootVisible(false);// 设置不显示树的根结点，默认为显示，即true
        tree.setRowHeight(24);// 设置各结点的高度为27像素
        tree.setFont(new Font("宋体", Font.BOLD, 14));// 设置结点的字体样式
        
        MDefaultTreeCellRenderer renderer = new MDefaultTreeCellRenderer();
        renderer.setClosedIcon(null);
        renderer.setOpenIcon(null);
        tree.setCellRenderer(renderer);
        
        int count = root.getChildCount();// 获得一级结点的数量
        for (int i = 0; i < count; i++) {// 遍历树的一级结点
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) root.getChildAt(i);// 获得指定索引位置的一级结点对象
            TreePath path = new TreePath(node.getPath());// 获得结点对象的路径
            tree.expandPath(path);// 展开该结点
        }
        tree.updateUI();
        return tree;
    }

    public static void main(String[] args) {
SwingUtilities.invokeLater(new Runnable() {
            
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                Dimension screenSize = toolkit.getScreenSize();
                MainFrame mainFrame = new MainFrame();
                Dimension frameSize = mainFrame.getSize();
                if (frameSize.width > screenSize.width) {
                    frameSize.width = screenSize.width;
                }
                if (frameSize.height > screenSize.height) {
                    frameSize.height = screenSize.height;
                }
                mainFrame.setLocation((screenSize.width - frameSize.width) / 2, (screenSize.height - frameSize.height) / 2);
                mainFrame.setVisible(true);
            }
        });
    }

}