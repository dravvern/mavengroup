package cn.dravvern.mwing;

import javax.swing.tree.DefaultMutableTreeNode;

public class MDefaultMutableTreeNode extends DefaultMutableTreeNode {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    protected Object txt;
    protected int menuId;

    public MDefaultMutableTreeNode(Object txt) {
        this(txt, 0);
    }
    
    public MDefaultMutableTreeNode(Object txt, int menuId) {
        super(txt);
        this.txt = txt;
        this.menuId = menuId;
    }

    public void setText(Object txt) {
        this.txt = txt;
    }

    public Object getText() {
        return txt;
    }
    
    public int getMenuId() {
        return menuId;
    }

    public void setMenuId(int menuId) {
        this.menuId = menuId;
    }
}
