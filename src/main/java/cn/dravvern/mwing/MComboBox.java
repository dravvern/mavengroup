package cn.dravvern.mwing;

import java.util.ArrayList;
import java.util.List;

import javax.swing.JComboBox;

import cn.dravvern.util.Public;

public class MComboBox extends JComboBox<String> {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private List<String> list = new ArrayList<String>();

    public String getSelectedDataType() {
        int index = getSelectedIndex();
        if (this.list.size() > index) {
            return this.list.get(index);            
        } else {
            return null;
        }
        
    }

    public MComboBox(String[] strings) {
        this(strings, 0);
    }

    public MComboBox(String[] strings, int index) {
        super();
        for (int i = 0; i < strings.length; i++) {
            this.addItem(strings[i]);
        }
        this.setSelectedIndex(index);
    }
    
    public MComboBox(List<Object[]> oList) {
        this(oList, 0);
    }

    public MComboBox(List<Object[]> oList, int index) {
        super();
        if (oList.size() != 2 && oList.get(0).length != oList.get(1).length) {
            return;
        }
        String[] head = Public.objectsToStrings(oList.get(0));
        String[] type = Public.objectsToStrings(oList.get(1));
        
        for (int i = 0; i < head.length; i++) {
            this.addItem(head[i]);
            this.list.add(type[i]);
        }
        this.setSelectedIndex(index);
    }

    public MComboBox(String[] strings, String string) {
        super();
        for (int i = 0; i < strings.length; i++) {
            this.addItem(strings[i]);
        }
        this.setSelectedItem(string);
    }
}
