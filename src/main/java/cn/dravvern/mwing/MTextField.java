package cn.dravvern.mwing;

import javax.swing.JTextField;

public class MTextField extends JTextField {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private String datatype;
    
    public String getDatatype() {
        return datatype;
    }

    public void setDatatype(String datatype) {
        this.datatype = datatype;
    }

    public MTextField(String datatype) {
        super();
        this.datatype = datatype;
    }

}
